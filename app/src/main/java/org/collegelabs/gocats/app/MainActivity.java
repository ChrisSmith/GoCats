package org.collegelabs.gocats.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import go.libcats.Libcats;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity  {

    @InjectView(R.id.viewpager) public ViewPager viewPager;
    @InjectView(R.id.progressSpinner) public ProgressBar progressSpinner;

    private CatPagerAdapter catPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        String lastId = savedInstanceState != null ? savedInstanceState.getString("lastId", null) : null;
        catPagerAdapter = new CatPagerAdapter(lastId);
        viewPager.setAdapter(catPagerAdapter);
        catPagerAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if(catPagerAdapter.getCount() != 0){
                    progressSpinner.setVisibility(View.GONE);
                    catPagerAdapter.unregisterDataSetObserver(this);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        catPagerAdapter.StartLoading();
    }

    @Override
    protected void onStop() {
        super.onStop();
        catPagerAdapter.StopLoading();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        int item = viewPager.getCurrentItem() - 1;
        String lastId = catPagerAdapter.GetId(item);
        savedInstanceState.putString("lastId", lastId);
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
