package org.collegelabs.gocats.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import go.libcats.Libcats;

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

    private void setCallback(){
        Log.d(CatView.class.getSimpleName(), "creating callback for: "+metaData.url);

        if(callback != null){
            callback.Close();
            callback = null;
        }

        callback = Libcats.CreateImageCallback(new ImgCallback(this), getWidth(), getHeight(), metaData.id, metaData.url);
    }

    private static class ImgCallback extends Libcats.ImageCallback.Stub {

        private WeakReference<CatView> view;

        public ImgCallback(CatView view){
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


    }
}
