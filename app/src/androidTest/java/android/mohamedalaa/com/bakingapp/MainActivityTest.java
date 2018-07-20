package android.mohamedalaa.com.bakingapp;

import android.mohamedalaa.com.bakingapp.view.MainActivity;
import android.mohamedalaa.com.bakingapp.viewmodel.MainViewModel;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

/**
 * Created by Mohamed on 7/19/2018.
 *
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    // --- Private Variables

    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<MainActivity> testRuleMainActivity
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        idlingResource = testRuleMainActivity.getActivity()
                .getViewModel().getIdlingResource();

        IdlingRegistry.getInstance().register(idlingResource);
    }

    /**
     * test if no connection that button fab inside noInternetConnectionView is clickable.
     *
     * Notes
     * only start this class's all tests after either 1st install of app or after clearing all
     * data for this app in settings, so that we ensure database is empty.
     */
    @Test
    public void noInternetConnection_fabIsClickable(){
        MainActivity mainActivity = testRuleMainActivity.getActivity();
        MainViewModel viewModel = mainActivity.getViewModel();

        boolean noInternetConnection = viewModel.recipeViewsVisibilities
                .get(MainViewModel.SHOW_NO_INTERNET_CONNECTION_VIEW);

        if (noInternetConnection){
            Espresso.onView(ViewMatchers.withId(R.id.fab))
                    .check(ViewAssertions.matches(ViewMatchers.isClickable()));

            Timber.v("if statement of -> noInternetConnection -> Entered");
        }
    }

    /**
     * test if there is connection that recyclerView or empty view is displayed and if it is
     * recyclerView check click of item will launch activity.
     */
    @Test
    public void thereIsInternetConnection_emptyViewCheckDisplayed_or_recyclerViewItemClickable(){
        MainActivity mainActivity = testRuleMainActivity.getActivity();
        MainViewModel viewModel = mainActivity.getViewModel();
        String noRecipesFoundEmptyViewText = mainActivity.getResources()
                .getString(R.string.no_recipes_found);

        boolean showEmptyView = viewModel.recipeViewsVisibilities
                .get(MainViewModel.SHOW_EMPTY_VIEW);
        boolean showRecyclerView = viewModel.recipeViewsVisibilities
                .get(MainViewModel.SHOW_RECYCLER_VIEW);

        if (showEmptyView){
            Espresso.onView(ViewMatchers.withId(R.id.emptyViewTextView))
                    .check(ViewAssertions.matches(ViewMatchers
                            .withText(noRecipesFoundEmptyViewText)));

            Timber.v("if statement of -> showEmptyView -> Entered");
        }else if (showRecyclerView){
            Espresso.onView(ViewMatchers.withId(R.id.recipeRecyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                            ViewActions.click()));

            // Check if item click launched the next activity
            // used Intents.init() instead of @Rule intent as we have as well idling resources
            Intents.init();
            Intents.intended(
                    IntentMatchers.isInternal());

            Timber.v("if statement of -> showRecyclerView -> Entered");
        }
    }

    @After
    public void unregisterIdlingResource(){
        if (idlingResource != null){
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }

}
