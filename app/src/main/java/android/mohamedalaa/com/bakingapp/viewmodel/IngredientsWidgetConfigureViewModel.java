package android.mohamedalaa.com.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.mohamedalaa.com.bakingapp.BaseApplication;
import android.mohamedalaa.com.bakingapp.DataRepository;
import android.mohamedalaa.com.bakingapp.cutomClasses.WidgetConfigureAsyncTask;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Mohamed on 7/23/2018.
 *
 */
public class IngredientsWidgetConfigureViewModel extends AndroidViewModel {

    /**
     * to survive configuration changes, it's placed here.
     * -1 means nothing selected.
     */
    public int indexChosen = -1;

    public MutableLiveData<List<Recipe>> recipesListLiveData = new MutableLiveData<>();

    public IngredientsWidgetConfigureViewModel(@NonNull Application application) {
        super(application);

        initialSetups();
    }

    public void initialSetups() {
        // -- Get List of Recipes
        // null means keep loading
        // if empty returned means no internet connection
        // else show the list
        recipesListLiveData.setValue(null);

        DataRepository dataRepository = ((BaseApplication) getApplication()).getRepository();
        WidgetConfigureAsyncTask asyncTask = new WidgetConfigureAsyncTask(
                dataRepository, this);
        asyncTask.execute();
    }

}
