package android.mohamedalaa.com.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.mohamedalaa.com.bakingapp.R;
import android.mohamedalaa.com.bakingapp.databinding.ActivityMainBinding;
import android.mohamedalaa.com.bakingapp.model.Ingredients;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.mohamedalaa.com.bakingapp.view.adapters.RecipeRecyclerViewAdapter;
import android.mohamedalaa.com.bakingapp.viewmodel.MainViewModel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        RecipeRecyclerViewAdapter.RecipesAdapterListener{

    // --- Constants

    private static final int RECIPE_SPAN_COUNT_GRID_LAYOUT = 2;
    private static final int RECIPE_SPAN_COUNT_GRID_LAYOUT_TABLET_AND_LANDSCAPE = 3;

    // --- Private Variables

    private ActivityMainBinding binding;

    private MainViewModel viewModel;

    private RecipeRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);

        setupXmlViews();

        observeViewModelLiveDataVariables();
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.initialSetupsInOnResume();
    }

    /** {@link #onCreate(Bundle)} */
    
    private void setupXmlViews(){
        // Setup Recycler View
        RecyclerView.LayoutManager layoutManager;
        boolean isPortrait = getResources().getBoolean(R.bool.is_portrait);
        boolean isPhone = getResources().getBoolean(R.bool.is_phone);
        if (isPortrait){
            if (isPhone){
                layoutManager = new LinearLayoutManager(this);
            }else {
                layoutManager = new GridLayoutManager(
                        this,
                        RECIPE_SPAN_COUNT_GRID_LAYOUT,
                        LinearLayoutManager.VERTICAL,
                        false);
            }
        }else {
            layoutManager = new GridLayoutManager(
                    this,
                    isPhone ? RECIPE_SPAN_COUNT_GRID_LAYOUT : RECIPE_SPAN_COUNT_GRID_LAYOUT_TABLET_AND_LANDSCAPE,
                    LinearLayoutManager.VERTICAL,
                    false);
        }
        binding.recipeRecyclerView.setLayoutManager(layoutManager);
        adapter = new RecipeRecyclerViewAdapter(this,
                this, viewModel.recipesListLiveData.getValue());
        binding.recipeRecyclerView.setAdapter(adapter);
    }

    private void observeViewModelLiveDataVariables() {
        viewModel.recipesListLiveData.observe(this, recipesList -> {
            if (recipesList != null){
                adapter.swapList(recipesList);
            }
        });
    }

    // ---- Public Methods

    public MainViewModel getViewModel(){
        return viewModel;
    }

    // ---- Implemented Steps Adapter Listener Methods

    @Override
    public void onItemClick(int recipeIndex) {
        List<Recipe> recipeList = viewModel.recipesListLiveData.getValue();

        List<Ingredients> ingredientsList = null;
        List<Steps> stepsList = null;
        if (recipeList != null){
            Recipe recipe = recipeList.get(recipeIndex);

            ingredientsList = recipe.getIngredients();
            stepsList = recipe.getSteps();
        }

        Intent intent = new Intent(this, RecipeStepsMasterActivity.class);
        intent.putExtra(RecipeStepsMasterFragment.INTENT_KEY_INGREDIENTS_LIST,
                ((Serializable) (ingredientsList == null ? new ArrayList<>() : ingredientsList)));
        intent.putExtra(RecipeStepsMasterFragment.INTENT_KEY_STEPS_LIST,
                ((Serializable) (stepsList == null ? new ArrayList<>() : stepsList)));
        startActivity(intent);
    }
    
}
