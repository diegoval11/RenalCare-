package itca.soft.renalcare.ui.alimentos;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.FoodItem;

import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewHolder> {

    private List<FoodItem> foodList;

    public FoodListAdapter(List<FoodItem> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);
        holder.foodName.setText(item.getName());
        holder.nutrientK.setText(item.getPotassium() + "mg");
        holder.nutrientNa.setText(item.getSodium() + "mg");
        holder.nutrientP.setText(item.getPhosphorus() + "mg");
        holder.recommendationTag.setText(item.getRecommendation());

        // Aquí es donde pondrías la lógica para cargar la imagen real.
        // Por ahora, solo estableceremos un color de fondo para el placeholder
        if (item.getImageResId() != 0) {
            // Si tuvieras imágenes reales en drawables:
            // holder.foodImage.setImageResource(item.getImageResId());
        } else {
            // Placeholder: Puedes establecer un color de fondo o un drawable por defecto
            holder.foodImage.setBackgroundResource(R.color.renal_dark_gray); // O cualquier color para el hueco
        }

        // Puedes cambiar el color del tag según la recomendación
        if (item.getRecommendation().equals("Apto para dieta renal")) {
            holder.recommendationTag.setBackgroundResource(R.drawable.rounded_background_green);
        } else if (item.getRecommendation().equals("Consumir con moderación")) {
            // Si creas un drawable para "moderación"
            // holder.recommendationTag.setBackgroundResource(R.drawable.rounded_background_orange);
            // Por ahora, usaremos el mismo o un color diferente directamente
            holder.recommendationTag.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.renal_blue_gray));
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName;
        TextView nutrientK;
        TextView nutrientNa;
        TextView nutrientP;
        TextView recommendationTag;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            nutrientK = itemView.findViewById(R.id.nutrient_k);
            nutrientNa = itemView.findViewById(R.id.nutrient_na);
            nutrientP = itemView.findViewById(R.id.nutrient_p);
            recommendationTag = itemView.findViewById(R.id.food_recommendation_tag);
        }
    }
}