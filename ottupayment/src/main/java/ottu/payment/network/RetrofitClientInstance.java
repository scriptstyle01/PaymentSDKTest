package ottu.payment.network;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {


    public static native String getLink();
    public static native String getLinkPg();

    private static Retrofit retrofit;
    private static Retrofit retrofit1;

    public static GetDataService getRetrofitInstance() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Api-Key L0Fc5f81.dLqByodGesaD9pJdzoKpo6rP1FQBkVzR")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getLink())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
            return retrofit.create(GetDataService.class);
        }
        return retrofit.create(GetDataService.class);
    }

    public static GetDataService getRetrofitInstancePg() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Api-Key L0Fc5f81.dLqByodGesaD9pJdzoKpo6rP1FQBkVzR")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (retrofit1 == null) {
            retrofit1 = new Retrofit.Builder()
                    .baseUrl(getLinkPg())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
            return retrofit1.create(GetDataService.class);
        }
        return retrofit1.create(GetDataService.class);
    }


}