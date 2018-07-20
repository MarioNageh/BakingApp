package android.mohamedalaa.com.bakingapp.utils;

import android.mohamedalaa.com.bakingapp.R;
import android.mohamedalaa.com.bakingapp.model.Recipe;

import java.util.List;

/**
 * Created by Mohamed on 7/17/2018.
 *
 * Note
 * 1- Always use opt instead of get to handle any error that would be thrown from null fields.
 */
public class RecipeUtils {

    private static final String RECIPE_NAME_NUTELLA_PIE = "Nutella Pie";
    private static final String RECIPE_NAME_BROWNIES = "Brownies";
    private static final String RECIPE_NAME_YELLOW_CAKE = "Yellow Cake";
    private static final String RECIPE_NAME_CHEESECAKE = "Cheesecake";

    public static List<Recipe> fillImagesInsideRecipeList(List<Recipe> list){
        for (Recipe recipe : list){
            switch (recipe.getName()){
                case RECIPE_NAME_NUTELLA_PIE:
                    recipe.setImageDrawableRes(R.drawable.ic_nutella_pie);
                    break;
                case RECIPE_NAME_BROWNIES:
                    recipe.setImageDrawableRes(R.drawable.ic_brownies);
                    break;
                case RECIPE_NAME_YELLOW_CAKE:
                    recipe.setImageDrawableRes(R.drawable.ic_yellow_cake);
                    break;
                case RECIPE_NAME_CHEESECAKE:
                    recipe.setImageDrawableRes(R.drawable.ic_cheesecake);
                    break;
                default:
                    recipe.setImageDrawableRes(R.drawable.ic_base_recipe_image);
                    break;
            }
        }

        return list;
    }

}
