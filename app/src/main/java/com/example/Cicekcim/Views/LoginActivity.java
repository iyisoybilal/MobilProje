package com.example.Cicekcim.Views;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.Cicekcim.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText emailText,passText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText=findViewById(R.id.email);
        passText= findViewById(R.id.password);
        CardView loginBtn = findViewById(R.id.login);
        TextView signUpButton = findViewById(R.id.loginSignup);

        firebaseAuth=FirebaseAuth.getInstance();

        openHomePage();

        loginBtn.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

    }

    public void openHomePage() {// kullanıcı oturumu kapatmadıysa uygulama home ekranından başlar.
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login:
                String email = emailText.getText().toString();
                String pass = passText.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                    firebaseAuth.signInWithEmailAndPassword(email, pass) // mauth üzerinden
                            // email ve şifre kontrollü kullanici girişi
                            .addOnSuccessListener(this, authResult -> {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }).addOnFailureListener(this, e -> Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show());

                }else{

                    Toast.makeText(LoginActivity.this,"Boş alan bırakmayınız.",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.loginSignup:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
    }
}