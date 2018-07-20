package android.mohamedalaa.com.bakingapp.view;

import android.annotation.SuppressLint;
import android.mohamedalaa.com.bakingapp.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RecipeStepsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeEitherFullscreenOrNotAndChangeNavigationBarAccordingly();
        setContentView(R.layout.activity_recipe_steps_detail);
    }

    /**
     * Note
     * I 've done this approach as in the mock UI it was obvious that it should be fullscreen
     * Also note it is still scrollable.
     */
    private void setThemeEitherFullscreenOrNotAndChangeNavigationBarAccordingly() {
        boolean isLandscape = ! getResources().getBoolean(R.bool.is_portrait);
        boolean isPhone = getResources().getBoolean(R.bool.is_phone);

        if (isLandscape && isPhone){
            // fullscreen theme && hide system navigation bar
            setTheme(R.style.AppFullscreenTheme);
            hideNavigationBarAndKeepItHidden();
        }else {
            // normal theme && show system navigation bar ( in case was hidden )
            setTheme(R.style.AppTheme);
            showNavigationBarAndKeepItShown();
        }
    }

    // ---- Private Global Helper

    @SuppressLint("InlinedApi")
    private void hideNavigationBarAndKeepItHidden(){
        View decorView = getWindow().getDecorView();
        // It's good practice to include other system UI flags
        // (such as SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and SYSTEM_UI_FLAG_LAYOUT_STABLE)
        // to keep the content from resizing when the system bars hide and show.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                /*| View.SYSTEM_UI_FLAG_LAYOUT_STABLE*/;
        decorView.setSystemUiVisibility(uiOptions);

        // If System uiOptions changed
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                // The system bars are visible. Make any desired decision.
                decorView.setSystemUiVisibility(uiOptions);
            }
        });
    }

    private void showNavigationBarAndKeepItShown(){
        View decorView = getWindow().getDecorView();
        // It's good practice to include other system UI flags
        // (such as SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and SYSTEM_UI_FLAG_LAYOUT_STABLE)
        // to keep the content from resizing when the system bars hide and show.

        // If System uiOptions changed
        decorView.setOnSystemUiVisibilityChangeListener(null);

        int uiOptions = 0 /*View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR*/
                /*| View.SYSTEM_UI_FLAG_LAYOUT_STABLE*/;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
