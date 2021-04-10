package com.example.ratatouille.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.ratatouille.Activity.RecipieDetailActivity;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.R;

import java.util.List;

public class WhatTodayAdapter extends PagerAdapter {

    private List<Recipes> recipes;
    private LayoutInflater layoutInflater;
    private Context context;
    public static final String TAG="whattodayadapter";

    public WhatTodayAdapter(List<Recipes> models, Context context) {
        this.recipes = models;
        this.context = context;
        Log.d(TAG, String.valueOf(recipes.size()));
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.what_today_item, container, false);

        ImageView imageView;
        TextView title, desc;

        imageView = view.findViewById(R.id.image_whattoday);
        title = view.findViewById(R.id.title_whattoday);
        desc = view.findViewById(R.id.desc_whattoday);

        Log.d(TAG, "instantiateItem: "+desc);

        // imageView.setImageResource(recipes.get(position).getImage());
        title.setText(recipes.get(position).getRecipeName());
        desc.setText(recipes.get(position).getRecipeDescription());
        imageView.setImageDrawable(null);
        if(recipes.get(position).getRecipeImageUrl()!=null) {
            Glide.with(this.context).load(recipes.get(position).getRecipeImageUrl()).into(imageView);
        }else{
            Glide.with(this.context).load(R.drawable.ic_post).into(imageView);
        }
        Log.d(TAG, "instantiateItem: "+desc);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipieDetailActivity.class);
                intent.putExtra("recipie_model", recipes.get(position));
                context.startActivity(intent);
                // finish();



            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}