package com.example.ratatouille.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.ratatouille.Authentication.Login;
import com.example.ratatouille.Models.Recipes;
import com.example.ratatouille.Models.User;
import com.example.ratatouille.R;
import com.example.ratatouille.databinding.ActivityAddRecipeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class addRecipe extends AppCompatActivity {

    ActivityAddRecipeBinding binding;

    static String recipeName;
    static String recipeDescription;
    static ArrayList<String> ingredients;
    static ArrayList<String> moods;
    static String recipeImageUrl;
    static String chefId;
    static String chefName;
    static Uri recipeImageUri = null;
    static long timestamp;
    static String recipeId;
    static int cookingTime;
    static boolean isVeg;
    static String region;
    static int health;
    private Recipes recipe;

    String TAG = "Add Recipe Activity";

    FirebaseFirestore fStore;
    FirebaseStorage firebaseStorage;
    FirebaseAuth firebaseAuth;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    String[] cameraPermissions;
    String[] storagePermissions;

    private static final String[] INGREDIENTS = new String[]{
            "butter", "cocoa", "eggs", "flour", "sugar", "whiteSugar"
    };

    private static final String[] MOODS = new String[]{
            "Spicy", "Sweet", "Desert", "fastFood"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeImageUri = null;

        firebaseAuth = Login.mAuth;
        fStore = StartActivity.fStore;
        firebaseStorage = StartActivity.firebaseStorage;
        ;

        chefId = FirebaseAuth.getInstance().getUid();

        fStore.collection("usersDetails").document(chefId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                chefName = documentSnapshot.toObject(User.class).getName();
            }
        });

        ArrayAdapter<String> ingredientsarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, INGREDIENTS);
        binding.autoCompleteTextViewIngridient.setAdapter(ingredientsarrayAdapter);

        ingredients = new ArrayList<String>();

        binding.addIngridient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ingtag = binding.autoCompleteTextViewIngridient.getText().toString().trim();
                if (ingtag.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Tag can't be empty !", Toast.LENGTH_LONG).show();
                    return;
                }
                binding.autoCompleteTextViewIngridient.getText().clear();
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                Chip chip = (Chip) layoutInflater.inflate(R.layout.item_ingredient_tag, null, false);
                chip.setText(ingtag);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.ingredientsTagsGroup.removeView(view);
                    }
                });
                binding.ingredientsTagsGroup.addView(chip);
                ingredients.add(ingtag);
            }
        });

        binding.addCookingTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.CookingTimeText.setText(" Cooking Time : " + binding.autoCompleteTextViewCookingTime.getText().toString() + " mins");
                cookingTime = Integer.parseInt(binding.autoCompleteTextViewCookingTime.getText().toString());
            }
        });

        ArrayAdapter<String> moodarrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, MOODS);
        binding.autoCompleteTextViewsMoods.setAdapter(ingredientsarrayAdapter);

        moods = new ArrayList<String>();

        binding.addMoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ingtag = binding.autoCompleteTextViewsMoods.getText().toString().trim();
                if (ingtag.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Tag can't be empty !", Toast.LENGTH_LONG).show();
                    return;
                }
                binding.autoCompleteTextViewsMoods.getText().clear();
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                Chip chip = (Chip) layoutInflater.inflate(R.layout.item_mood_tag, null, false);
                chip.setText(ingtag);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.moodsTagsGroup.removeView(view);
                    }
                });
                binding.moodsTagsGroup.addView(chip);
                moods.add(ingtag);
            }
        });


        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        binding.addRecipeCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.addRecipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        binding.addHealthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                health=Integer.parseInt(binding.addHealtInput.getText().toString().trim());
                binding.CookingHealthText.setText("Health wise Rating : "+ ((int)health)%10+"/10");
            }
        });

        binding.addRegionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                region=binding.addRegionInput.getText().toString().trim().toLowerCase();
                binding.CookingRegionText.setText("Region : "+region);
            }
        });

        binding.addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Initiating to upload Post");
                binding.addRecipeProgressBar.setVisibility(View.VISIBLE);
                binding.addRecipeBtn.setVisibility(View.GONE);

                recipeName = binding.addRecipeTitleInput.getText().toString().trim();
                recipeDescription = binding.addRecipeDescriptionInput.getText().toString().trim();
                timestamp = (new Date()).getTime();

                isVeg=binding.isVegitarian.isChecked();

                Log.d(TAG, "Chef Id :" + chefId);

                recipe = new Recipes(recipeName, recipeDescription, chefId, chefName, timestamp, ingredients, moods, cookingTime,isVeg,health,region);

                fStore.collection("recipesDetails").add(recipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        recipe.setRecipeId(documentReference.getId());
                        if (recipeImageUri != null) {
                            StorageReference reference = firebaseStorage.getReference().child("RecipesImages").child(recipe.getRecipeId());
                            reference.putFile(recipeImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                recipe.setRecipeImageUrl(uri.toString());
                                                fStore.collection("recipesDetails").document(recipe.getRecipeId()).set(recipe);
                                                Log.d(TAG, "Recipe Added" + recipe.getRecipeId());
                                                binding.addRecipeProgressBar.setVisibility(View.GONE);
                                                binding.addRecipeBtn.setVisibility(View.VISIBLE);
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });

                        } else {
                            fStore.collection("recipesDetails").document(recipe.getRecipeId()).set(recipe);
                            binding.addRecipeProgressBar.setVisibility(View.GONE);
                            binding.addRecipeBtn.setVisibility(View.VISIBLE);
                            finish();
                        }

                        Log.d(TAG, "Recipe Uploaded ID : " + recipe.getRecipeId());
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Recipe Uploading Failed: " + recipe.getRecipeId());
                            }
                        });


            }
        });
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Post Image Source : - ");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        PickFromCamera();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
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
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void PickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Click your Dish");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Dish Description");
        recipeImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, recipeImageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkCameraPermission() {
        boolean resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resultCamera && resultStorage;
    }

    private boolean checkStoragePermission() {
        boolean resultStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resultStorage;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void checkUserStatus() {
        FirebaseUser curUser = firebaseAuth.getCurrentUser();
        if (curUser == null) {
            Toast.makeText(this, "Please Login to Make Posts", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, StartActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    boolean storageAccepted = (grantResults[1] == PackageManager.PERMISSION_GRANTED);
                    if (cameraAccepted && storageAccepted) {
                        PickFromCamera();
                    } else {
                        Toast.makeText(this, "Camera & Storage Permission neccessary", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    if (storageAccepted) {
                        PickFromGallery();
                    } else {
                        Toast.makeText(this, "Storage Permission is neccessary", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                recipeImageUri = data.getData();

                binding.addRecipeImage.setImageURI(recipeImageUri);
                Toast.makeText(this, "Image added from Gallery", Toast.LENGTH_SHORT).show();
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                binding.addRecipeImage.setImageURI(recipeImageUri);
                Toast.makeText(this, "Image add from Camera", Toast.LENGTH_SHORT).show();

            }
        } else {
            recipeImageUri = null;
            Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show();

        }
    }
}
