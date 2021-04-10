package com.example.ratatouille.Fragments;

import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;



public class CookNowFragment extends Fragment {

    private static final String[] INGREDIENTS = new String[]{
            "butter", "cocoa", "eggs", "flour", "sugar", "whiteSugar", "milk", "rava", "Chicken", "gram flour",
            "mustard", "tomato", "curry leaves", "garlic", "salt", "urad dal", "rice", "onion", "oil", "chana dal",
            "paneer", "potato", "cream", "garam masala", "lemon juice", "ginger", "garlic", "chillies", "lemon",
            "nuts", "cardamom", "bread", "rice flour", "Vermicelli Pudding", "peanut", "jaggery", "pumpkin", "lime",
            "yogurt", "termeric", "turmeric", "chana dal", "Tea leaves", "red kidney beans", "lentil", "ghee",
            "Ladies fingers",
    };

    private static final String[] MOODS = new String[]{
            "spicy", "sweet", "Desert", "fast food", "snack"
    };

    public RecipeViewAdapter getRecipeViewAdapter() {
        return recipeViewAdapter;
    }

    private RecipeViewAdapter recipeViewAdapter;
    boolean check = false;

    private ArrayList<Recipes> recipes;
    FirebaseFirestore fstore;
    String TAG = "Cook Now Fragment";
    private HashSet<String> ingredientPresent = new HashSet<>();
    private HashSet<String > ingredientAbsent = new HashSet<>();
    private HashSet<String> mood = new HashSet<>();
    private int minTime = Integer.MAX_VALUE;
    private HashMap<String, ArrayList<Recipes>> ingredientMap = new HashMap<>();
    HashSet<Recipes> recipeList = new HashSet<>();
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
        fstore = StartActivity.fStore;
        firstQuery();
        mood = new HashSet<>();
        ingredientPresent = new HashSet<>();
        ingredientAbsent = new HashSet<>();
        minTime = Integer.MAX_VALUE;
        check = false;
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
                String temp = chip.getText().toString();
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.allTagsGroup.removeView(view);
                        mood.remove(temp);
                        getQueryResult();
                    }
                });


                if (!(temp.length() == 0)) {
                    binding.allTagsGroup.addView(chip);
                    mood.add(temp);
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
                String temp = chip.getText().toString();
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.allTagsGroup.removeView(view);
                        ingredientAbsent.remove(temp);
                        getQueryResult();
                    }
                });

                if (!(temp.length() == 0)) {
                    binding.allTagsGroup.addView(chip);
                    ingredientAbsent.add(temp);
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
                String temp = chip.getText().toString();
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.allTagsGroup.removeView(view);
                        ingredientPresent.remove(temp);
                        getQueryResult();
                    }
                });


                if (!(temp.length() == 0)) {
                    binding.allTagsGroup.addView(chip);
                    ingredientPresent.add(temp);
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
                minTime = Integer.parseInt(cooktime);
                getQueryResult();
            }
        });

        binding.isVeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                check = !check;
                getQueryResult();
            }
        });
        recipes = new ArrayList<Recipes>();

        recipeViewAdapter = new RecipeViewAdapter(this.getContext(), recipes, fstore);
        binding.CookNowRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.CookNowRecyclerView.setAdapter(recipeViewAdapter);

        return binding.getRoot();

    }

    // Query for the first time from firebase to store recipes locally
    public void firstQuery(){
        fstore.collection("recipesDetails")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Recipes recipe = document.toObject(Recipes.class);
                                recipes.add(recipe);
                                recipeList.add(recipe);
                                for(String s: recipe.getIngredients()){
                                    if(!ingredientMap.containsKey(s))
                                        ingredientMap.put(s, new ArrayList<Recipes>());
                                    ingredientMap.get(s).add(recipe);
                                }
                            }
                            recipeViewAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    public void getQueryResult() {

        HashSet<Recipes> resultList = new HashSet<>();
        HashSet<Recipes> tempList = new HashSet<>();

        // for adding recipes according to ingredients which is present
        int count = 0;
        Toast.makeText(getContext(), ingredientPresent.size() + " ", Toast.LENGTH_SHORT).show();
        for(String ingredient : ingredientPresent){
            resultList = new HashSet<>();
            if(ingredientMap.containsKey(ingredient)){
                for(Recipes r: ingredientMap.get(ingredient)){
                    if(count==0)               // for count = 0 , tmpList is empty
                        resultList.add(r);
                    else{
                        if(tempList.contains(r)){
                            resultList.add(r);
                        }
                    }
                }
                tempList = resultList;
            }
            count++;
        }

        // when no ingredient chosen
        if(ingredientPresent.size()==0){
            for(Recipes r: recipeList){
                resultList.add(r);
            }
        }

        // HashSet for removing unwanted recipes
        HashSet<Recipes> removeList = new HashSet<>();

        // for filtering according to mood list
        for(String s : mood){
            for(Recipes r: resultList){
                if(!r.getMoods().contains(s)){
                    removeList.add(r);
                }
            }

        }
        // Removing unwanted recipes
        for(Recipes r: removeList){
            if(resultList.contains(r))
                resultList.remove(r);
        }
        removeList.clear();
        // for removing ingredients which is absent
        for(String ingredient: ingredientAbsent){
            if(ingredientMap.containsKey(ingredient)){
                for(Recipes r: ingredientMap.get(ingredient)){
                    if(resultList.contains(r)){
                        removeList.add(r);
                    }
                }
            }
        }
        for(Recipes r: removeList){
            if(resultList.contains(r))
                resultList.remove(r);
        }
        removeList.clear();
        // for checking minTime to cook and mood
        for(Recipes r: recipeList){
            if(r.getCookTimeMin()>minTime)
                removeList.add(r);
        }
        //Toast.makeText(getContext(), recipes.size() + " reciepes.size", Toast.LENGTH_SHORT).show();
        for(Recipes r: removeList){
            if(resultList.contains(r))
                resultList.remove(r);
        }
        removeList.clear();
        //Toast.makeText(getContext(), resultList.size() + " 111 reciepes.size", Toast.LENGTH_SHORT).show();

        Toast.makeText(getContext(), " " + check, Toast.LENGTH_SHORT).show();

        // for filtering according to veg/non-veg
        for(Recipes r: resultList){
            if(check){
                if(r.isVeg()){
                    removeList.add(r);
                }
            } else{
                if(!r.isVeg()){
                    removeList.add(r);
                }
            }
        }
        // adding filtered recipes into recipes list
        for(Recipes r: removeList){
            if(resultList.contains(r))
                resultList.remove(r);
        }

        removeList.clear();
        recipes.clear();

        for(Recipes r: resultList){
            recipes.add(r);
        }
        recipeViewAdapter.notifyDataSetChanged();

    }
}
