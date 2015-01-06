package org.collegelabs.gocats.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import go.Go;
import go.libcats.Libcats;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import timber.log.Timber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ReportsCrashes(
        formKey = "",
        formUri = BuildConfig.ACRA_ENDPOINT,
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin=BuildConfig.ACRA_USER,
        formUriBasicAuthPassword=BuildConfig.ACRA_PASS

)
public class BaseApplication extends Application {

    // Background thread to run tasks that don't need to be on the ui thread
    private static final ExecutorService mFogettablePool = Executors.newFixedThreadPool(1);

    public static void submit(Runnable action){
        mFogettablePool.submit(action);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);

        Go.init(getApplicationContext());

        String cachePath = this.getCacheDir().getAbsolutePath();
        Libcats.Init(cachePath);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Libcats.DisableDebugLogging();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onTrimMemory (int level){
        super.onTrimMemory(level);

        Timber.d("[memory trim] level: " + level);

        if(level <= TRIM_MEMORY_RUNNING_MODERATE){
            Timber.d("[memory trim] TRIM_MEMORY_RUNNING_MODERATE");

        }else if(level <= TRIM_MEMORY_RUNNING_LOW){
            Timber.d("[memory trim] TRIM_MEMORY_RUNNING_LOW");

        }else if(level <= TRIM_MEMORY_RUNNING_CRITICAL){
            //onLowMemory is about to be called and background processes killed
            //this is just us being nice to the system since we have priority
            Timber.d("[memory trim] TRIM_MEMORY_RUNNING_CRITICAL");

        }else if(level <= TRIM_MEMORY_UI_HIDDEN){ //TODO use for general cleanup? only useful in 14+
            Timber.d("[memory trim] TRIM_MEMORY_UI_HIDDEN");

        }else if(level <= TRIM_MEMORY_BACKGROUND){
            Timber.d("[memory trim] TRIM_MEMORY_BACKGROUND");

        }else if(level <= TRIM_MEMORY_MODERATE){
            Timber.d("[memory trim] TRIM_MEMORY_MODERATE");

        }else if(level <= TRIM_MEMORY_COMPLETE){
            Timber.d("[memory trim] TRIM_MEMORY_COMPLETE");
        }
    }
}
