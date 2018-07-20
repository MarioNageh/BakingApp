package android.mohamedalaa.com.bakingapp.model.retrofit;

import android.mohamedalaa.com.bakingapp.BuildConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mohamed on 7/18/2018.
 *
 */
public class RetrofitApiClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(){
        if (retrofit == null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .serializeNulls()
                    .create();

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    /* Default seconds are 10, increasing them in case of poor internet connectivity */
                    .readTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.RECIPES_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();

            return retrofit;
        }else {
            return retrofit;
        }
    }
}
