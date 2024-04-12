package com.nhathuy.connectify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.emoji.widget.EmojiEditText;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nhathuy.connectify.databinding.ActivitySignDetailBinding;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SignDetailActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;


    ConstraintLayout container;
    private ImageView profilePic;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EmojiEditText bioEditText;

    private MaterialButton finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SignUpActivity);
        setContentView(R.layout.activity_sign_detail);

        mAuth=FirebaseAuth.getInstance();
        mFirestore=FirebaseFirestore.getInstance();
        mStorage=FirebaseStorage.getInstance();
        storageReference=mStorage.getReference();


        container=findViewById(R.id.sigup_extra_container);
        profilePic=findViewById(R.id.signup_detail_pic);
        firstNameEditText=findViewById(R.id.ed_first_name);
        lastNameEditText=findViewById(R.id.ed_last_name);
        bioEditText=findViewById(R.id.ed_signup_bio);
        finish=findViewById(R.id.button_finish);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                try {
                    StorageReference userRef=storageReference.child(mAuth.getCurrentUser().getDisplayName() +"/"+ imageFiles.get(0).getName());
                    UploadTask uploadTask;
                    uploadTask = userRef.putFile(Uri.fromFile(new Compressor(SignDetailActivity.this).compressToFile(imageFiles.get(0))));

                    Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return userRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUri=task.getResult();
                                UserProfileChangeRequest profileUpdate=new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();

                                mAuth.getCurrentUser().updateProfile(profileUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Glide.with(getApplicationContext()).load(downloadUri.toString()).into(profilePic);
                                        finish.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if(firstNameEditText.getText().length()!=0||lastNameEditText.getText().length()!=0||bioEditText.getText().length()!=0){
                                                    Map<String,Object> userDoc=new HashMap<>();
                                                    userDoc.put("first_name",firstNameEditText.getText().toString());
                                                    userDoc.put("last_name",lastNameEditText.getText().toString());
                                                    userDoc.put("bio",bioEditText.getText().toString());
                                                    userDoc.put("profile",downloadUri.toString());

                                                    mFirestore.collection("users").document(mAuth.getCurrentUser().getDisplayName()).update(userDoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void openGallery(int i) {
        EasyImage.openGallery(this,i);
    }
}