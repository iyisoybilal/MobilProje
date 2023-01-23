package com.example.Cicekcim.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.Cicekcim.Adapters.PostAdapter;
import com.example.Cicekcim.Models.Post;
import com.example.Cicekcim.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements PostAdapter.OnNoteListener {
    private TextView userNameText, cityText, followText, numberOfPostText;
    private String email, who;
    private FirebaseFirestore firebaseFirestore;
    private PostAdapter postAdapter;
    private ArrayList<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userNameText = findViewById(R.id.profileUserNameText);
        cityText = findViewById(R.id.profileCityText);
        followText = findViewById(R.id.followText);
        numberOfPostText = findViewById(R.id.profileNumberOfPostText);
        firebaseFirestore = FirebaseFirestore.getInstance();
        CardView followBtn = findViewById(R.id.follow);
        RecyclerView recyclerView = findViewById(R.id.profilRecyclerView);

        postList = new ArrayList<>();

        Intent intent = getIntent();

        email = intent.getStringExtra("email");
        who = intent.getStringExtra("who");

        setUserInfo();
        getPostData();
        setFollowButtonText();

        followBtn.setOnClickListener(view -> {
            if (who.equals("me")){
                changeCity();
            }else {
                Intent intent1 = new Intent(ProfileActivity.this,ChatActivity.class);
                intent1.putExtra("useremail",email);
                intent1.putExtra("username",userNameText.getText().toString());
                startActivity(intent1);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        postAdapter = new PostAdapter(postList, who, this);
        recyclerView.setAdapter(postAdapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void getPostData() {

        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener((value, error1) -> {
            if (value != null) {
                postList.clear();
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    String useremail = documentSnapshot.getString("useremail");
                    assert useremail != null;
                    if (useremail.equals(email)){
                        String postcity = documentSnapshot.getString("postcity");
                        String imagedesc = documentSnapshot.getString("imagedesc");
                        String imageurl = documentSnapshot.getString("imageurl");
                        String postid = documentSnapshot.getId();

                        Post post = new Post(imagedesc, imageurl, useremail, postid, postcity);
                        postList.add(post);
                    }
                    postAdapter.notifyDataSetChanged();
                }
                numberOfPostText.setText(String.valueOf(postList.size()));
            }
        });
    }

    public void setUserInfo() {
        firebaseFirestore.collection("Kullanicilar").document(email).addSnapshotListener((value, error) -> {
            if (value != null){
                String username = value.getString("KullaniciAdi");
                String city = value.getString("Sehir");
                cityText.setText(city);
                userNameText.setText(username);
            }
        });
    }

    public void setFollowButtonText(){
        if (who.equals("me")){
            followText.setText("Şehir Değiştir");
        }else {
            followText.setText("Mesaj Gönder");
        }
    }

    public void changeCity(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        Spinner spinner = new Spinner(ProfileActivity.this);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Cities, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        builder.setView(spinner);

        builder.setPositiveButton("Evet", (dialogInterface, i) -> {
            if (!spinner.getSelectedItem().toString().equals("Bir Şehir Seçiniz")){
                firebaseFirestore.collection("Kullanicilar").document(email).update("Sehir",spinner.getSelectedItem().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Şehir değiştirildi.",Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(getApplicationContext(),"Şehir seçiniz.",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hayır",((dialogInterface, i) -> {}));
        builder.create().show();
    }

    @Override
    public void onNoteClick(int position) {

    }
}