package com.example.ratatouille.Fragments;

import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.ratatouille.Activity.StartActivity;
import com.example.ratatouille.Adapters.RecipeViewAdapter;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.R;
import com.example.ratatouille.databinding.CooknowFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class CookNowFragment extends Fragment {

    private static final String[] INGREDIENTS = new String[]{
            "butter", "cocoa", "eggs", "flour", "sugar", "whiteSugar"
    };

    private static final String[] MOODS = new String[]{
            "Spicy", "Sweet", "Desert", "fastFood"
    };

    public RecipeViewAdapter getRecipeViewAdapter() {
        return recipeViewAdapter;
    }

    private RecipeViewAdapter recipeViewAdapter;

    private ArrayList<Recipes> recipes;
    FirebaseFirestore fstore;
    String TAG = "Cook Now Fragment";

    private static boolean firstQuery = true;
    private static  boolean timeChanged = false, ingredientsChanged = false, moodChanged = false,
            ingredientsAbsChanged = false;
    private static int timeVal;
    private static String ingredientVal, ingredientAbsVal, moodVal;

    HashMap<Recipes, HashSet<String >> resultSet = new HashMap<>();

    // Getter for reipes Arraylist
    public ArrayList<Recipes> getRecipes() {
        return recipes;
    }

    private static CookNowFragment singleInstance;

    //static method to create instance of singleton class
    public static CookNowFragment getInstance(){
        if(singleInstance==null)
            singleInstance = new CookNowFragment();
        return singleInstance;
    }

    CooknowFragmentBinding binding;
    // ChipItemBinding chipItemBinding;

    public CookNowFragment() {
        resultSet = new HashMap<>();
        timeChanged = false; ingredientsChanged = false; ingredientsAbsChanged = false; moodChanged = false;
        firstQuery = true;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fstore = StartActivity.fStore;
        binding = DataBindingUtil.inflate(inflater, R.layout.cooknow_fragment, container, false);

        // chipItemBinding=DataBindingUtil.inflate(inflater,R.layout.chip_item,container,false);

        ArrayAdapter<String> moodarrayAdapter = new ArrayAdapter<String>(container.getContext(),
                android.R.layout.simple_list_item_1, MOODS);

        ArrayAdapter<String> ingredientsarrayAdapter = new ArrayAdapter<String>(container.getContext(),
                android.R.layout.simple_list_item_1, INGREDIENTS);
        binding.moodautocompleteedittext.setAdapter(moodarrayAdapter);

        binding.ingredientsautocompleteedittext.setAdapter(ingredientsarrayAdapter);

        binding.btnAddMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String moodtag = binding.moodautocompleteedittext.getText().toString().trim();
                binding.moodautocompleteedittext.getText().clear();
                LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());

                Chip chip = (Chip) inflater.inflate(R.layout.item_mood_tag, null, false);
                chip.setText(moodtag);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.allTagsGroup.removeView(view);
                    }
                });

                String temp = chip.getText().toString();
                if (!(temp.length() == 0)) {
                    binding.allTagsGroup.addView(chip);
                    moodChanged = true;
                    moodVal = temp;
                    getQueryResult();
                }

            }
        });

        binding.btnAddAbsIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String absIngredient = binding.absIngredientautocompleteedittext.getText().toString();
                binding.absIngredientautocompleteedittext.getText().clear();
                LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());

                Chip chip = (Chip) inflater.inflate(R.layout.item_ingredient_tag, null, false);
                chip.setText(absIngredient);
                chip.setTextColor(Color.RED);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.allTagsGroup.removeView(view);
                    }
                });

                String temp = chip.getText().toString();
                if (!(temp.length() == 0)) {
                    binding.allTagsGroup.addView(chip);
                    ingredientAbsVal = temp;
                    ingredientsAbsChanged = true;
                    getQueryResult();
                }
            }
        });


        binding.btnAddIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ingtag = binding.ingredientsautocompleteedittext.getText().toString().trim();
                binding.ingredientsautocompleteedittext.getText().clear();
                LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());

                Chip chip = (Chip) inflater.inflate(R.layout.item_ingredient_tag, null, false);
                chip.setText(ingtag);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.allTagsGroup.removeView(view);
                    }
                });

                String temp = chip.getText().toString();
                if (!(temp.length() == 0)) {
                    binding.allTagsGroup.addView(chip);
                    ingredientsChanged = true;
                    ingredientVal = temp;
                    getQueryResult();
                }

            }
        });

        binding.cooktimeenterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cooktime = binding.cooktimeedittxt.getText().toString();
                binding.cooktimetxtview.setText("Cooking Time : "+cooktime);
                binding.cooktimeedittxt.getText().clear();
                timeChanged = true;
                timeVal = Integer.parseInt(cooktime);
                getQueryResult();
            }
        });

        recipes = new ArrayList<Recipes>();

