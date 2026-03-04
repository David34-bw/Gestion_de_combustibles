package co.edu.unipiloto.aplicaciongestiondecombustibles.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // ⚠ Emulador Android → usa 10.0.2.2 en vez de localhost
    // ⚠ Dispositivo físico → usa la IP local de tu PC (ej: 192.168.1.X)
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;
    private static String authToken = null;

    public static void setToken(String token) {
        authToken = token;
        retrofit = null; // Fuerza recrear con el nuevo token
    }

    public static void clearToken() {
        authToken = null;
        retrofit = null;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json");

                        if (authToken != null) {
                            builder.header("Authorization", "Bearer " + authToken);
                        }
                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
