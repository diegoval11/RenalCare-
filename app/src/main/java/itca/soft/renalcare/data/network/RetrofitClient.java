package itca.soft.renalcare.data.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    // ⚠️ Cambia esta IP por la de tu servidor o dominio en producción
    private static final String BASE_URL = "http://192.168.1.163:3000/";
    // URL base para imágenes (carpeta /public del servidor)
    public static final String IMAGE_BASE_URL = BASE_URL + "public/";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    /**
     * Inicializa Retrofit con configuración avanzada
     */
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Interceptor para mostrar logs de las peticiones HTTP
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

    /**
     * Devuelve la instancia de Retrofit (para crear servicios personalizados)
     */
    public static Retrofit getClient() {
        return getRetrofitInstance();
    }

    /**
     * Devuelve la instancia del ApiService
     */
    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofitInstance().create(ApiService.class);
        }
        return apiService;
    }

    /**
     * Retorna la URL completa de una imagen
     */
    public static String getFullImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // Si ya es una URL completa, retornar tal cual
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }

        // Eliminar "/" inicial para evitar duplicar el separador
        if (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        }

        return IMAGE_BASE_URL + imagePath;
    }
}