//        // Sample Posts List
//        for (int i = 0; i < 10; i++) {
//            Recipe recipe = new Recipe();
//            recipe.setChefName("Jay");
//            recipes.add(recipe);
//        }

        recipeViewAdapter = new RecipeViewAdapter(this.getContext(), recipes, fstore);
        binding.CookNowRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.CookNowRecyclerView.setAdapter(recipeViewAdapter);

        return binding.getRoot();

    }



    public void getQueryResult(){
        Toast.makeText(getContext(), resultSet.size() + " " , Toast.LENGTH_SHORT).show();
        if(firstQuery){
            recipes.clear();
            Query query = null;
            fstore = FirebaseFirestore.getInstance();
            CollectionReference recipeReference = fstore.collection("recipesDetails");
            if(ingredientsChanged){
                query = recipeReference.whereArrayContains("ingredients", ingredientVal);

                ingredientsChanged = false;
            }else if(ingredientsAbsChanged){
                query = recipeReference.whereNotIn("ingredients", Arrays.asList(ingredientAbsVal));
                Log.d("Tag----------------->",ingredientAbsVal);

                ingredientsAbsChanged = false;
            }else if(moodChanged){
                query = recipeReference.whereArrayContains("moods", moodVal);
                moodChanged = false;
            }else if(timeChanged){
                query = recipeReference.whereLessThanOrEqualTo("cookTimeMin", timeVal);
                timeChanged = false;
            }
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipes recipe = document.toObject(Recipes.class);
                            recipes.add(recipe);
                            resultSet.put(recipe, new HashSet<String>());
                            for(String s: recipe.getIngredients()){
                                resultSet.get(recipe).add(s);
                            }
                            Toast.makeText(getContext(), document.getId(), Toast.LENGTH_LONG).show();
                        }
                        recipeViewAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

            firstQuery = false;
        }else {
            ArrayList<Recipes> newRecipes = new ArrayList<>();
            Set<Map.Entry<Recipes, HashSet<String>>> entrySet = resultSet.entrySet();
            // Collection Iterator
            Iterator<Map.Entry<Recipes, HashSet<String>>> iterator = entrySet.iterator();

            if(ingredientsChanged){
                while (iterator.hasNext()) {
                    Map.Entry<Recipes, HashSet<String >> mapElement = iterator.next();
                    Recipes recipe = mapElement.getKey();
                    HashSet<String> set = mapElement.getValue();
                    if(set.contains(ingredientVal)){
                        newRecipes.add(recipe);
                    }else{
                        iterator.remove();
                    }
                }
                ingredientsChanged = false;
            }else if(ingredientsAbsChanged){

                while (iterator.hasNext()) {
                    Map.Entry<Recipes, HashSet<String>> mapElement = iterator.next();
                    Recipes recipe = mapElement.getKey();
                    HashSet<String> set = mapElement.getValue();
                    Toast.makeText(getContext(), ingredientAbsVal + "removed " , Toast.LENGTH_SHORT).show();
                    if (!set.contains(ingredientAbsVal)) {
                        newRecipes.add(recipe);
                    } else {
                        iterator.remove();
                    }
                }
                ingredientsAbsChanged = false;
            }else if (moodChanged){
                while (iterator.hasNext()) {
                    Map.Entry<Recipes, HashSet<String>> mapElement = iterator.next();
                    Recipes recipe = mapElement.getKey();
                    HashSet<String> set = mapElement.getValue();
                    if (set.contains(moodVal)) {
                        newRecipes.add(recipe);
                    } else {
                        iterator.remove();
                    }
                }
                moodChanged = false;
            }else if(timeChanged){
                while (iterator.hasNext()) {
                    Map.Entry<Recipes, HashSet<String>> mapElement = iterator.next();
                    Recipes recipe = mapElement.getKey();
                    HashSet<String> set = mapElement.getValue();
                    if (recipe.getCookTimeMin()<=timeVal) {
                        newRecipes.add(recipe);
                    } else {
                        iterator.remove();
                    }
                }
                timeChanged = false;
            }
            recipes.clear();
            // Toast.makeText(getContext(), " " + newRecipes.size(), Toast.LENGTH_SHORT).show();
            for(Recipes r: newRecipes){
                recipes.add(r);
            }
        }
        recipeViewAdapter.notifyDataSetChanged();
    }


}
