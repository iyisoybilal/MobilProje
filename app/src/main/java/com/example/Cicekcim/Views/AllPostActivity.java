package com.example.Cicekcim.Views;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Cicekcim.Adapters.PostAdapter;
import com.example.Cicekcim.Models.Post;
import com.example.Cicekcim.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class AllPostActivity extends AppCompatActivity implements PostAdapter.OnNoteListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String email;
    private ArrayList<Post> postList;
    private PostAdapter postAdapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_post);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        email = firebaseAuth.getCurrentUser().getEmail();

        postList = new ArrayList<>();

        getPostData();

        RecyclerView recyclerView = findViewById(R.id.allPostRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        postAdapter = new PostAdapter(postList, "allPost", this);
        recyclerView.setAdapter(postAdapter);
    }

    public void getPostData() {

        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener((value, error1) -> {
            if (value != null) {
                postList.clear();
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    String postcity = documentSnapshot.getString("postcity");
                    String imagedesc = documentSnapshot.getString("imagedesc");
                    String imageurl = documentSnapshot.getString("imageurl");
                    String useremail = documentSnapshot.getString("useremail");

                    String postid = documentSnapshot.getId();

                    Post post = new Post(imagedesc, imageurl, useremail, postid, postcity);
                    postList.add(post);

                    postAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public void onNoteClick(int position) {
        intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("email",postList.get(position).useremail);
        if (email.equals(postList.get(position).useremail)){
            intent.putExtra("who","me");
        }else {
            intent.putExtra("who","other");
        }
        startActivity(intent);
    }
}