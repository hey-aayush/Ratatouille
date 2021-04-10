package com.example.ratatouille.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

        userId=FirebaseAuth.getInstance().getUid();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:         // profile
                 StartProfileActivity();
                break;
            case R.id.nav_logout:          // for logout
                 SignOut();
                break;
            case R.id.nav_aboutus:         // for about us
                StartAboutusActivity();
                break;
            case R.id.nav_share:                //For sharing the app
                share();
                break;
            case R.id.nav_rate:         //For giving the rate to our app
                rateUs();
                break;

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void rateUs(){
        try{
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.example.ratatouille")));
        }catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.saurabh.socialdistancing")));
        }
    }
    private void share(){
        Intent shareintent = new Intent();
        shareintent.setAction(Intent.ACTION_SEND);
        shareintent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.example.ratatouille");
        shareintent.setType("text/plain");
        startActivity(Intent.createChooser(shareintent,"share via"));
    }

    private void SignOut() {

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

        FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
        if(mFirebaseUser != null) {

            Toast.makeText(this, "Logout txt clicked by - " + mFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            finishAffinity();
            firebaseAuth.signOut();
            Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));

        } else {
            finishAffinity();
            startActivity(new Intent(this, Login.class));
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