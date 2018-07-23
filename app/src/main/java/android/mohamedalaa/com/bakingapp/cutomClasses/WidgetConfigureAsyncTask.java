package android.mohamedalaa.com.bakingapp.cutomClasses;

import android.mohamedalaa.com.bakingapp.DataRepository;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.model.retrofit.RetrofitApiClient;
import android.mohamedalaa.com.bakingapp.model.retrofit.RetrofitApiInterface;
import android.mohamedalaa.com.bakingapp.utils.RecipeUtils;
import android.mohamedalaa.com.bakingapp.viewmodel.IngredientsWidgetConfigureViewModel;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by Mohamed on 7/23/2018.
 *
 */
public class WidgetConfigureAsyncTask extends AsyncTask<Void , Void , List<Recipe>>  {

    private final WeakReference<DataRepository> weakReferenceDataRepository;
    private final WeakReference<IngredientsWidgetConfigureViewModel> weakReferenceViewModel;

    public WidgetConfigureAsyncTask(DataRepository dataRepository,
                                    IngredientsWidgetConfigureViewModel viewModel) {
        weakReferenceDataRepository = new WeakReference<>(dataRepository);

        weakReferenceViewModel = new WeakReference<>(viewModel);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected List<Recipe> doInBackground(Void... voids) {
        if (weakReferenceDataRepository != null){
            DataRepository dataRepository = weakReferenceDataRepository.get();

            if (dataRepository != null){
                try {
                    // wrapping in try and catch in case of any unexpected error that would occur.
                    List<Recipe> recipeList = getRecipeListFromDatabaseOrInternet(dataRepository);
                    return recipeList == null ? new ArrayList<>() : recipeList;
                }catch (Exception e){
                    Timber.v("Async Task error -> " + e.getMessage());
                    return new ArrayList<>();
                }
            }
        }

        return new ArrayList<>();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onPostExecute(List<Recipe> recipeList) {
        if (recipeList == null || weakReferenceViewModel == null){
            return;
        }

        IngredientsWidgetConfigureViewModel viewModel = weakReferenceViewModel.get();
        if (viewModel == null){
            return;
        }

        if (recipeList != null && recipeList.size() > 0){
            Timber.v("Came here 1");
            viewModel.recipesListLiveData.setValue(recipeList);
        }else {
            // return empty list since initial value is null, if we change to null the observer
            // won't be notified.
            Timber.v("Came here 2");
            recipeList = new ArrayList<>();
            viewModel.recipesListLiveData.setValue(new ArrayList<>());
        }
    }

    // --- Private Helper Methods

    private List<Recipe> getRecipeListFromDatabaseOrInternet(DataRepository dataRepository) throws Exception{
        // firstly Trying database, if not forced, then try from internet.
        List<Recipe> recipeList = dataRepository.getAllRecipeList();
        recipeList = RecipeUtils.fillImagesInsideRecipeList(recipeList);

        if (recipeList == null || recipeList.size() == 0){
            // 1- Get data from internet.
            // 2- Save it inside database
            recipeList = getRecipeListFromInternet();

            if (recipeList != null && recipeList.size() > 0){
                dataRepository.insertRecipeList(recipeList);
            }

            return recipeList;
        }else {
            return recipeList;
        }
    }

    @SuppressWarnings("RedundantThrows")
    private List<Recipe> getRecipeListFromInternet() throws Exception{
        Retrofit retrofitApiClient = RetrofitApiClient.getClient();
        RetrofitApiInterface retrofitApiInterface = retrofitApiClient
                .create(RetrofitApiInterface.class);

        Call<List<Recipe>> call = retrofitApiInterface.getAllRecipes();
        try {
            Response<List<Recipe>> response = call.execute();

            if (response.isSuccessful()){
                List<Recipe> recipeList = response.body();
                recipeList = RecipeUtils.fillImagesInsideRecipeList(recipeList);
                return recipeList;
            }else {
                return null;
            }
        } catch (IOException e) {
            // Maybe network failure.
            Timber.v("getRecipeListFromInternet() throws Exception -> Error Msg -> "
                    + e.getMessage());

            return null;
        }
    }
}
