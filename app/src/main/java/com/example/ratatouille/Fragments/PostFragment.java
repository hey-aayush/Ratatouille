package com.example.ratatouille.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ratatouille.Activity.StartActivity;
import com.example.ratatouille.Activity.addRecipe;
import com.example.ratatouille.Adapters.RecipeViewAdapter;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.R;
import com.example.ratatouille.databinding.PostFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostFragment extends Fragment {

    String Tag = "Post Fragment";
    PostFragmentBinding binding;
    RecipeViewAdapter recipeViewAdapter;

    static ArrayList<Recipes> recipes;

    public PostFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.post_fragment,container,false);
        recipes = new ArrayList<Recipes>();

        recipeViewAdapter = new RecipeViewAdapter(this.getContext(),recipes, StartActivity.fStore);

        binding.RecipesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.RecipesRecyclerView.setItemViewCacheSize(20);
        binding.RecipesRecyclerView.setDrawingCacheEnabled(true);
        binding.RecipesRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.RecipesRecyclerView.setAdapter(recipeViewAdapter);

        loadRecipes();
        fetchLiveChanges();

        binding.addRecipeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), addRecipe.class));
            }
        });

        return binding.getRoot();
    }


    void loadRecipes(){
        StartActivity.fStore.collection("recipesDetails").orderBy("timeStamp", Query.Direction.DESCENDING).limit(20)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            recipes.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(Tag, document.getId() + " => " + document.getData());
                                Recipes recipe = document.toObject(Recipes.class);
                                recipes.add(recipe);
                            }
                            recipeViewAdapter.notifyDataSetChanged();
                        }else{
                            Log.d(Tag, "Error getting documents: ", task.getException());
                            Toast.makeText(getContext(),"Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void fetchLiveChanges() {
        StartActivity.fStore.collection("recipesDetails").orderBy("timeStamp", Query.Direction.DESCENDING).limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(Tag, "Listen failed.", e);
                            return;
                        }

                        recipes.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc != null) {
                                Recipes recipe = new Recipes();
                                recipe = doc.toObject(Recipes.class);
                                if (recipe.getRecipeId() != null) {
                                    recipes.add(recipe);
                                }
                            }
                        }
                        recipeViewAdapter.notifyDataSetChanged();
                    }
                });
    }
}
