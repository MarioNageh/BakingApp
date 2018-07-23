package android.mohamedalaa.com.bakingapp.model.retrofit;

import android.mohamedalaa.com.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mohamed on 7/18/2018.
 *
 * VIP NOTE
 * API End Point is fixed and doesn't have IMAGES for the recipes
 * so that's why i provided fixed images in drawable res.
 *
 */
public interface RetrofitApiInterface {

    @GET("baking.json")
    Call<List<Recipe>> getAllRecipes();

}
