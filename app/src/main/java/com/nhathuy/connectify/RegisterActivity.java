package com.nhathuy.connectify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nhathuy.connectify.databinding.ActivityRegisterBinding;
import com.nhathuy.connectify.interfaces.OnEmailCheckListener;
import com.nhathuy.connectify.interfaces.OnUserNameCheckListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SignUpActivity);
        binding=ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();


        Window w=getWindow();
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setOnEditActionListener();
        setBtnRegister();
    }

    private void setBtnRegister() {
        binding.btnSignup.setOnClickListener(v-> {
            if(binding.signupEmailEdittext.length()!=0&&binding.signupUsernameEdittext.length()!=0&&binding.signupPasswordEdittext.length()!=0){
                acceptSignUp(binding.signupEmailEdittext.getText().toString(),binding.signupUsernameEdittext.getText().toString(),binding.signupPasswordEdittext.getText().toString());
            }
        });
        binding.tvLoginToSignUp.setOnClickListener(v ->{
            startActivity(new Intent(this,LoginActivity.class));
        });
    }

    private void acceptSignUp(String email, String username, String pass) {
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    addUserToUser(task.getResult().getUser(),username);
                }
                else{
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // Email đã tồn tại
                        binding.signupEmailEdittext.setError(getString(R.string.email_exist));
                    } else {
                        // Lỗi khác
                        Toast.makeText(RegisterActivity.this, "Registration error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    //thêm người dùng lên firebase
    //upload user on firebase
    private void addUserToUser(FirebaseUser user, String username) {
        Map<String,Object> userDoc=new HashMap<>();

        userDoc.put("username",username);
        userDoc.put("uid",user.getUid());
        userDoc.put("email",user.getEmail());
        userDoc.put("following",0);
        userDoc.put("follower",0);
        userDoc.put("following_array", Arrays.asList(username));
        userDoc.put("follower_array",Arrays.asList());
        //cập nhập tên người dùng đã đăng nhập trước đó
        UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();


        firestore.collection("users").document(username).set(userDoc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.updateProfile(profileChangeRequest)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    auth.getCurrentUser().sendEmailVerification();
                                    startActivity(new Intent(RegisterActivity.this,SignDetailActivity.class));
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void setOnEditActionListener() {
        binding.signupEmailEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_NEXT){
                    binding.signupEmailCheck.setAnimation(R.raw.loading);
                    binding.signupEmailCheck.playAnimation();
                    if(isEmailValid(binding.signupEmailEdittext.getText().toString())){
                        isCheckEmail(binding.signupEmailEdittext.getText().toString(), new OnEmailCheckListener() {
                            @Override
                            public void onSuccess(boolean isRegistered) {
                                if(isRegistered){
                                    binding.signupEmailCheck.setAnimation(R.raw.error);
                                    binding.signupEmailCheck.playAnimation();
                                }
                                else{
                                    binding.signupEmailCheck.setAnimation(R.raw.success_check);
                                    binding.signupEmailCheck.playAnimation();
                                }
                            }
                        });

                    }
                    else{
                        binding.signupEmailCheck.setAnimation(R.raw.error);
                        binding.signupEmailCheck.playAnimation();
                    }
                }
                return false;
            }
        });
        binding.signupUsernameEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyEvent) {
                if (actionId==EditorInfo.IME_ACTION_NEXT){
                    binding.signupUsernameCheck.setAnimation(R.raw.loading);
                    binding.signupUsernameCheck.playAnimation();
                    if(binding.signupUsernameEdittext.getText().length()>0){
                        isCheckName(binding.signupUsernameEdittext.getText().toString(), new OnUserNameCheckListener() {
                            @Override
                            public void onSuccess(boolean isRegistered) {
                                if(isRegistered){
                                    binding.signupUsernameCheck.setAnimation(R.raw.error);
                                    binding.signupUsernameCheck.playAnimation();
                                }
                                else{
                                    binding.signupUsernameCheck.setAnimation(R.raw.success_check);
                                    binding.signupUsernameCheck.playAnimation();
                                }
                            }
                        });
                    }
                }
                return false;
            }
        });

        binding.signupPasswordEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyEvent) {
               if(actionId==EditorInfo.IME_ACTION_DONE){
                   if(binding.signupPasswordEdittext.getText().length()<5){
                       binding.signupPasswordCheck.setAnimation(R.raw.error);
                       binding.signupPasswordCheck.playAnimation();
                   }
                   else{
                       binding.signupPasswordCheck.setAnimation(R.raw.success_check);
                       binding.signupPasswordCheck.playAnimation();
                   }
               }

                return false;
            }
        });
    }
    //Kiểm tra tên user name có tồn tại trên firebase chưa
    //check your name exist on firebase
    private void isCheckName(final String userName,final OnUserNameCheckListener onUserNameCheckListener) {
        if(!userName.contains(" ")){
            firestore.collection("users").document(userName)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                onUserNameCheckListener.onSuccess(task.getResult().exists());
                            }
                        }
                    });
        }else{
            onUserNameCheckListener.onSuccess(true);
        }
    }


    //Kiểm tra tài khoản email có tồn tại  trên firebase chưa
    //check your email exist on firebase
    private void isCheckEmail(final String email, final OnEmailCheckListener onEmailCheckListener) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean check=!task.getResult().getSignInMethods().isEmpty();
                onEmailCheckListener.onSuccess(check);
            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        String tranName="splash_anim";
        ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this,binding.signupLogo,tranName);
        startActivity(intent,activityOptions.toBundle());
    }
}