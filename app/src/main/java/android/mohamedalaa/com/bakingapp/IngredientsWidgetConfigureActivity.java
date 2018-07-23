package android.mohamedalaa.com.bakingapp;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.mohamedalaa.com.bakingapp.databinding.ActivityIngredientsWidgetConfigureBinding;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.utils.SharedPrefUtils;
import android.mohamedalaa.com.bakingapp.view.adapters.WidgetConfAdapter;
import android.mohamedalaa.com.bakingapp.viewmodel.IngredientsWidgetConfigureViewModel;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

/**
 * Created by Mohamed on 7/23/2018.
 *
 */
public class IngredientsWidgetConfigureActivity extends FragmentActivity implements
        WidgetConfAdapter.WidgetConfItemListener {

    private static final int RECYCLER_VIEW_SPAN_COUNT = 2;

    private ActivityIngredientsWidgetConfigureBinding binding;

    private IngredientsWidgetConfigureViewModel viewModel;

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private WidgetConfAdapter adapter;

    public IngredientsWidgetConfigureActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        // Set the view layout resource to use.
        binding = DataBindingUtil.setContentView(
                this, R.layout.activity_ingredients_widget_configure);
        // Title for activity
        setTitle(getString(R.string.widget_configuration_title_short));

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget id, just bail.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();

            return;
        }

        // Start getting from database
        viewModel = ViewModelProviders.of(this)
                .get(IngredientsWidgetConfigureViewModel.class);

        // observe view model changes
        observeViewModelObservableVariables();

        // setup views
        setupViews();

        // setup clicks
        setupClicks();
    }

    private void observeViewModelObservableVariables() {
        viewModel.recipesListLiveData.observe(this, recipeList -> {
            if (recipeList == null){
                binding.loadingFrameLayout.setVisibility(View.VISIBLE);
            }else if (recipeList.size() == 0){
                /*
                VIP NOTE -> we get data from fixed API that has list and cannot be empty
                that's why i handle empty list as there is no internet connection
                other ways to handle it would be like what i did in
                RecipeListAsyncTask class in customClasses package
                ( short explanation )
                in the background we set int values to determine which is shown
                either empty view, loading, no internet or the list got from API
                 */
                binding.loadingFrameLayout.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.GONE);
                // no internet connection view is always visible.
            }else {
                binding.loadingFrameLayout.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);

                adapter.swapList(recipeList);
            }
        });
    }

    private void setupViews() {
        if (viewModel != null && viewModel.indexChosen != -1){
            // set button enabled && it's alpha to indicate that it is enabled
            binding.okButton.setEnabled(true);
            binding.okButton.setAlpha(1f);
        }else {
            binding.okButton.setEnabled(false);
            binding.okButton.setAlpha(0.5f);
        }

        adapter = new WidgetConfAdapter(this, null, viewModel, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                this,
                RECYCLER_VIEW_SPAN_COUNT,
                LinearLayoutManager.VERTICAL,
                false);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupClicks() {
        binding.cancelButton.setOnClickListener(view -> {
            // Note we already set the result to cancel so no need to re fo it.

            finish();
        });

        binding.okButton.setOnClickListener(view -> {
            Context context = getBaseContext().getApplicationContext();

            int indexChosen = viewModel.indexChosen;

            SharedPrefUtils.setWidgetChosenRecipeIndex(
                    context, appWidgetId, indexChosen);

            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            List<Recipe> recipeList = viewModel.recipesListLiveData.getValue();
            if (recipeList != null && recipeList.size() > 0){
                IngredientsWidgetProvider.performUpdateOnOneId(
                        context,
                        appWidgetManager,
                        appWidgetId,
                        recipeList.get(indexChosen));
                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }else {
                finish();
            }
        });

        binding.refreshButton.setOnClickListener(view
                -> viewModel.initialSetups());
    }

    // ---- Implemented interfaces methods

    @Override
    public void onItemClick() {
        if (viewModel.indexChosen != -1){
            // set button enabled && it's alpha to indicate that it is enabled
            binding.okButton.setEnabled(true);
            binding.okButton.setAlpha(1f);
        }else {
            binding.okButton.setEnabled(false);
            binding.okButton.setAlpha(0.5f);
        }
    }

}
