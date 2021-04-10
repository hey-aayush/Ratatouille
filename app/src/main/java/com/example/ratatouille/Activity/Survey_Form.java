package com.example.ratatouille.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ratatouille.Fragments.OnBoardingFragment0;
import com.example.ratatouille.Fragments.OnBoardingFragment1;
import com.example.ratatouille.Fragments.OnBoardingFragment2;
import com.example.ratatouille.Fragments.OnBoardingFragment3;
import com.example.ratatouille.Fragments.OnBoardingFragment4;
import com.example.ratatouille.Fragments.OnBoardingFragment5;
import com.example.ratatouille.Fragments.OnBoardingFragment6;
import com.example.ratatouille.Fragments.OnBoardingFragment7;
import com.example.ratatouille.Models.User;
import com.example.ratatouille.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// The SurveyForm Fragment

public class Survey_Form extends FragmentActivity {


    //Survey of some preferences of the user, having some default values
    private static final String TAG = "TAG";
    public static String veg_nonveg = "vegetarian", sp_sw_sr = "spicy", reg_food = "north india" ;
    public static int health = 5, fastfood = 5, newfood = 5, cooking = 5 ;

    private FirebaseFirestore fstore;

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;        //linear layout for dot indicators at bottom of onboarding screen

    private TextView[] mDots;               //dot indicators at bottom of onboarding screen


    private Button mSkipBtn;                //skip button
    private Button mNextBtn;                //next button
    private Button mBackBtn;                //back button

    private int mCurrentPage;               //current page

    private FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {                                                     //position decides which fragment to display
                case 0 : return new OnBoardingFragment0();
                case 1 : return new OnBoardingFragment1();
                case 2 : return new OnBoardingFragment2();
                case 3 : return new OnBoardingFragment3();
                case 4 : return new OnBoardingFragment4();
                case 5 : return new OnBoardingFragment5();
                case 6 : return new OnBoardingFragment6();
                case 7 : return new OnBoardingFragment7();
                default: return null;
            }
        }

        @Override
        public int getCount() {                                                      //count of number of onboarding screens
            return 8;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey__form);

        mSlideViewPager = (ViewPager)findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout)findViewById(R.id.dotsLayout);

        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mBackBtn = (Button) findViewById(R.id.prevBtn);
        mSkipBtn = (Button) findViewById(R.id.skipBtn);

        mSlideViewPager.setAdapter(adapter);

        addDotsIndicator(0);                                   //Initially create the dot indicators with position on first page

        mSlideViewPager.addOnPageChangeListener(viewListener);      //ViewPager listener

        mNextBtn.setOnClickListener(new View.OnClickListener() {        // Next button listener
            @Override
            public void onClick(View view) {

                Log.d("veg_nonveg = ", veg_nonveg);
                Log.d("spicy sweet sour = ", sp_sw_sr);
                Log.d("region = ", reg_food);
                Log.d("health conscious = ", String.valueOf(health));
                Log.d("fastfood = ", String.valueOf(fastfood));
                Log.d("newfood = ", String.valueOf(newfood));
                Log.d("cooking = ", String.valueOf(cooking));
                update();
                if(mCurrentPage == 8-1){                                                            //last page of survey completed
                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                    update();                                                                       //updating the preferences of user into database
                } else {
                    mSlideViewPager.setCurrentItem(mCurrentPage + 1);                               //if not last page, then go to next page
                    update();
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {        // Back button listener
            @Override
            public void onClick(View view) {                                                        //if not first page, then go to previous page
                mSlideViewPager.setCurrentItem(mCurrentPage-1);
            }
        });

        mSkipBtn.setOnClickListener(new View.OnClickListener() {        // Skip button listener
            @Override
            public void onClick(View view) {                                                        //skip the rest of the survey
                Log.d("veg_nonveg = ", veg_nonveg);
                Log.d("spicy sweet sour = ", sp_sw_sr);
                Log.d("region = ", reg_food);
                Log.d("health conscious = ", String.valueOf(health));
                Log.d("fastfood = ", String.valueOf(fastfood));
                Log.d("newfood = ", String.valueOf(newfood));
                Log.d("cooking = ", String.valueOf(cooking));
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                update();                                                                           //updating the preference of user into the database
            }
        });

    }

    // Selected page Dot indicator
    public void addDotsIndicator(int position){

        mDots = new TextView[4];                        // TextView for DotsIndicators
        mDotLayout.removeAllViews();                    // Old dots removed

        for (int i=0; i<mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.transparentWhite));

            mDotLayout.addView(mDots[i]);

        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }

    }

    // Listener for ViewPager
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position % 4);                 // Selected page dot indicator

            mCurrentPage = position;

            //Modifying Skip button for Compulsory questions
            if(position >= 0 && position <= 3){
                mSkipBtn.setEnabled(false);
                mSkipBtn.setVisibility(View.INVISIBLE);
            } else {
                mSkipBtn.setEnabled(true);
                mSkipBtn.setVisibility(View.VISIBLE);
            }

            //Modifying previous and next button for first and last onboarding screen
            if(position == 0){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("");

            } else if(position == 8-1){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);

                mNextBtn.setText("Finish");
                mBackBtn.setText("Back");

            } else {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("Back");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    // update the answer of survey in database
    public void update(){

        SurveyAnswer answer = new SurveyAnswer(veg_nonveg, sp_sw_sr, reg_food, health, fastfood, newfood, cooking);

        fstore = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getUid();
        DocumentReference userDataReference = fstore.collection("usersDetails").document(userID);
        userDataReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "DocumentSnapshot successfully retrieved!");
                        User user = documentSnapshot.toObject(User.class);
                        user.setUserSurvey(answer);
                        userDataReference.set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Error in updating userData " + e.toString());
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error in retrieving userData " + e.toString());
                    }
                });
    }
}