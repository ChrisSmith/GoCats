package org.collegelabs.gocats.app;

import android.app.Activity;
import android.os.Bundle;
import butterknife.ButterKnife;

/**
 * Used for testing small bits of code (like finding memory leaks)
 */
public class SampleActivity extends Activity {


    private CatView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new CatView(this);
        setContentView(view);

        String squareImg = "http://i.imgur.com/aZaFne4.jpg";
        String rectImg = "http://i.imgur.com/0Regu2A.jpg";

        view.setMetaData(new ImageMetaData(squareImg, "Hi", "Test", "nope", "id"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        view.RemoveCallback();
    }
}
