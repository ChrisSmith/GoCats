package org.collegelabs.gocats.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.*;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import go.libcats.Libcats;
import timber.log.Timber;

import java.lang.ref.WeakReference;


public class CatView extends RelativeLayout implements View.OnClickListener {

    @InjectView(R.id.textview) public TextView textView;
    @InjectView(R.id.progressSpinner) public ProgressBar progressSpinner;
    @InjectView(R.id.imageview) ImageView imageView;

    private ImageInfo currentImage = null;
    private ImageMetaData metaData;
    private Libcats.ImageCallbackToken callback;

    public CatView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater layoutInflater  = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.pager_view, this);
        ButterKnife.inject(this, view);

        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.textview){
            if(metaData != null){
                try{
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(metaData.permalink));
                    getContext().startActivity(i);
                }catch(Exception e){
                    Toast.makeText(getContext(), "Unable to open browser", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void ImageReceived(ImageInfo info){
        if(this.metaData == null || !info.id.equals(metaData.id)){
            // wrong image
            if(info.image != null){
                Timber.d("recycling lost image");
                info.image.recycle();
                info.image = null;
            }
            return;
        }

        textView.setText(metaData.title + " - " + metaData.author);

        if(info.image == null){
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_warning));
        }else{
            imageView.setImageBitmap(info.image);
        }

        currentImage = info;

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0, 1);
        ObjectAnimator fadeIn2 = ObjectAnimator.ofFloat(textView, View.ALPHA, 0, 1);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(progressSpinner, View.ALPHA, 1, 0);

        set.setDuration(500);
        set.playTogether(fadeIn, fadeIn2, fadeOut);
        set.start();
    }

    public void setMetaData(ImageMetaData metaData) {
        if(this.metaData != null && this.metaData.id.equals(metaData.id)){
            return;
        }

        progressSpinner.setAlpha(1);
        textView.setAlpha(0);

        this.metaData = metaData;

        imageView.setImageDrawable(null);

        if (currentImage != null && currentImage.image != null) {
            currentImage.image.recycle();
            currentImage.image = null;
        }
        this.currentImage = null;

        int width = getWidth();
        int height = getHeight();

        if(width == 0 || height == 0){
            post(new Runnable() {
                @Override
                public void run() {
                    ImageMetaData metaData1 = CatView.this.metaData;
                    if(metaData1 == null){
                        return;
                    }
                    CatView.this.setCallback();
                }
            });
        }else{
            setCallback();
        }
    }

    private void setCallback(final Libcats.ImageCallbackToken newCallback, String id){
        if(!id.equals(metaData.id)){
            // whelp, just cancel it
            Timber.d("Cancelling old callback");
            BaseApplication.submit(new Runnable() {
                @Override
                public void run() {
                    newCallback.Close();
                }
            });
        } else {
            callback = newCallback;
        }
    }

    public void RemoveCallback(){
        Timber.d("removing callback");
        final Libcats.ImageCallbackToken oldCallback = callback;
        if(oldCallback == null){
            return;
        }
        callback = null;
        BaseApplication.submit(new Runnable() {
            @Override
            public void run() {
                oldCallback.Close();
            }
        });
    }

    private void setCallback(){
        Timber.d("creating callback for: "+metaData.url);
        long start = System.currentTimeMillis();

        Libcats.ImageCallbackToken oldCallback = callback;
        callback = null;

        BaseApplication.submit(new CallbackRunner(this, oldCallback, getWidth(), getHeight(), metaData));

        Timber.d("submitted to thread: "+(System.currentTimeMillis() - start)+" ms");
    }

    private static class CallbackRunner implements Runnable {

        private WeakReference<CatView> viewRef;
        private final Libcats.ImageCallbackToken callback;
        private final int width;
        private final int height;
        private final ImageMetaData metaData;

        public CallbackRunner(CatView view, Libcats.ImageCallbackToken callback, int width, int height, ImageMetaData metaData){
            this.callback = callback;
            this.width = width;
            this.height = height;
            this.metaData = metaData;
            this.viewRef = new WeakReference<CatView>(view);
        }

        @Override
        public void run() {
            if(this.callback != null){
                long start = System.currentTimeMillis();
                this.callback.Close();
                Timber.d("callback.Close(): "+(System.currentTimeMillis() - start)+" ms");
            }

            final CatView view = viewRef.get();
            if(view == null){
                 return;
            }

            long start = System.currentTimeMillis();
            final Libcats.ImageCallbackToken newCallback = Libcats.CreateImageCallback(new ImgCallback(view, width, height), width, height, metaData.id, metaData.url);
            Timber.d("CreateImageCallback: "+(System.currentTimeMillis() - start)+" ms");

            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setCallback(newCallback, metaData.id);
                }
            });
        }
    }


    private static class ImgCallback extends Libcats.ImageCallback.Stub {

        private WeakReference<CatView> view;
        private final int width;
        private final int height;

        public ImgCallback(CatView view, int width, int height){
            this.width = width;
            this.height = height;
            this.view = new WeakReference<CatView>(view);
        }

        @Override
        public void ImageFailed(String id) {
            ImageReceived(null, id);
        }

        @Override
        public void ImageReceived(byte[] image, String id) {
            final CatView catView = this.view.get();
            if(catView == null){
                return;
            }

            Bitmap bmp = null;
            if(image != null){
                bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            final ImageInfo info = new ImageInfo(id, bmp);

            catView.post(new Runnable() {
                @Override
                public void run() {
                    catView.ImageReceived(info);
                }
            });
        }

        @Override
        public void RawImageReceived(byte[] image, String id){
            final CatView catView = this.view.get();
            if(catView == null){
                return;
            }

            Bitmap bmp = null;
            if(image != null){
                long start = System.currentTimeMillis();
                bmp = decodeSampledBitmap(image, width, height);
                image = null;

                if(bmp != null){
                    Timber.d("decodeSampledBitmap ("+bmp.getWidth()+"x"+bmp.getHeight()+" "+getSize(bmp)+" ): "+(System.currentTimeMillis() - start)+" ms");

                    start = System.currentTimeMillis();
                    bmp = centerCropBitmap(bmp, width, height);

                    Timber.d("centerCropBitmap ("+bmp.getWidth()+"x"+bmp.getHeight()+" "+getSize(bmp)+" ): to fit " + width + "x" + height + " "+(System.currentTimeMillis() - start)+" ms");
                }
            }
            final ImageInfo info = new ImageInfo(id, bmp);

            catView.post(new Runnable() {
                @Override
                public void run() {
                    catView.ImageReceived(info);
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getSize(Bitmap bmp){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            return sizeof_fmt(bmp.getAllocationByteCount());
        }   else {
            return sizeof_fmt(bmp.getByteCount());
        }
    }

    public static Bitmap centerCropBitmap(Bitmap bm, int newWidth, int newHeight) {
        if(bm == null || (bm.getWidth() == newWidth && bm.getHeight() == newHeight)){
            return bm;
        }

        Matrix matrix = new Matrix();

        float scale;
        float dx = 0, dy = 0;

        int bitmapWidth = bm.getWidth();
        int bitmapHeight = bm.getHeight();

        if (bitmapWidth * newHeight > newWidth * bitmapHeight) {
            scale = (float) newHeight / (float) bitmapHeight;
            dx = (newWidth - bitmapWidth * scale) * 0.5f;
        } else {
            scale = (float) newWidth / (float) bitmapWidth;
            dy = (newHeight - bitmapHeight * scale) * 0.5f;
        }

        // final bitmap is exact size (1080x1776 7 Mb on nexus 5)
        Bitmap cropped = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(cropped);
        Paint paint = new Paint();

        matrix.setScale(scale, scale);
        matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

        c.drawBitmap(bm, matrix, paint);
        c.setBitmap(null);
        bm.recycle();

        return cropped;
    }

    public static Bitmap decodeSampledBitmap(byte[] image, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(image, 0, image.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(image, 0, image.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String sizeof_fmt(int num) {
        String[] byteSizes = new String[]{
            "", "K", "M", "G", "T", "P", "E", "Z",
        };

        String suffix = "b";

        for(String unit : byteSizes){
            if (num < 1024.0) {
                return String.format("%d %s%s", num, unit, suffix);
            }
            num /= 1024.0;
        }

        return String.format("%d %s%s", num, "Y", suffix);
    }
}
