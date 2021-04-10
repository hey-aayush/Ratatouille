package com.example.ratatouille.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ratatouille.R;
import com.example.ratatouille.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {
     ActivityStartBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}