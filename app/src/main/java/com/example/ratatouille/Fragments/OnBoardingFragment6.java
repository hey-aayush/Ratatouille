package com.example.ratatouille.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ratatouille.Activity.Survey_Form;
import com.example.ratatouille.R;
import com.google.android.material.slider.Slider;

//Fragment for Onboarding screen6

public class OnBoardingFragment6 extends Fragment {

    private Slider slider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.onboarding_screen6,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        slider = (Slider)view.findViewById(R.id.slider3);

        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                float value = slider.getValue();

                Log.d("slider 3 value = ", value+"");

                //how much the user likes to try new dishes
                Survey_Form.newfood = (int)value;

            }
        });

    }

}
