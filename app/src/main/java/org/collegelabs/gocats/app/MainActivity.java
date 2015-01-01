package org.collegelabs.gocats.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import go.libcats.Libcats;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity implements View.OnClickListener {

    private ProgressBar progressBar;
    private Libcats.CallbackToken callbackToken;
    private ImageInfo currentImage = null;
    private TextView textView;

    private static class ImageInfo{
        public String author, url, title, permalink;
        public Bitmap image;
    }

    private static class ImgCallback extends Libcats.ImageCallback.Stub {

        private WeakReference<MainActivity> activity;

        public ImgCallback(MainActivity activity){
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void ImageReceived(byte[] image, String url, String title, String author, String permalink) {
            final MainActivity mainActivity = this.activity.get();
            if(mainActivity == null){
                return;
            }

            final Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            if(bmp == null)  {
                return;
            }

            final ImageInfo info = new ImageInfo();
            info.author = author;
            info.image = bmp;
            info.title = title;
            info.url = url;
            info.permalink = permalink;

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mainActivity.progressBar.getVisibility() == View.VISIBLE){
                        mainActivity.progressBar.setVisibility(View.GONE);
                    }

                    mainActivity.textView.setText(info.title + " - " + info.author);

                    ImageView imageView = (ImageView) mainActivity.findViewById(R.id.imageview);

                    imageView.setImageBitmap(bmp);

                    final ImageInfo oldImage = mainActivity.currentImage;
                    if(oldImage != null){
                        oldImage.image.recycle();
                    }
                    mainActivity.currentImage = info;
                }
            });
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressSpinner);

        textView = (TextView) findViewById(R.id.textview);
        textView.setOnClickListener(this);
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
        Log.d(this.getClass().getSimpleName(), "onStart");
        closeCallback();
        this.callbackToken = Libcats.CreateImageCallback(new ImgCallback(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "onStop");
        closeCallback();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.textview){
            if(currentImage != null){
                try{
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(currentImage.permalink));
                    startActivity(i);
                }catch(Exception e){
                    Toast.makeText(this, "Unable to open browser", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
