package com.example.ratatouille.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.example.ratatouille.Adapters.RecipeViewAdapter;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.Models.User;
import com.example.ratatouille.R;
import com.example.ratatouille.databinding.ActivityStartBinding;
import com.example.ratatouille.databinding.ActivityUserProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    FirebaseFirestore fstore;
    User user;
    ArrayList<Recipes> recipes;
    ActivityUserProfileBinding binding;
    RecipeViewAdapter recipeViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fstore=StartActivity.fStore;
        user=StartActivity.user;
        recipes=new ArrayList<Recipes>();
        recipeViewAdapter = new RecipeViewAdapter(this,recipes,fstore);
        binding.savedRecipes.setLayoutManager(new LinearLayoutManager(this));
        binding.savedRecipes.setAdapter(recipeViewAdapter);
        loadSavedRecipes();
    }

    void loadSavedRecipes(){
        if(fstore!=null) {
            fstore.collection("usersDetails").document(user.getUid()).collection("favourites").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    recipes.clear();
                    if (task.isSuccessful()) {
                        Log.d("Profile Activity :", "Recipes Size : " + task.getResult().size());
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            recipes.add(documentSnapshot.toObject(Recipes.class));
                        }
                        recipeViewAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}