package android.mohamedalaa.com.bakingapp;

import android.app.Application;
import android.mohamedalaa.com.bakingapp.model.database.AppDatabase;

import timber.log.Timber;

/**
 * Created by Mohamed on 7/18/2018.
 *
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Planting timber tree
        Timber.plant(new Timber.DebugTree());
    }

    // ---- Database && Data Repository

    private AppDatabase getDatabase(){
        return AppDatabase.getInstance(this);
    }

    public DataRepository getRepository(){
        return DataRepository.getInstance(getDatabase());
    }
}
