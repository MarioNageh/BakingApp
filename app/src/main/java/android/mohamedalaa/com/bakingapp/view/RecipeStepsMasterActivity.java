package android.mohamedalaa.com.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.mohamedalaa.com.bakingapp.R;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.mohamedalaa.com.bakingapp.viewmodel.RecipeStepsMasterViewModel;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

import timber.log.Timber;

public class RecipeStepsMasterActivity extends AppCompatActivity {

    private static final String FRAGMENT_MANAGER_TAG_RECIPE_STEPS_DETAIL_FRAGMENT
            = "FRAGMENT_MANAGER_TAG_RECIPE_STEPS_DETAIL_FRAGMENT";

    private RecipeStepsDetailFragment detailFragment;

    private RecipeStepsMasterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps_master);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get instance of view model
        // Note this instance is not the same instance inside the fragment, since the container
        // here is the activity and there is the fragment.
        viewModel = ViewModelProviders.of(this)
                .get(RecipeStepsMasterViewModel.class);

        // setup dynamic fragment if tablet
        boolean isTablet = ! getResources().getBoolean(R.bool.is_phone);
        if (isTablet){
            tabletSetups(savedInstanceState == null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            // NavUtils.navigateUpFromSameTask(this);
            // Above approach Won't change if launched from widget ingredients list, that's why
            // we used below approach.
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            android.app.TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    /** {@link #onCreate(Bundle)} */

    @SuppressWarnings("unchecked")
    private void tabletSetups(boolean savedInstanceStateIsNull) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceStateIsNull){
            detailFragment = new RecipeStepsDetailFragment();

            List<Steps> stepsList = (List<Steps>) getIntent()
                    .getSerializableExtra(RecipeStepsMasterFragment.INTENT_KEY_STEPS_LIST);

            detailFragment.setStepList(stepsList);
            detailFragment.setIndexChosen(viewModel.tabletAdapterIndexChosen);

            fragmentManager.beginTransaction()
                    .add(R.id.recipeStepsDetailFragmentFrameLayout, detailFragment,
                            FRAGMENT_MANAGER_TAG_RECIPE_STEPS_DETAIL_FRAGMENT)
                    .commit();
        }else {
            detailFragment = (RecipeStepsDetailFragment) fragmentManager.findFragmentByTag(
                    FRAGMENT_MANAGER_TAG_RECIPE_STEPS_DETAIL_FRAGMENT);

            // To ensure that it is not null
            if (detailFragment == null){
                detailFragment = new RecipeStepsDetailFragment();

                List<Steps> stepsList = (List<Steps>) getIntent()
                        .getSerializableExtra(RecipeStepsMasterFragment.INTENT_KEY_STEPS_LIST);

                detailFragment.setStepList(stepsList);
                detailFragment.setIndexChosen(viewModel.tabletAdapterIndexChosen);

                fragmentManager.beginTransaction()
                        .add(R.id.recipeStepsDetailFragmentFrameLayout, detailFragment,
                                FRAGMENT_MANAGER_TAG_RECIPE_STEPS_DETAIL_FRAGMENT)
                        .commit();
            }else {
                RecipeStepsMasterFragment masterFragment = (RecipeStepsMasterFragment)
                        fragmentManager.findFragmentById(R.id.recipeStepsMasterFragment);
                masterFragment.setCurrentStepIndexInsideAdapter(viewModel.tabletAdapterIndexChosen);
                //changeCurrentStep();
            }
        }
    }

    // ---- Public Methods

    /** For tablet only */
    public void changeCurrentStep(int currentStepIndex){
        detailFragment.changeIndexChosen(currentStepIndex);
        viewModel.tabletAdapterIndexChosen = currentStepIndex;
    }

}