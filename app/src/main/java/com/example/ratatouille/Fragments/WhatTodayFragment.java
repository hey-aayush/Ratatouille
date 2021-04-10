package com.example.ratatouille.Fragments;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.ratatouille.Activity.SurveyAnswer;
import com.example.ratatouille.Adapters.WhatTodayAdapter;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.Models.User;
import com.example.ratatouille.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class WhatTodayFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference dbRef;

    private String reg_food, sp_sw_sr;
    private int cooking, fastfood, health;
    private String qDiet;
    private boolean isVeg;

    private User userDetails;

    public static List<Recipes> recipes=new ArrayList<>();

    private static final String TAG = "WhatTodayFrag";

    ProgressBar progressBar;

    ViewPager viewPager;
    WhatTodayAdapter adapter;
   // List<what_today_model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    View RootView;


    public WhatTodayFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         RootView = inflater.inflate(R.layout.what_today_fragment, container, false);

        progressBar=RootView.findViewById(R.id.whats_today_progressbar);
       // Recipes demo=new Recipes();
        getRecipieData();

        check(RootView);

        Log.d(TAG, "after getrecipie");



        return RootView;
    }

    private void check(View view)
    {
        adapter = new WhatTodayAdapter(recipes, getContext());



        viewPager = RootView.findViewById(R.id.viewPager_what_today);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() -1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                }

                else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getRecipieData() {
       progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "getRecipieData Called");

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        dbRef = db.collection("usersDetails").document(user.getUid());
        CollectionReference recipesDbRef = db.collection("recipesDetails");
        Log.d(TAG, "User UID=" + user.getUid());

        dbRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                userDetails = document.toObject(User.class);

                                reg_food = userDetails.getReg_food();
                                sp_sw_sr = userDetails.getSp_sw_sr();
                                isVeg = userDetails.isVeg();
                                cooking = userDetails.getCooking();
                                fastfood = userDetails.getFastfood();
                                health = userDetails.getHealth();

                                Log.d(TAG, "user = " + userDetails.toString());

                                recipes = new ArrayList<>();

                                Query query;

                                if (isVeg) {
//                                    query = recipesDbRef.whereEqualTo("veg", true).whereArrayContains("moods", sp_sw_sr).orderBy("noOfLikes").orderBy("cookTimeMin").orderBy("noOfFavourites").limit(10);
                                    query = recipesDbRef.whereEqualTo("veg", true).whereArrayContains("moods", sp_sw_sr).orderBy("noOfLikes", Query.Direction.DESCENDING).orderBy("cookTimeMin");
                                } else {
                                    // non-veg , check for mood, and order by diet in ascending order "non-veg" < "veg"
                                    query = recipesDbRef.whereArrayContains("moods", sp_sw_sr).orderBy("veg").orderBy("noOfLikes", Query.Direction.DESCENDING).orderBy("cookTimeMin").limit(10);
                                }

                                query.get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "task result size = " + (task.getResult()).size());
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                                        Recipes recipe = new Recipes(document);
                                                        Log.d(TAG, "recipe = " + recipe.toString());
                                                        recipes.add(recipe);
                                                        Log.d(TAG, "recipes size = " + recipes.size());
//                                                            recipes.add(document.toObject(Recipe.class));
                                                    }

                                                    Recipes.RecipeCustomSortingComparator comparator = new Recipes.RecipeCustomSortingComparator();
                                                    comparator.setUserDetails(userDetails);
                                                    Collections.sort(recipes, comparator);
                                                    adapter.notifyDataSetChanged();
                                                    progressBar.setVisibility(View.GONE);
                                                    check(RootView);


                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d("WhatToday dbRef.get", " get failed with ", task.getException());
                        }
                    }
                });

        Log.d(TAG, "getRecipieData end");
    }
}
