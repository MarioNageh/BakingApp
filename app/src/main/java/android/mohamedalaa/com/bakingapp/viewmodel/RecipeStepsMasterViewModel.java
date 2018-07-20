package android.mohamedalaa.com.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.mohamedalaa.com.bakingapp.model.Ingredients;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Mohamed on 7/19/2018.
 *
 */
public class RecipeStepsMasterViewModel extends AndroidViewModel {

    // --- Public Variables to survive configuration changes

    public List<Ingredients> ingredientsList;
    public List<Steps> stepsList;

    public List<String> adapterShortDescriptionList;
    public List<String> adapterFullDescriptionList;

    public int tabletAdapterIndexChosen;

    public RecipeStepsMasterViewModel(@NonNull Application application) {
        super(application);
    }
}
