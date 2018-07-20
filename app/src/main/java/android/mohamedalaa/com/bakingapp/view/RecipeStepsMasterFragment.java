package android.mohamedalaa.com.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.mohamedalaa.com.bakingapp.R;
import android.mohamedalaa.com.bakingapp.databinding.FragmentRecipeStepsMasterBinding;
import android.mohamedalaa.com.bakingapp.model.Ingredients;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.mohamedalaa.com.bakingapp.view.adapters.StepsRecyclerViewAdapter;
import android.mohamedalaa.com.bakingapp.viewmodel.RecipeStepsMasterViewModel;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed on 7/19/2018.
 *
 * Notes
 * Since ArrayList implements Serializable, and List<Ingredients> is actually arrayList
 * then we can pass and get that list through activities as any Serializable Object.
 *
 * We Saved value of both lists in viewModel since it survives configuration changes.
 */
public class RecipeStepsMasterFragment extends Fragment implements
        StepsRecyclerViewAdapter.StepsAdapterListener {

    // ---- Constants

    public static final String INTENT_KEY_INGREDIENTS_LIST = "INTENT_KEY_INGREDIENTS_LIST";
    public static final String INTENT_KEY_STEPS_LIST = "INTENT_KEY_STEPS_LIST";

    // --- Private Variables

    private FragmentRecipeStepsMasterBinding binding;

    private RecipeStepsMasterViewModel viewModel;

    private StepsRecyclerViewAdapter adapter;

    public RecipeStepsMasterFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_recipe_steps_master, container, false);
        View view = binding.getRoot();

        // get view model instance
        viewModel = ViewModelProviders.of(this).get(RecipeStepsMasterViewModel.class);

        // get ingredients and steps list
        receiveIntentValues();

        // setup the adapter of the recycler view
        setupAdapter();

        // setup xml views
        setupXmlViews();

        return view;
    }

    /** {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} */

    @SuppressWarnings("unchecked")
    private void receiveIntentValues(){
        // Case orientation change, no need to retrieve data from getIntent();
        if (viewModel.ingredientsList != null && viewModel.stepsList != null){
            return;
        }

        if (getActivity() != null){
            Intent intent = getActivity().getIntent();

            if (intent == null){
                return;
            }

            if (intent.hasExtra(INTENT_KEY_INGREDIENTS_LIST)
                    && intent.hasExtra(INTENT_KEY_STEPS_LIST)){
                viewModel.ingredientsList = (List<Ingredients>) intent
                        .getSerializableExtra(INTENT_KEY_INGREDIENTS_LIST);

                viewModel.stepsList = (List<Steps>) intent
                        .getSerializableExtra(INTENT_KEY_STEPS_LIST);
            }
        }
    }

    private void setupAdapter(){
        if (viewModel.adapterShortDescriptionList == null
                && viewModel.adapterFullDescriptionList == null){
            List<String> shortDescriptionList = new ArrayList<>();
            List<String> fullDescriptionList = new ArrayList<>();

            // Ingredients string
            shortDescriptionList.add(getString(R.string.ingredients));
            fullDescriptionList.add(getIngredientsListAsString());
            // Steps Strings
            for (Steps step : viewModel.stepsList){
                shortDescriptionList.add(step.getShortDescription());
                fullDescriptionList.add(step.getDescription());
            }

            viewModel.adapterShortDescriptionList = shortDescriptionList;
            viewModel.adapterFullDescriptionList = fullDescriptionList;
        }

        adapter = new StepsRecyclerViewAdapter(
                getContext(),
                viewModel.adapterShortDescriptionList,
                viewModel.adapterFullDescriptionList,
                this);
    }

    private void setupXmlViews() {
        // -- Setup Recycler View
        boolean isTablet = ! getResources().getBoolean(R.bool.is_phone);
        boolean isPortrait = getResources().getBoolean(R.bool.is_portrait);
        boolean isTabletAndPortrait = isTablet && isPortrait;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(),
                isTabletAndPortrait ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL,
                false);
        binding.stepsRecyclerView.setLayoutManager(linearLayoutManager);
        // adapter has already been initialized.
        binding.stepsRecyclerView.setAdapter(adapter);
    }

    // --- Private helper methods

    private String getIngredientsListAsString(){
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<viewModel.ingredientsList.size(); i++){
            Ingredients ingredient = viewModel.ingredientsList.get(i);
            builder.append(String.valueOf((i + 1)));
            builder.append(". ");
            builder.append(ingredient.getIngredient());
            builder.append(" (");
            builder.append(ingredient.getQuantity());
            builder.append("  ");
            builder.append(ingredient.getmeasure());
            builder.append(")");

            // make new line only if it is not the last ingredient.
            if (i != viewModel.ingredientsList.size() - 1){
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    // ---- Implemented Steps Adapter Listener Methods

    @Override
    public void onItemClick(int stepIndex) {
        Intent intent = new Intent(getContext(), RecipeStepsDetailActivity.class);

        intent.putExtra(RecipeStepsDetailFragment.INTENT_KEY_STEPS_LIST,
                ((Serializable) (viewModel.stepsList == null ? new ArrayList<>() : viewModel.stepsList)));
        intent.putExtra(RecipeStepsDetailFragment.INTENT_KEY_STEP_INDEX_CHOSEN, stepIndex);

        startActivity(intent);
    }

    @Override
    public void tabletOnItemClick(int currentStepIndex) {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null && fragmentActivity instanceof RecipeStepsMasterActivity){
            ((RecipeStepsMasterActivity) fragmentActivity)
                    .changeCurrentStep(currentStepIndex);
        }
    }

}
