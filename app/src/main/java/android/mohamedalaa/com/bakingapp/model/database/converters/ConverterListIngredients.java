package android.mohamedalaa.com.bakingapp.model.database.converters;

import android.arch.persistence.room.TypeConverter;
import android.mohamedalaa.com.bakingapp.model.Ingredients;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Mohamed on 7/18/2018.
 *
 */
public class ConverterListIngredients {

    @TypeConverter
    public static List<Ingredients> toListIngredients(String jsonString){
        List<Ingredients> ingredientsList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.optJSONObject(i);

                String jsonStringItem = jsonObject.toString();
                Timber.v(jsonStringItem);
                Gson gson = new Gson();
                Ingredients ingredient = gson.fromJson(jsonStringItem, Ingredients.class);

                ingredientsList.add(ingredient);
            }
        } catch (JSONException e) {
            Timber.v(e.getMessage());
        }

        return ingredientsList;
    }

    @TypeConverter
    public static String toString(List<Ingredients> ingredientsList){
        Gson gson = new Gson();
        return gson.toJson(ingredientsList);
    }

}
