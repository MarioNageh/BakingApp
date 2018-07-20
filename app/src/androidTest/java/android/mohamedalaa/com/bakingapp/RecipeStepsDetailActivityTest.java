package android.mohamedalaa.com.bakingapp;

import android.content.Intent;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.mohamedalaa.com.bakingapp.view.RecipeStepsDetailActivity;
import android.mohamedalaa.com.bakingapp.view.RecipeStepsDetailFragment;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed on 7/21/2018.
 *
 * Note
 * we can as well test this activity on tablet.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeStepsDetailActivityTest {

    private static final String FAKE_VIDEO_URL
            = "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4";

    @Rule
    public ActivityTestRule<RecipeStepsDetailActivity> ruleRecipeStepsDetailActivity
            = new ActivityTestRule<>(RecipeStepsDetailActivity.class, false, false);

    @Before
    public void launchActivityWithFakeIntentExtras(){
        List<Steps> fakeStepList = new ArrayList<>();
        for (int i=0; i<3; i++){
            Steps steps = new Steps(
                    "shortDesc", "desc", FAKE_VIDEO_URL, "");
            fakeStepList.add(steps);
        }

        Intent intent = new Intent();
        intent.putExtra(RecipeStepsDetailFragment.INTENT_KEY_STEPS_LIST,
                (Serializable) fakeStepList);
        intent.putExtra(RecipeStepsDetailFragment.INTENT_KEY_STEP_INDEX_CHOSEN,
                0);
        ruleRecipeStepsDetailActivity.launchActivity(intent);

        ruleRecipeStepsDetailActivity.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void checkSimpleExoPlayerViewExistence(){
        Espresso.onView(ViewMatchers.withId(R.id.recipeStepsDetailFragment))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.simpleExoPlayerView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

}
