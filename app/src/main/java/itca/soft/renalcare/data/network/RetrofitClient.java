// RetrofitClient.java
package itca.soft.renalcare.data.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // IMPORTANTE: Cambia esta URL por la de tu servidor
    // Ejemplo: "http://192.168.1.100:3000/" si estás en la misma red
    // o "https://tu-dominio.com/" si está en producción

    private static final String BASE_URL = "http://192.168.138.109:3000/";
    // URL base para las imágenes (mismo servidor)
    public static final String IMAGE_BASE_URL = BASE_URL+"public";

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logger para ver las peticiones en Logcat
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Método helper para obtener URL completa de imagen
    public static String getFullImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // Si ya es una URL completa, retornarla
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }

        // Si empieza con /, quitarlo porque BASE_URL ya termina con /
        if (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        }

        return IMAGE_BASE_URL + imagePath;
    }
}