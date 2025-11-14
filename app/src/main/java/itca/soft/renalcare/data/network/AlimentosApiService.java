package itca.soft.renalcare.data.network;

import java.util.List;
import itca.soft.renalcare.data.models.FoodItem;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AlimentosApiService {

    /**
     * Define el endpoint GET para /api/alimentos
     * Espera recibir una Lista de objetos FoodItem.
     */
    @GET("api/alimentos")
    Call<List<FoodItem>> getAlimentos();
}