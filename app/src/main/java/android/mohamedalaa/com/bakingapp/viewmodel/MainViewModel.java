package android.mohamedalaa.com.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableMap;
import android.mohamedalaa.com.bakingapp.BaseApplication;
import android.mohamedalaa.com.bakingapp.DataRepository;
import android.mohamedalaa.com.bakingapp.cutomClasses.RecipeListAsyncTask;
import android.mohamedalaa.com.bakingapp.cutomClasses.SimpleIdlingResource;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.List;

/**
 * Created by Mohamed on 7/18/2018.
 *
 * Usage
 * 1- since viewModels survive config changes so we put here values that need to survive as well.
 * Ex. the list that comes from internet it's better not to re-call it, since we already have it.
 */
public class MainViewModel extends AndroidViewModel {

    // --- Constants

    public static final int SHOW_RECYCLER_VIEW = 1;
    public static final int SHOW_EMPTY_VIEW = 2;
    private static final int SHOW_LOADING_VIEW = 3;
    public static final int SHOW_NO_INTERNET_CONNECTION_VIEW = 4;

    // --- Xml Observable Variables

    public final ObservableMap<Integer , Boolean> recipeViewsVisibilities = new ObservableArrayMap<>();

    // --- Class Live Data Variable

    public final MutableLiveData<List<Recipe>> recipesListLiveData = new MutableLiveData<>();

    // --- Private Variables

    @Nullable private SimpleIdlingResource idlingResource;

    public MainViewModel(@NonNull Application application) {
        super(application);

        initXmlObservableVariables();
    }

    private void initXmlObservableVariables(){
        // initial values for visibilities
        for (int i=1; i<=4; i++){
            if (i == SHOW_LOADING_VIEW){
                // Since initial state must be loading till we get data from internet
                recipeViewsVisibilities.put(i, true);

                continue;
            }

            recipeViewsVisibilities.put(i, false);
        }
        // listener to ensure only one visibility is visible
        recipeViewsVisibilities.addOnMapChangedCallback(observableCallbackRecipeViewsVisibilities);
    }

    // ---- Public Fragment/Activity Methods

    /**
     * Could 've been initialized in constructor but any delay has to be done after onCreate()
     * so that espresso can work properly.
     */
    public void initialSetupsInOnResume(){
        // For testing purposes
        if (idlingResource != null){
            idlingResource.setIdleState(false);
        }

        // Getting recipe list from internet, only if it null.
        // otherwise means a config change is made and we already have the value.
        if (recipesListLiveData.getValue() == null){
            getRecipeListFromDbOrInternetAndSaveItToDbIfWasEmpty(false);
        }
    }

    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getIdlingResource(){
        if (idlingResource == null){
            idlingResource = new SimpleIdlingResource();
        }

        return idlingResource;
    }

    // --- Xml Direct Methods

    public void fabClickRetryGettingRecipeListFromInternet(){
        // this fab only appears when no data in database and no internet connection
        // so we previously know we o=need internet connection, so no need for re-checking database.
        recipeViewsVisibilities.put(SHOW_LOADING_VIEW, true);
        getRecipeListFromDbOrInternetAndSaveItToDbIfWasEmpty(true);
    }

    // --- Private Listener Variables

    private final ObservableMap.OnMapChangedCallback<ObservableMap<Integer, Boolean>, Integer, Boolean> observableCallbackRecipeViewsVisibilities
            = new ObservableMap.OnMapChangedCallback<ObservableMap<Integer, Boolean>, Integer, Boolean>() {
        @Override
        public void onMapChanged(ObservableMap<Integer, Boolean> observableMap, Integer integer) {
            if (observableMap.get(integer)){
                // then make all others false in visibility
                for (int i=1; i<=4; i++){
                    if (i == integer){
                        continue;
                    }

                    observableMap.put(i, false);
                }
            }
        }
    };

    // ---- Private Helper Methods ( used in more than one place )

    private void getRecipeListFromDbOrInternetAndSaveItToDbIfWasEmpty(boolean forceFromInternet){
        DataRepository dataRepository = ((BaseApplication) getApplication()).getRepository();

        RecipeListAsyncTask recipeListAsyncTask = new RecipeListAsyncTask(
                dataRepository, this, idlingResource);
        if (forceFromInternet){
            recipeListAsyncTask.forceGettingRecipeListFromInternet();
        }
        recipeListAsyncTask.execute();
    }

}
