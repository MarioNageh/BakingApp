package android.mohamedalaa.com.bakingapp.model;

import java.io.Serializable;

/**
 * Created by Mohamed on 7/17/2018.
 *
 */
@SuppressWarnings("SameParameterValue")
public class Ingredients implements Serializable {

    private float quantity;

    private String measure;

    private String ingredient;

    public Ingredients(float quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getmeasure() {
        return measure;
    }

    public void setmeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
