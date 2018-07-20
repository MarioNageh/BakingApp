package android.mohamedalaa.com.bakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Mohamed on 7/20/2018.
 *
 */
public class NetworkUtils {

    /**
     * Searched From Stack-Overflow post
     * PLUS ==> I added .isConnected() as we need to be connected,
     *      so we can know if we are online or not
     * @return true if device has online network, false otherwise.
     */
    public static boolean isCurrentlyOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = null;
        if (cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.isConnected();
    }

}
