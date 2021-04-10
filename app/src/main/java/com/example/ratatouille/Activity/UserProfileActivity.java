package com.example.ratatouille.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ratatouille.Adapters.RecipeViewAdapter;
import com.example.ratatouille.Authentication.Login;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.Models.User;
import com.example.ratatouille.R;
import com.example.ratatouille.databinding.ActivityUserProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView imgProfile;
    private ImageView imgIcLogout, imgIcEditProfile;
    private TextView txtUsername;
    private TextView txtUserVeg;
    private TextView txtUserRegion;
    private TextView txtUserMood;

    private Uri profileImageUri =null;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private String userName, userVeg, userRegion, userMood;
    private String userProfileImageUrl;

    private FirebaseAuth firebaseAuth;
    //    private FirebaseFirestore db;
    private DocumentReference dbRef;

    private StorageReference storageReference;          //storage
    String storagePath = "Users_Profile_Imgs/";         //path where profile images of users will be stored


    String[] cameraPermissions;
    String[] storagePermissions;

    private ProgressDialog pd;

    User user;

    FirebaseFirestore fstore;
    private FirebaseUser fUser;
    ArrayList<Recipes> recipes;
    ActivityUserProfileBinding binding;
    RecipeViewAdapter recipeViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        db = FirebaseFirestore.getInstance();
        fstore=StartActivity.fStore;
        firebaseAuth = FirebaseAuth.getInstance();
        fUser = firebaseAuth.getCurrentUser();
        user=StartActivity.user;
        dbRef = fstore.collection("usersDetails").document(fUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        imgProfile = (CircleImageView)findViewById(R.id.profilepageProfileImage);
        imgIcLogout = (ImageView)findViewById(R.id.iclogout);
        imgIcEditProfile = (ImageView)findViewById(R.id.edit_profile);

        pd = new ProgressDialog(UserProfileActivity.this);

        txtUsername = (TextView)findViewById(R.id.profilePageUserName);
        txtUserVeg = (TextView)findViewById(R.id.profilePageVeg);
        txtUserRegion = (TextView)findViewById(R.id.profilePageRegion);
        txtUserMood = (TextView)findViewById(R.id.profilePageMood);

        dbRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {

                                userName = document.getString("name");
                                Log.d("User Name -----  ", userName);
                                txtUsername.setText(userName);

                                userVeg = (document.getBoolean("veg")) ? "Vegetarian" : "Non-Vegetarian";
                                Log.d("User Veg -----  ", userVeg);
                                txtUserVeg.setText(userVeg);

                                userRegion = document.getString("reg_food");
                                Log.d("User Region -----  ", userRegion);
                                txtUserRegion.setText(userRegion);

                                userMood = document.getString("sp_sw_sr");
                                Log.d("User Mood -----  ", userMood);
                                txtUserMood.setText(userMood);

                                userProfileImageUrl = document.getString("image");

                                if(userProfileImageUrl != null){
                                    Log.d("ProfileImageUrl --->  ", userProfileImageUrl);
                                    try{
                                        //if image is recieved then set
                                        Picasso.get().load(userProfileImageUrl).into(imgProfile);
                                    } catch (Exception e) {
                                        //if there is any exception while getting image then set default
                                        Picasso.get().load(R.drawable.man).into(imgProfile);
                                    }
                                } else {
                                    Picasso.get().load(R.drawable.man).into(imgProfile);
                                }

                            } else {
                                Log.d("User Name -----  ", "Not found !!!!");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });


        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imgIcLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserProfileActivity.this, "Logout icon clicked ! ", Toast.LENGTH_SHORT).show();

                FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
                if(mFirebaseUser != null) {

                    Toast.makeText(UserProfileActivity.this, "Logout icon clicked by - " + mFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    logout(view);
                    Toast.makeText(UserProfileActivity.this, "Logout success", Toast.LENGTH_SHORT).show();
                }
                //to finish current activity as well as all the activities in the stack, and start login activity
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        imgIcEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });

        recipes=new ArrayList<Recipes>();
        recipeViewAdapter = new RecipeViewAdapter(this,recipes,fstore);
        binding.savedRecipes.setLayoutManager(new LinearLayoutManager(this));
        binding.savedRecipes.setAdapter(recipeViewAdapter);
        loadSavedRecipes();
    }

    private void showChangePasswordDialog() {

        //inflate layout for dialog to update password
        View view = LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.dialog_update_password, null);

        EditText passwordEt = view.findViewById(R.id.passwordEt);
        EditText newPasswordEt = view.findViewById(R.id.newPasswordEt);
        Button updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);

        pd.setMessage("Updating Password...");

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setView(view);  //set view to dialog

        AlertDialog dialog = builder.create();
        dialog.show();

        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate data
                String oldPassword = passwordEt.getText().toString().trim();
                String newPassword = newPasswordEt.getText().toString().trim();
                if(TextUtils.isEmpty(oldPassword)){
                    Toast.makeText(UserProfileActivity.this, "Enter your current password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPassword.length()<6){
                    Toast.makeText(UserProfileActivity.this, "Password length must be atleast 6 characters....", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                updatePassword(oldPassword, newPassword);
            }
        });

    }

    private void updatePassword(String oldPassword, String newPassword) {
        pd.show();

        //before changing the password re-authenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(fUser.getEmail(), oldPassword);
        fUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //successfully authenticated, begin update

                        fUser.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //password updated successfully
                                        pd.dismiss();
                                        Toast.makeText(UserProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed to update password, show reason
                                        pd.dismiss();
                                        Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //authentication failed, show reason
                        pd.dismiss();
                        Toast.makeText(UserProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        user=StartActivity.user;
        recipes=new ArrayList<Recipes>();
        recipeViewAdapter = new RecipeViewAdapter(this,recipes,fstore);
        binding.savedRecipes.setLayoutManager(new LinearLayoutManager(this));
        binding.savedRecipes.setAdapter(recipeViewAdapter);
        loadSavedRecipes();
    }

    void loadSavedRecipes(){
        if(fstore!=null) {
            fstore.collection("usersDetails").document(user.getUid()).collection("favourites").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    recipes.clear();
                    if (task.isSuccessful()) {
                        Log.d("Profile Activity :", "Recipes Size : " + task.getResult().size());
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            recipes.add(documentSnapshot.toObject(Recipes.class));
                        }
                        recipeViewAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void showEditProfileDialog() {
        /*Show dialog containing options
         *  1) Edit Profile Picture
         *  2) Edit Name
         * 3) Edit Password
         * */

        String options[] = {"Edit Profile Picture", "Edit Name", "Edit Password", "Edit Preferences"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(which == 0){
                    //Edit profile picture clicked
                    pd.setMessage("Updating Profile Picture...");
                    showImagePickerDialog();

                } else if (which == 1) {
                    //Edit Name clicked
                    pd.setMessage("Updating Name...");
                    showNamePhoneUpdateDialog("name");

                } else if (which == 2){
                    //Edit Password clicked
                    pd.setMessage("Updating Password...");
                    showChangePasswordDialog();
                } else if(which == 3) {
                    //Edit Preferences clicked
                    pd.setMessage("Updating Preferences...");
                    startActivity(new Intent(getApplicationContext(), Survey_Form.class));
                }
            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(String key) {

        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Update "+ key);

        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(UserProfileActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        //add edit text
        EditText editText = new EditText(UserProfileActivity.this);
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add button in dialog to update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate whether user has entered something or not
                if(!(TextUtils.isEmpty(value))) {
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    Log.d("User uid", user.getUid());

                    dbRef.update(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //updated, dismiss progress
                                    pd.dismiss();
                                    Toast.makeText(UserProfileActivity.this, "Updated "+ key , Toast.LENGTH_SHORT).show();
                                    txtUsername.setText(value);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed, dismiss progress, get and show error message
                                    pd.dismiss();
                                    Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Toast.makeText(UserProfileActivity.this, "Please enter "+ key, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //add button in dialog to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create and show dialog
        builder.create().show();

    }

    public void logout(View view){
        firebaseAuth.signOut();
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera" , "Gallery"};
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture Source : - ");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        PickFromCamera();
                    }
                }else if(which==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        PickFromGallery();
                    }

                }
            }
        });

        builder.create().show();
    }

    private void PickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void PickFromCamera() {
        ContentValues cv = new ContentValues();
        profileImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkCameraPermission(){
        boolean resultCamera = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean resultStorage = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return resultCamera && resultStorage;
    }

    private boolean checkStoragePermission(){
        boolean resultStorage = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return resultStorage;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    boolean storageAccepted = (grantResults[1] == PackageManager.PERMISSION_GRANTED);
                    if(cameraAccepted && storageAccepted){
                        PickFromCamera();
                    }else{
                        Toast.makeText(this,"Camera & Storage Permission neccessary",Toast.LENGTH_SHORT).show();
                    }
                }else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    if(storageAccepted){
                        PickFromGallery();
                    }else{
                        Toast.makeText(this,"Storage Permission is neccessary",Toast.LENGTH_SHORT).show();
                    }
                }else {

                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                profileImageUri =data.getData();

                uploadProfilePhoto(profileImageUri);

//                imgProfile.setImageURI(profileImageUri);

                Toast.makeText(this,"Image Changed from Gallery",Toast.LENGTH_SHORT).show();
            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE){

                uploadProfilePhoto(profileImageUri);

//                imgProfile.setImageURI(profileImageUri);
                Toast.makeText(this,"Image Changed from Camera",Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(this,"Something went wrong ",Toast.LENGTH_SHORT).show();

        }
    }

    private void uploadProfilePhoto(Uri uri) {

        //show progress
        pd.show();

        //Path and name of image to be stored in firebase storage
        String filePathAndName = storagePath + "_" + user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage, now get it's url and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and url is recieved
                        if(uriTask.isSuccessful()){
                            //image uploaded
                            //add/update url in user's database
                            HashMap<String, Object> results = new HashMap<>();
                            results.put("image", downloadUri.toString());

                            dbRef.update(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //url in database of user is added successfully
                                            //dismiss progress bar
                                            pd.dismiss();
                                            imgProfile.setImageURI(profileImageUri);
                                            Toast.makeText(UserProfileActivity.this, "Image updated successfully!!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //error in adding url in database of user
                                            //dismiss progress bar
                                            pd.dismiss();
                                            Toast.makeText(UserProfileActivity.this, "Error in updating image!!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            //error
                            pd.dismiss();
                            Toast.makeText(UserProfileActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //there was some error, show error and dismiss progress dialog
                        pd.dismiss();
                        Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}