package android.mohamedalaa.com.bakingapp.cutomClasses;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.CardView;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Mohamed on 7/19/2018.
 *
 */
public class SimpleIdlingResource implements IdlingResource {

    @Nullable
    private volatile ResourceCallback mCallback;

    // Idleness is controlled with this boolean.
    private AtomicBoolean mIsIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    /**
     * Sets the new idle state, if isIdleNow is true, it pings the {@link ResourceCallback}.
     * @param isIdleNow false if there are pending operations, true if idle.
     */
    @SuppressWarnings("ConstantConditions")
    public void setIdleState(boolean isIdleNow) {
        mIsIdleNow.set(isIdleNow);
        if (isIdleNow && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
    }

}
