package android.mohamedalaa.com.bakingapp.services;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.mohamedalaa.com.bakingapp.BaseApplication;
import android.mohamedalaa.com.bakingapp.DataRepository;
import android.mohamedalaa.com.bakingapp.IngredientsWidgetProvider;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.model.retrofit.RetrofitApiClient;
import android.mohamedalaa.com.bakingapp.model.retrofit.RetrofitApiInterface;
import android.mohamedalaa.com.bakingapp.utils.RecipeUtils;
import android.mohamedalaa.com.bakingapp.utils.SharedPrefUtils;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Mohamed on 7/22/2018.
 *
 */
public class JobServiceUpdateWidget extends JobService {

    public static final String SERVICE_GENERAL_TAG = "SERVICE_GENERAL_TAG";

    /**
     * @return 	true if your service will continue running, using a separate thread when
     * appropriate. false means that this job has completed its work.
     */
    @Override
    public boolean onStartJob(JobParameters job) {
        new Thread(() -> {
            // Get recipe list from
            // 1- database
            DataRepository dataRepository = ((BaseApplication) getApplication()).getRepository();
            List<Recipe> recipeList = dataRepository.getAllRecipeList();
            // 2- internet, if not in database
            if (recipeList == null || recipeList.size() == 0){
                Retrofit retrofitApiClient = RetrofitApiClient.getClient();
                RetrofitApiInterface retrofitApiInterface = retrofitApiClient
                        .create(RetrofitApiInterface.class);

                Call<List<Recipe>> call = retrofitApiInterface.getAllRecipes();
                try {
                    Response<List<Recipe>> response = call.execute();

                    if (response.isSuccessful()){
                        recipeList = response.body();
                        recipeList = RecipeUtils.fillImagesInsideRecipeList(recipeList);
                    }
                } catch (IOException e) {
                    // Maybe network failure.
                }
            }

            // Make widget update.
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(this, IngredientsWidgetProvider.class));
            int indexChosen = SharedPrefUtils.getWidgetChosenRecipeIndex(getApplicationContext());
            if (recipeList == null || indexChosen < 0){
                IngredientsWidgetProvider.performUpdateOnAllIds(
                        this, appWidgetManager, appWidgetIds, null);
            }else {
                IngredientsWidgetProvider.performUpdateOnAllIds(
                        this, appWidgetManager, appWidgetIds, recipeList.get(indexChosen));
            }
        }).start();

        return true;
    }

    /**
     * link -> https://developer.android.com/reference/android/app/job/JobService#onStopJob(android.app.job.JobParameters)
     *
     * @return true to indicate to the JobManager whether you'd like to reschedule this job based on
     * the retry criteria provided at job creation-time; or false to end the job entirely.
     * Regardless of the value returned, your job must stop executing.
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
