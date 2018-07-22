package android.mohamedalaa.com.bakingapp.cutomClasses;

import android.mohamedalaa.com.bakingapp.DataRepository;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.model.retrofit.RetrofitApiClient;
import android.mohamedalaa.com.bakingapp.model.retrofit.RetrofitApiInterface;
import android.mohamedalaa.com.bakingapp.utils.RecipeUtils;
import android.mohamedalaa.com.bakingapp.viewmodel.MainViewModel;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by Mohamed on 7/18/2018.
 *
 * Flow of this method is described here
 * 1- check if database have list or not
 * 2- if have list, display it to user and no need for getting list from internet since we
 * already have it, also since it is fixed list no need to check internet for changes in list.
 * 3- if didn't have list in database, download data from internet, and display it to user,
 * and save it in database, so no need for internet connection in future.
 *
 * Since all of these steps in the method needs background thread, it will be done in
 * IntentService class
 *
 * Note
 * if it wasn't fixed database the flow would change, we should 've download from internet
 * whether there is data in database or not, then update database ONLY if new data came from
 * internet.
 */
public class RecipeListAsyncTask extends AsyncTask<Void , Void , RecipeListAsyncTask.IntegerAndList> {

    private final WeakReference<DataRepository> weakReferenceDataRepository;
    private final WeakReference<MainViewModel> weakReferenceMainViewModel;
    @Nullable private final SimpleIdlingResource idlingResource;

    private boolean forceFromInternet = false;

    public RecipeListAsyncTask(DataRepository dataRepository, MainViewModel mainViewModel,
                               @Nullable SimpleIdlingResource idlingResource) {
        weakReferenceDataRepository = new WeakReference<>(dataRepository);

        weakReferenceMainViewModel = new WeakReference<>(mainViewModel);

        this.idlingResource = idlingResource;
    }

    public void forceGettingRecipeListFromInternet(){
        this.forceFromInternet = true;
    }

    @Override
    protected IntegerAndList doInBackground(Void... voids) {
        if (weakReferenceDataRepository != null){
            DataRepository dataRepository = weakReferenceDataRepository.get();

            if (dataRepository != null){
                try {
                    // wrapping in try and catch in case of any unexpected error that would occur.
                    return getRecipeListFromDatabaseOrInternet(dataRepository);
                }catch (Exception e){
                    Timber.v("Async Task error -> " + e.getMessage());
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(IntegerAndList integerAndList) {
        if (integerAndList == null || weakReferenceMainViewModel == null){
            return;
        }

        MainViewModel mainViewModel = weakReferenceMainViewModel.get();
        if (mainViewModel == null){
            return;
        }

        mainViewModel.recipeViewsVisibilities.put(integerAndList.getValueInt(), true);

        List<Recipe> recipeList = integerAndList.getValueRecipeList();
        if (recipeList != null && recipeList.size() > 0){
            mainViewModel.recipesListLiveData.setValue(recipeList);
        }

        // For testing purposes
        if (idlingResource != null){
            idlingResource.setIdleState(true);
        }
    }

    // --- Private Helper Methods

    private IntegerAndList getRecipeListFromDatabaseOrInternet(DataRepository dataRepository) throws Exception{
        // firstly Trying database, if not forced, then try from internet.
        List<Recipe> recipeList = null;
        if (! forceFromInternet){
            recipeList = dataRepository.getAllRecipeList();
            recipeList = RecipeUtils.fillImagesInsideRecipeList(recipeList);
        }

        IntegerAndList integerAndList;
        if (recipeList == null || recipeList.size() == 0){
            // 1- Get data from internet.
            // 2- Save it inside database
            integerAndList = getRecipeListFromInternet();

            recipeList = integerAndList.getValueRecipeList();
            if (recipeList != null && recipeList.size() > 0){
                dataRepository.insertRecipeList(recipeList);
            }
        }else {
            // 1- Display data to user
            integerAndList = new IntegerAndList();
            integerAndList.setValueInt(MainViewModel.SHOW_RECYCLER_VIEW);
            integerAndList.setValueRecipeList(recipeList);
        }

        return integerAndList;
    }

    @SuppressWarnings("RedundantThrows")
    private IntegerAndList getRecipeListFromInternet() throws Exception{
        IntegerAndList integerAndList = new IntegerAndList();

        Retrofit retrofitApiClient = RetrofitApiClient.getClient();
        RetrofitApiInterface retrofitApiInterface = retrofitApiClient
                .create(RetrofitApiInterface.class);

        Call<List<Recipe>> call = retrofitApiInterface.getAllRecipes();
        try {
            Response<List<Recipe>> response = call.execute();

            if (response.isSuccessful()){
                List<Recipe> recipeList = response.body();
                recipeList = RecipeUtils.fillImagesInsideRecipeList(recipeList);

                if (recipeList == null || recipeList.size() == 0){
                    integerAndList.setValueInt(MainViewModel.SHOW_EMPTY_VIEW);
                }else {
                    integerAndList.setValueInt(MainViewModel.SHOW_RECYCLER_VIEW);
                }

                integerAndList.setValueRecipeList(recipeList);
            }else {
                // response isn't successful -> reason might be poor internet connection
                integerAndList.setValueInt(MainViewModel.SHOW_NO_INTERNET_CONNECTION_VIEW);
            }
        } catch (IOException e) {
            // Maybe network failure.
            integerAndList.setValueInt(MainViewModel.SHOW_NO_INTERNET_CONNECTION_VIEW);
        }

        return integerAndList;
    }

    // ----- Helper Class

    class IntegerAndList {

        private int valueInt;
        private List<Recipe> valueRecipeList;

        IntegerAndList(){}

        void setValueInt(int valueInt) {
            this.valueInt = valueInt;
        }

        void setValueRecipeList(List<Recipe> valueRecipeList) {
            this.valueRecipeList = valueRecipeList;
        }

        int getValueInt() {
            return valueInt;
        }

        List<Recipe> getValueRecipeList() {
            return valueRecipeList;
        }
    }

}
