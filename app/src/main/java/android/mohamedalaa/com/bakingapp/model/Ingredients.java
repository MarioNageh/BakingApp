package android.mohamedalaa.com.bakingapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Mohamed on 7/17/2018.
 *
 */
/*@Entity(foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "id",
        childColumns = "childIdOne",
        onDelete = ForeignKey.CASCADE))*/
public class Ingredients implements Serializable {

    /*@PrimaryKey(autoGenerate = true)
    private int childIdOne;*/

    private float quantity;

    private String measure;

    private String ingredient;

    public Ingredients(float quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    /*public int getChildIdOne() {
        return childIdOne;
    }

    public void setChildIdOne(int childIdOne) {
        this.childIdOne = childIdOne;
    }*/

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
