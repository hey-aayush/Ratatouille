package com.example.ratatouille.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ratatouille.R;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class IngredientViewAdapter extends RecyclerView.Adapter<IngredientViewAdapter.ingredientViewHolder> {
    ArrayList<String> ingredients;
    Context context;

    public IngredientViewAdapter(Context context,ArrayList<String> ingredients){
        this.ingredients=ingredients;
        this.context=context;
    }

    @NonNull
    @Override
    public IngredientViewAdapter.ingredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient_tag,parent,false);
        return new ingredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ingredientViewHolder holder, int position) {
        String tag = ingredients.get(position);
        holder.chip.setText(tag);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ingredientViewHolder extends RecyclerView.ViewHolder{
        Chip chip;
        public ingredientViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.ingredient_item);
            chip.setCloseIconEnabled(false);
        }
    }
}

