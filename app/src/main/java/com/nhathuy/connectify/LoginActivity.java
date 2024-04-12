package com.nhathuy.connectify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji2.bundled.BundledEmojiCompatConfig;
import androidx.emoji2.text.EmojiCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nhathuy.connectify.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        Window w=getWindow();
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);



        mAuth=FirebaseAuth.getInstance();


        binding.btnSignup.setOnClickListener(v->{
            if(isEmailCheck(binding.loginEmailEdittext.getText().toString())){
                if(binding.loginPasswordEdittext.getText().length()>5){
                    acceptLogin(binding.loginEmailEdittext.getText().toString(),binding.loginPasswordEdittext.getText().toString());
                }
                else{
                    binding.loginPasswordEdittext.setError("Enter valid password");
                }

            }
            else{
                binding.loginEmailEdittext.setError("Enter valid email");
            }

        });

        binding.tvLoginToSignUp.setOnClickListener(v->{
            Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
            String transName="splash_anim";
            ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,binding.tvLogo,transName);
            startActivity(intent,activityOptions.toBundle());
        });

        binding.loginCheckbox.setOnClickListener(v->{
            binding.loginCheckbox.playAnimation();
            binding.loginCheckbox.setRepeatMode(LottieDrawable.REVERSE);
        });

    }

    private void acceptLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    binding.loginEmailEdittext.setError("Login Error");
                }
            }
        });
    }


    private boolean isEmailCheck(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}