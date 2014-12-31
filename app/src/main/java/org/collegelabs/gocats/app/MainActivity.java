package org.collegelabs.gocats.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import go.libcats.Libcats;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {

    private ProgressBar progressBar;
    private Libcats.CallbackToken callbackToken;
    private Bitmap currentImage = null;

    private static class ImgCallback extends Libcats.ImageCallback.Stub {

        private WeakReference<MainActivity> activity;

        public ImgCallback(MainActivity activity){
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void ImageReceived(byte[] image) {
            final MainActivity mainActivity = this.activity.get();
            if(mainActivity == null){
                return;
            }

            final Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            if(bmp == null)  {
                return;
            }

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mainActivity.progressBar.getVisibility() == View.VISIBLE){
                        mainActivity.progressBar.setVisibility(View.GONE);
                    }

                    ImageView imageView = (ImageView) mainActivity.findViewById(R.id.imageview);

                    Resources resources = mainActivity.getResources();
                    Drawable currentDrawable = imageView.getDrawable();
                    currentDrawable = currentDrawable != null
                            ? currentDrawable
                            : new ColorDrawable(resources.getColor(android.R.color.darker_gray));

                    Drawable[] layers = {currentDrawable, new BitmapDrawable(resources, bmp)};
                    TransitionDrawable transition = new TransitionDrawable(layers);
                    transition.setCrossFadeEnabled(true);
                    transition.startTransition(300);

                    imageView.setImageDrawable(transition);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    final Bitmap oldBitmap = mainActivity.currentImage;
                    if(oldBitmap != null){
                        mainActivity.progressBar.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                oldBitmap.recycle();
                            }
                        }, 400);
                    }
                    mainActivity.currentImage = bmp;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressSpinner);
    }

    private void closeCallback(){
        if(this.callbackToken != null){
            this.callbackToken.Close();
            this.callbackToken = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        closeCallback();
        this.callbackToken = Libcats.CreateImageCallback(new ImgCallback(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCallback();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
