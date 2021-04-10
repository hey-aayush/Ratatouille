package com.example.ratatouille.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ratatouille.Activity.Survey_Form;
import com.example.ratatouille.R;

//Fragment for OnboardingScreen2

public class OnBoardingFragment2 extends Fragment {

    private RadioGroup radioGroup;
    private RadioButton radioBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.onboarding_screen2,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup = (RadioGroup)view.findViewById(R.id.radioGrp2);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioBtn = (RadioButton)view.findViewById(selectedId);
                Log.d("selected radio = ", radioBtn.getText().toString());

                //User prefers vegetarian or non-vegetarian food
                Survey_Form.veg_nonveg = radioBtn.getText().toString();

            }
        });

    }
}
