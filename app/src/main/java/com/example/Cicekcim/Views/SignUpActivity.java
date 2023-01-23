package com.example.Cicekcim.Views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.Cicekcim.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private Spinner spinner;
    private TextInputEditText emailText, passText, usernameText;
    private String email, pass, username, usercity;
    private FirebaseAuth firebaseAuth;
    private HashMap<String, Object> mData;
    private FirebaseFirestore firebaseFirestore; // database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Cities, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        emailText = findViewById(R.id.signupemail);
        passText = findViewById(R.id.signuppassword);
        usernameText = findViewById(R.id.signupusername);
        CardView singupBtn = findViewById(R.id.singup);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        singupBtn.setOnClickListener(view -> {
            email = emailText.getText().toString();
            pass = passText.getText().toString();
            username = usernameText.getText().toString();
            usercity = spinner.getSelectedItem().toString();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(username)) {
                if (!usercity.equals("Bir Şehir Seçiniz")) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            // mauth üzerinden email ve şifre kontrollü kullanıcı oluşturma
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    mData = new HashMap<>();
                                    mData.put("KullaniciAdi", username);
                                    mData.put("Sehir", usercity);

                                    firebaseFirestore.collection("Kullanicilar").document(email)
                                            .set(mData)
                                            .addOnCompleteListener(SignUpActivity.this, task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "Kayıt işlemi başarılı.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                } else {//Kullanıcı başarılı bir şekilde oluşturulamazsa
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Yaşadığınız şehri seçiniz.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Boş alan bırakmayınız.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}