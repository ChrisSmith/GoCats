package org.collegelabs.gocats.app;

import android.app.Application;
import go.Go;
import go.libcats.Libcats;

/**
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Go.init(getApplicationContext());

        String cachePath = this.getCacheDir().getAbsolutePath();
        Libcats.Init(cachePath);
    }
}
