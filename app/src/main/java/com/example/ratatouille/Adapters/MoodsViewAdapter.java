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

public class MoodsViewAdapter extends RecyclerView.Adapter<MoodsViewAdapter.MoodsViewHolder> {
    ArrayList<String> Moods;
    Context context;

    public MoodsViewAdapter(Context context,ArrayList<String> Moods){
        this.Moods=Moods;
        this.context=context;
    }


    @NonNull
    @Override
    public MoodsViewAdapter.MoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mood_tag,parent,false);
        return new MoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodsViewHolder holder, int position) {
//        holder.setIsRecyclable(false);

        String tag = Moods.get(position);
        holder.chip.setText(tag);
    }

    @Override
    public int getItemCount() {
        return Moods.size();
    }

    public class MoodsViewHolder extends RecyclerView.ViewHolder{

        Chip chip;
        public MoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.moods_item);
            chip.setCloseIconEnabled(false);

        }
    }
}
