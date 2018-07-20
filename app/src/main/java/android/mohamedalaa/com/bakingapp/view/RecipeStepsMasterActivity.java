package android.mohamedalaa.com.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.mohamedalaa.com.bakingapp.R;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.mohamedalaa.com.bakingapp.viewmodel.RecipeStepsDetailViewModel;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class RecipeStepsMasterActivity extends AppCompatActivity {

    // todo in orient changes u have to keep the fragment master step colored ALong with fragment detail but not now
    // todo and it has to be in viewModel isa

    private static final String FRAGMENT_MANAGER_TAG_RECIPE_STEPS_DETAIL_FRAGMENT
            = "FRAGMENT_MANAGER_TAG_RECIPE_STEPS_DETAIL_FRAGMENT";

    private RecipeStepsDetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps_master);

        // setup dynamic fragment if tablet
        boolean isTablet = ! getResources().getBoolean(R.bool.is_phone);
        if (isTablet){
            tabletSetups(savedInstanceState == null);
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

                fragmentManager.beginTransaction()
                        .add(R.id.recipeStepsDetailFragmentFrameLayout, detailFragment,
                                FRAGMENT_MANAGER_TAG_RECIPE_STEPS_DETAIL_FRAGMENT)
                        .commit();
            }
        }
    }

    // ---- Public Methods

    /** For tablet only */
    public void changeCurrentStep(int currentStepIndex){
        detailFragment.changeIndexChosen(currentStepIndex);
    }

}