package itca.soft.renalcare.ui.alimentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout; // ¡NUEVO IMPORT!
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.FoodItem;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewHolder> implements Filterable {

    private Context context;
    private List<FoodItem> foodList;
    private List<FoodItem> foodListFull;

    // ¡NUEVO! Variable para rastrear el ítem expandido
    // -1 significa que ninguno está expandido.
    private int expandedPosition = -1;

    public FoodListAdapter(Context context, List<FoodItem> foodList) {
        this.context = context;
        this.foodList = new ArrayList<>(foodList);
        this.foodListFull = new ArrayList<>(foodList);
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);

        // --- Settear datos (la mayoría sin cambios) ---
        holder.tvName.setText(item.getNombre());
        holder.tvSodio.setText(String.format(Locale.US, "%d mg", item.getSodio()));
        holder.tvPotasio.setText(String.format(Locale.US, "%d mg", item.getPotasio()));
        holder.tvFosforo.setText(String.format(Locale.US, "%d mg", item.getFosforo()));
        holder.tvTag.setText(item.getEtiqueta());

        Glide.with(context)
                .load(item.getFotoUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.ivPhoto);

        String etiqueta = item.getEtiqueta().toLowerCase();
        int colorRes = R.drawable.rounded_background_dark_gray;

        if (etiqueta.contains("apto")) {
            colorRes = R.drawable.rounded_background_green;
        } else if (etiqueta.contains("moderaci")) {
            colorRes = R.drawable.rounded_background_orange;
        } else if (etiqueta.contains("alto") || etiqueta.contains("evitar")) {
            colorRes = R.drawable.rounded_background_red;
        }

        holder.tvTag.setBackgroundResource(colorRes);

        // --- ¡NUEVA LÓGICA DE EXPANSIÓN! ---

        // 1. Settear el texto de los campos expandibles
        holder.tvIngredientes.setText(item.getIngredientes());
        holder.tvReceta.setText(item.getReceta());

        // 2. Comprobar si esta es la posición que debe estar expandida
        final boolean isExpanded = position == expandedPosition;
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // 3. Configurar el ClickListener para todo el ítem
        holder.itemView.setOnClickListener(v -> {
            // Guardamos la posición que estaba expandida ANTES del clic
            int previousExpandedPosition = expandedPosition;

            if (isExpanded) {
                // Si el ítem ya estaba expandido, lo colapsamos
                expandedPosition = -1;
            } else {
                // Si el ítem estaba colapsado, lo expandimos
                expandedPosition = position;
            }

            // Notificamos al adaptador para que se redibuje con animación

            // Colapsamos el ítem anterior (si existe)
            if (previousExpandedPosition != -1) {
                notifyItemChanged(previousExpandedPosition);
            }
            // Expandimos el nuevo ítem (o colapsamos el actual si fue re-clicado)
            notifyItemChanged(expandedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // --- Filtro (Sin cambios) ---
    @Override
    public Filter getFilter() {
        return foodFilter;
    }

    private Filter foodFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FoodItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(foodListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (FoodItem item : foodListFull) {
                    if (item.getNombre().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            foodList.clear();
            foodList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // --- ViewHolder (Actualizado) ---
    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvName, tvTag, tvSodio, tvPotasio, tvFosforo;

        // ¡NUEVAS VISTAS!
        LinearLayout expandableLayout;
        TextView tvIngredientes, tvReceta;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivFoodPhoto);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvTag = itemView.findViewById(R.id.tvFoodTag);
            tvSodio = itemView.findViewById(R.id.tvFoodSodio);
            tvPotasio = itemView.findViewById(R.id.tvFoodPotasio);
            tvFosforo = itemView.findViewById(R.id.tvFoodFosforo);

            // ¡NUEVOS FINDBYID!
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            tvIngredientes = itemView.findViewById(R.id.tvFoodIngredientes);
            tvReceta = itemView.findViewById(R.id.tvFoodReceta);
        }
    }

    // --- updateData (Sin cambios) ---
    public void updateData(List<FoodItem> newFoodList) {
        foodList.clear();
        foodList.addAll(newFoodList);
        foodListFull.clear();
        foodListFull.addAll(newFoodList);

        // ¡NUEVO! Reseteamos la expansión si los datos se actualizan (ej. por filtro)
        expandedPosition = -1;

        notifyDataSetChanged();
    }
}