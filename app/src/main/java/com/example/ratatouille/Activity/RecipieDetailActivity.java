package com.example.ratatouille.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.R;
import com.example.ratatouille.databinding.ActivityRecipieDetailBinding;

public class RecipieDetailActivity extends AppCompatActivity {

    ActivityRecipieDetailBinding activityRecipieDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRecipieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipie_detail);

        Recipes model = (Recipes) getIntent().getSerializableExtra("recipie_model");

        activityRecipieDetailBinding.category.setText(String.valueOf(model.getCookTimeMin()));
        activityRecipieDetailBinding.country.setText(model.getRegion());
        activityRecipieDetailBinding.instructions.setText( model.getRecipeDescription());


        //activityRecipieDetailBinding.mealThumb.setImageDrawable(null);
        if(model.getRecipeImageUrl()!=null) {
            Glide.with(this).load(model.getRecipeImageUrl()).into(activityRecipieDetailBinding.mealThumb);
        }else{
            Glide.with(this).load(R.drawable.ic_post).into(activityRecipieDetailBinding.mealThumb);
        }

        String ing="";

        for(int i=0;i<model.getIngredients().size();i++)
        {
            ing+=model.getIngredients().get(i)+System.lineSeparator()+System.lineSeparator();

        }
        activityRecipieDetailBinding.ingredient.setText(ing);


    }
}