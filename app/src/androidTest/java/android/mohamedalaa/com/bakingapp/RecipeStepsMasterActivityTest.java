package android.mohamedalaa.com.bakingapp;

import android.content.Intent;
import android.mohamedalaa.com.bakingapp.model.Ingredients;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.mohamedalaa.com.bakingapp.view.RecipeStepsMasterActivity;
import android.mohamedalaa.com.bakingapp.view.RecipeStepsMasterFragment;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed on 7/20/2018.
 *
 */
@RunWith(AndroidJUnit4.class)
public class RecipeStepsMasterActivityTest {

    @Rule
    public ActivityTestRule<RecipeStepsMasterActivity> ruleRecipeStepsMasterActivity
            = new ActivityTestRule<>(RecipeStepsMasterActivity.class, false, false);

    @Before
    public void launchActivityTheRightWay(){
        List<Ingredients> fakeIngredientList = new ArrayList<>();
        List<Steps> fakeStepList = new ArrayList<>();
        for (int i=0; i<3; i++){
            Ingredients ingredients = new Ingredients(
                    1.4f, "measure", "ingredient");
            fakeIngredientList.add(ingredients);

            Steps steps = new Steps(
                    "shortDesc", "desc", "", "");
            fakeStepList.add(steps);
        }

        Intent intent = new Intent();
        intent.putExtra(RecipeStepsMasterFragment.INTENT_KEY_INGREDIENTS_LIST,
                (Serializable) fakeIngredientList);
        intent.putExtra(RecipeStepsMasterFragment.INTENT_KEY_STEPS_LIST,
                (Serializable) fakeStepList);
        ruleRecipeStepsMasterActivity.launchActivity(intent);

        ruleRecipeStepsMasterActivity.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void checkRecyclerViewExistence(){
        Espresso.onView(ViewMatchers.withId(R.id.recipeStepsMasterFragment))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        RecyclerView stepsRecyclerView = (RecyclerView) ruleRecipeStepsMasterActivity.getActivity()
                .getSupportFragmentManager().findFragmentById(
                        R.id.recipeStepsMasterFragment).getView();

        if (stepsRecyclerView != null){
            Espresso.onView(ViewMatchers.withId(stepsRecyclerView.getId()))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

}
