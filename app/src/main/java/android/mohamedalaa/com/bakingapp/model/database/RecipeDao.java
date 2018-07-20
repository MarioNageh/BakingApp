package android.mohamedalaa.com.bakingapp.model.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.mohamedalaa.com.bakingapp.model.Recipe;

import java.util.List;

/**
 * Created by Mohamed on 7/18/2018.
 *
 */
@Dao
public interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Recipe> list);

    @Query("SELECT * FROM recipeTable")
    List<Recipe> getAll();

}
