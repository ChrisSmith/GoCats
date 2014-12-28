package org.collegelabs.gocats.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import go.Go;
import go.Seq;
import go.libcats.Libcats;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {

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

            final byte[] fimage = image;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = BitmapFactory.decodeByteArray(fimage, 0, fimage.length);
                    if(bmp == null)  {
                        return;
                    }

                    ImageView imageView = (ImageView) mainActivity.findViewById(R.id.imageview);
                    imageView.setImageBitmap(bmp);

                    if(mainActivity.currentImage != null){
                        mainActivity.currentImage.recycle();
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
