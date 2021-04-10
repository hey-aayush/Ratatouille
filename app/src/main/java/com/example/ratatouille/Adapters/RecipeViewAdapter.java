package com.example.ratatouille.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ratatouille.Models.Likes;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.R;
import com.example.ratatouille.appUtils.HumanDateUtils;
import com.example.ratatouille.databinding.ItemRecipeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class RecipeViewAdapter extends RecyclerView.Adapter<RecipeViewAdapter.RecipeViewHolder> {

    Context context;
    ArrayList<Recipes> recipes;
    FirebaseFirestore fStore;

    public RecipeViewAdapter(Context context, ArrayList<Recipes> recipes, FirebaseFirestore fStore){
        this.context = context;
        this.recipes=recipes;
        this.fStore=fStore;
    }

    public RecipeViewAdapter(Context context, ArrayList<Recipes> recipes) {
        this.context=context;
        this.recipes=recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe,parent,false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {

        Recipes recipe = recipes.get(position);
        holder.binding.recipeTitle.setText(recipe.getRecipeName());
        holder.binding.Username.setText(recipe.getChefName());
        holder.binding.recipeTimestamp.setText(HumanDateUtils.durationFromNow(new Date(recipe.getTimeStamp())));
        holder.binding.recipeText.setText(recipe.getRecipeDescription());
        holder.binding.likesCounts.setText(recipe.getNoOfLikes()+" Likes");

        if(recipe.getIngredients()!=null) {
            ArrayList<String>ingredients = (ArrayList<String>) recipe.getIngredients();
            IngredientViewAdapter ingredientViewAdapter = new IngredientViewAdapter(context,ingredients);
            holder.ingredientsTags.setAdapter(ingredientViewAdapter);

        }
        if(recipe.getMoods()!=null){
            ArrayList<String> moods = (ArrayList<String>) recipe.getMoods();
            MoodsViewAdapter moodsViewAdapter = new MoodsViewAdapter(context,moods);
            holder.moodsTags.setAdapter(moodsViewAdapter);
        }

        holder.binding.CookingTime.setText("CookingTime : "+ recipe.getCookTimeMin() +" mins");

        holder.binding.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareRecipe(recipe);
            }
        });

        if (recipe.getRecipeId()!=null) {

            fStore.collection("recipesLikes").document(recipe.getRecipeId()).collection("Likes").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        if (documentSnapshot.toObject(Likes.class).getReactions() == 1) {
                            holder.binding.likeIcon.setImageResource(R.drawable.ic_liked);
                            holder.isLiked=true;
                        }else{
                            holder.isLiked=false;
                            holder.binding.likeIcon.setImageResource(R.drawable.ic_unliked);
                        }
                    }else{
                        holder.isLiked=false;
                        holder.binding.likeIcon.setImageResource(R.drawable.ic_unliked);
                    }
                }
            });

            holder.binding.likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.isLiked = !holder.isLiked;
                    //See Video First !!
                    if (holder.isLiked) {
                        holder.binding.likeIcon.setImageResource(R.drawable.ic_liked);
                        fStore.collection("recipesLikes").document(recipe.getRecipeId()).collection("Likes").document(FirebaseAuth.getInstance().getUid()).set(new Likes(1));
                        recipe.increamentLike();
                        fStore.collection("recipesDetails").document(recipe.getRecipeId()).set(recipe);

                    } else {
                        holder.binding.likeIcon.setImageResource(R.drawable.ic_unliked);
                        fStore.collection("recipesLikes").document(recipe.getRecipeId()).collection("Likes").document(FirebaseAuth.getInstance().getUid()).set(new Likes(0));
                        recipe.disLike();
                        fStore.collection("recipesDetails").document(recipe.getRecipeId()).set(recipe);
                    }
                    notifyDataSetChanged();
                }
            });
        }
        holder.binding.RecipeImage.setImageDrawable(null);
        if(recipe.getRecipeImageUrl()!=null) {
            Glide.with(this.context).load(recipe.getRecipeImageUrl()).into(holder.binding.RecipeImage);
        }else{
            Glide.with(this.context).load(R.drawable.ic_post).into(holder.binding.RecipeImage);
        }
//--Add other Details--//

        if (recipe.getRecipeId()!=null) {

            fStore.collection("usersDetails").document(FirebaseAuth.getInstance().getUid()).collection("favourites").document(recipe.getRecipeId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        holder.isStared=true;
                        holder.binding.bookIcon.setImageResource(R.drawable.ic_yellowstar);
                    }else{
                        holder.isStared=true;
                        holder.binding.bookIcon.setImageResource(R.drawable.ic_star);
                    }
                }
            });

        }

        if (recipe.getRecipeId()!=null) {
            holder.binding.bookIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.isStared = !holder.isStared;
                    if (holder.isStared) {
                        holder.binding.bookIcon.setImageResource(R.drawable.ic_yellowstar);
                        fStore.collection("usersDetails").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("favourites").document(recipe.getRecipeId()).set(recipe);
                    }else {
                        holder.binding.bookIcon.setImageResource(R.drawable.ic_star);
                        fStore.collection("usersDetails").document(FirebaseAuth.getInstance().getUid()).collection("favourites").document(recipe.getRecipeId()).delete();
                    }
                }
            });
        }
    }

    private void shareRecipe(Recipes recipe) {
        String recipeText = recipe.toShareString();

        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        String shareBody = recipeText;
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        /*Fire!*/
        context.startActivity(Intent.createChooser(intent, "Share Recipe Via"));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder{
        ItemRecipeBinding binding;
        RecyclerView ingredientsTags;
        RecyclerView moodsTags;
        boolean isLiked=false;
        boolean isStared=false;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecipeBinding.bind(itemView);
            ingredientsTags = binding.tagsHorizontalScrollView ;
            moodsTags = binding.moodsTagsHorizontalScrollView;
//

            //imp to define here as onBindView Call every its Visible whereas its store after 1st time
            ingredientsTags.setItemViewCacheSize(20);
            ingredientsTags.setDrawingCacheEnabled(true);
            ingredientsTags.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


            moodsTags.setItemViewCacheSize(20);
            moodsTags.setDrawingCacheEnabled(true);
            moodsTags.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        }
    }

    public String getLastPostId(){
        return recipes.get(recipes.size()-1).getRecipeId();
    }
}
