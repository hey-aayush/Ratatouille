package com.example.ratatouille.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.ratatouille.Activity.Survey_Form;
import com.example.ratatouille.Models.User;
import com.example.ratatouille.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private static final String TAG = "TAG";
    private EditText mFullName,mEmail,mPassword,mConfirmPassword;
    private Button mRegisterBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore fStore;
    private String userID;


    //we are using the firebase email and password authentication to verify the user
    //And using the firebase cloudFirestore to store the data about a person

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmpassword);
        mRegisterBtn = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.registrationProgressBar);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                final String confirmPassword    = mConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(email)){     //checking the textinput layout for email is empty or not
                    mEmail.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }
                if(password.length() < 6){                    //checking the length of password
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                if(!(password.equals(confirmPassword))){
                    mPassword.setError("Password does not match");  //checking password equal to confirm password or not
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //mRegisterBtn.setVisibility(View.GONE);


                // register the user in firebase
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);

                        if(task.isSuccessful()){
                            // send verification link
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                    mRegisterBtn.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());

                                    mRegisterBtn.setVisibility(View.VISIBLE);
                                }
                            });
                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();   //getting the Uid of user

                            //Storing user Details in FireStores...
                            DocumentReference usersDetailsReference = fStore.collection("usersDetails").document(userID);
                            User newUser = new User(fullName,userID,email);
                            usersDetailsReference.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                    progressBar.setVisibility(View.GONE);
                                    mRegisterBtn.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                    progressBar.setVisibility(View.GONE);
                                    mRegisterBtn.setVisibility(View.VISIBLE);
                                }
                            });
//                                startActivity(new Intent(getApplicationContext(),Login.class));

                            DocumentReference favuorites = fStore.collection("users").document(userID);
                            Map<String, Object> favuoriteFoods = new HashMap<>();
                            favuorites.set(favuoriteFoods).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "favuorites is added " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "failure in favuorites " + e.toString());
                                }
                            });

                            // for user Data
                            DocumentReference userDataReference = fStore.collection("users").document(userID);
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("personalInfo", usersDetailsReference);
                            userData.put("favuorites", favuorites);
                            userDataReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "userData is set " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "userData failure: " + e.toString());
                                }
                            });

                            progressBar.setVisibility(View.GONE);
                            mRegisterBtn.setVisibility(View.VISIBLE);

//                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                            startActivity(new Intent(getApplicationContext(), Survey_Form.class));
                        }else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),Login.class));
    }
    public void onalready(View view) {
        startActivity(new Intent(Register.this,Login.class));
    }
}
