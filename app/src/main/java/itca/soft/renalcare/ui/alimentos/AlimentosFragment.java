package itca.soft.renalcare.ui.alimentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import itca.soft.renalcare.R;

import itca.soft.renalcare.ui.alimentos.FoodListAdapter;
import itca.soft.renalcare.data.models.FoodItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlimentosFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodListAdapter adapter;
    private List<FoodItem> foodList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alimentos, container, false);

        recyclerView = view.findViewById(R.id.recycler_food_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Datos de ejemplo (sustituye con datos reales de tu app)
        foodList = new ArrayList<>();
        foodList.add(new FoodItem("Pechuga de Pollo a la Plancha", 256, 74, 228, "Apto para dieta renal", 0));
        foodList.add(new FoodItem("Brócoli al Vapor", 316, 33, 66, "Consumir con moderación", 0));
        foodList.add(new FoodItem("Arroz Blanco", 55, 5, 43, "Apto para dieta renal", 0));
        foodList.add(new FoodItem("Salmón Fresco", 363, 59, 252, "Consumir con moderación", 0));
        foodList.add(new FoodItem("Pepino", 147, 2, 24, "Apto para dieta renal", 0));
        // Añade más alimentos si lo deseas

        adapter = new FoodListAdapter(foodList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}