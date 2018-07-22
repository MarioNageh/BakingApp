package android.mohamedalaa.com.bakingapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.mohamedalaa.com.bakingapp.services.JobServiceUpdateWidget;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by Mohamed on 7/22/2018.
 *
 * Used for android devices running on android Oreo and above.
 * due to background limitations, we cannot launch service except the one that applies to
 * the limitation ( JobService ).
 */
public class BroadcastReceiverUpdateWidget extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Made below alternative for android Oreo and above due to the background limitation
        // link -> https://developer.android.com/about/versions/oreo/background
        // because if not done it will make error on devices running Oreo and above
        Driver driver = new GooglePlayDriver(context.getApplicationContext());
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job job = dispatcher.newJobBuilder()
                .setService(JobServiceUpdateWidget.class)
                .setTag(JobServiceUpdateWidget.SERVICE_GENERAL_TAG)
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(0, 0))
                .build();
        dispatcher.mustSchedule(job);
    }

}
