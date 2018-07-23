package android.mohamedalaa.com.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Mohamed on 7/19/2018.
 *
 */
public class RecipeStepsDetailViewModel extends AndroidViewModel {

    // --- Public Variables ( to survive configuration

    public List<Steps> stepsList;
    public int indexChosen;

    public long videoPlayerPosition;
    public boolean lastVideoStateOfPlayWhenReady = true;

    public RecipeStepsDetailViewModel(@NonNull Application application) {
        super(application);
    }

}
