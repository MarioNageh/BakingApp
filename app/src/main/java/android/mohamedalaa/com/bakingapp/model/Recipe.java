package android.mohamedalaa.com.bakingapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

/**
 * Created by Mohamed on 7/17/2018.
 *
 */
@Entity(tableName = "recipeTable")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private List<Ingredients> ingredients;

    private List<Steps> steps;

    private int servings;

    private int imageDrawableRes;

    public Recipe(int id, String name, List<Ingredients> ingredients, List<Steps> steps, int servings, int imageDrawableRes) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imageDrawableRes = imageDrawableRes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getImageDrawableRes() {
        return imageDrawableRes;
    }

    public void setImageDrawableRes(int imageDrawableRes) {
        this.imageDrawableRes = imageDrawableRes;
    }
}
