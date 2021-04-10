package com.example.ratatouille.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ratatouille.Authentication.Login;
import com.example.ratatouille.Fragments.CookNowFragment;
import com.example.ratatouille.Fragments.PostFragment;
import com.example.ratatouille.Fragments.WhatTodayFragment;
import com.example.ratatouille.Models.User;
import com.example.ratatouille.R;
import com.example.ratatouille.databinding.ActivityStartBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityStartBinding binding;
    private ImageView nav_header_img;
    private TextView nav_header_name;
    private TextView nav_header_email;

    static public User user;
    static public String userId;
    static public FirebaseFirestore fStore;
    static public FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        userId=Login.mAuth.getUid();
        fStore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);
        View header = binding.navView.getHeaderView(0);

        //nav_header_img= (ImageView) header.findViewById(R.id.nav_header_img);//when user img will get uploaded then it will be required
        nav_header_email=(TextView) header.findViewById(R.id.nav_header_email);
        nav_header_name=(TextView) header.findViewById(R.id.nav_header_name);

        fStore.collection("usersDetails").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user=documentSnapshot.toObject(User.class);
                nav_header_name.setText(user.getName());
                nav_header_email.setText(user.getEmail());
            }
        });


        // mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostFragment()).commit();//start fragment
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragmentSelected = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_post:
                            fragmentSelected = new PostFragment();
                            break;
                        case R.id.navigation_cook_now:
                            fragmentSelected = new CookNowFragment();
                            break;
                        case R.id.navigation_what_today:
                            fragmentSelected = new WhatTodayFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentSelected).commit();

                    return true;
                }
            };


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                 StartProfileActivity();
                break;
            case R.id.nav_logout:
                 SignOut();
                break;
            case R.id.nav_aboutus:
                StartAboutusActivity();
                break;

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void SignOut() {

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

        FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
        if(mFirebaseUser != null) {

            Toast.makeText(this, "Logout txt clicked by - " + mFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            // finish();
            finish();

        } else {
            startActivity(new Intent(this, Login.class));
            // finish();
            finish();
        }
    }

    private void StartProfileActivity()
    {

        Toast.makeText(this, "Profile icon clicked ! ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UserProfileActivity.class));

    }

    private void StartAboutusActivity()
    {

        Toast.makeText(this, "Profile icon clicked ! ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, AboutUsActivity.class));

    }
}