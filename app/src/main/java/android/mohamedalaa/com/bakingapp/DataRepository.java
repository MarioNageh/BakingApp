package android.mohamedalaa.com.bakingapp;

import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.model.database.AppDatabase;

import java.util.List;

/**
 * Created by Mohamed on 7/18/2018.
 *
 * Usage
 * 1- for ease of access to database.
 *
 * Notes
 * 1- any method here must be run in any thread other than Main UI Thread.
 */
public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
    }

    static DataRepository getInstance(final AppDatabase database){
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }

        return sInstance;
    }

    public void insertRecipeList(List<Recipe> recipeList){
        mDatabase.getRecipeDao().insert(recipeList);
    }

    public List<Recipe> getAllRecipeList(){
        return mDatabase.getRecipeDao().getAll();
    }

}
