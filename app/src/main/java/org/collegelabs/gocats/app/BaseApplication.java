package org.collegelabs.gocats.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.util.Log;
import go.Go;
import go.libcats.Libcats;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        formUri = "http://localhost/reportpath"
)
public class BaseApplication extends Application {
    
    private static String TAG = Application.class.getSimpleName();

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
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onTrimMemory (int level){
        super.onTrimMemory(level);

        if(BuildConfig.DEBUG){
            Log.d(TAG, "[memory trim] level: " + level);
        }

        if(level <= TRIM_MEMORY_RUNNING_MODERATE){
            Log.d(TAG, "[memory trim] TRIM_MEMORY_RUNNING_MODERATE");

        }else if(level <= TRIM_MEMORY_RUNNING_LOW){
            Log.d(TAG, "[memory trim] TRIM_MEMORY_RUNNING_LOW");

        }else if(level <= TRIM_MEMORY_RUNNING_CRITICAL){
            //onLowMemory is about to be called and background processes killed
            //this is just us being nice to the system since we have priority
            Log.d(TAG, "[memory trim] TRIM_MEMORY_RUNNING_CRITICAL");

        }else if(level <= TRIM_MEMORY_UI_HIDDEN){ //TODO use for general cleanup? only useful in 14+
            Log.d(TAG, "[memory trim] TRIM_MEMORY_UI_HIDDEN");

        }else if(level <= TRIM_MEMORY_BACKGROUND){
            Log.d(TAG, "[memory trim] TRIM_MEMORY_BACKGROUND");

        }else if(level <= TRIM_MEMORY_MODERATE){
            Log.d(TAG, "[memory trim] TRIM_MEMORY_MODERATE");

        }else if(level <= TRIM_MEMORY_COMPLETE){
            Log.d(TAG, "[memory trim] TRIM_MEMORY_COMPLETE");
        }
    }
}
