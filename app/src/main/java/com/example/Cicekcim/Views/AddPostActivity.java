package com.example.Cicekcim.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.Cicekcim.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity {
    private Spinner spinner;
    private TextInputEditText imageDesc;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Uri imageData;
    private ImageView photoGallery;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissonLauncher;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        spinner = (Spinner) findViewById(R.id.addPostSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Cities, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        registerLauncher();

        photoGallery = findViewById(R.id.photoGallery);
        CardView addPost = findViewById(R.id.addPost);
        imageDesc = findViewById(R.id.imageDesc);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userEmail = firebaseAuth.getCurrentUser().getEmail();

        photoGallery.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //Eğer izin yoksa izin isteme
                if (ActivityCompat.shouldShowRequestPermissionRationale(AddPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    permissonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            } else {
                //İzin varsa galeriye gitme
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        });

        addPost.setOnClickListener(view -> {
            if (imageData != null && !spinner.getSelectedItem().toString().equals("Bir Şehir Seçiniz")) {

                UUID uuid = UUID.randomUUID();
                String imageDataName = userEmail + "/" + uuid + ".jpg";

                storageReference.child(imageDataName).putFile(imageData).addOnSuccessListener(taskSnapshot -> {

                    StorageReference newReferance = FirebaseStorage.getInstance().getReference(imageDataName);
                    newReferance.getDownloadUrl().addOnSuccessListener(uri -> {

                        String imageurl = uri.toString();
                        String imagedesc = imageDesc.getText().toString();
                        String postcity = spinner.getSelectedItem().toString();

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("useremail",userEmail);
                        data.put("imageurl", imageurl);
                        data.put("imagedesc", imagedesc);
                        data.put("postcity",postcity);
                        data.put("date", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Posts").add(data).addOnSuccessListener(documentReference -> {

                            Toast.makeText(getApplicationContext(), "Post eklendi.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPostActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }).addOnFailureListener(e -> Toast.makeText(AddPostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

                    });
                }).addOnFailureListener(e -> Toast.makeText(AddPostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
            } else {
                Toast.makeText(AddPostActivity.this, "Görsel - Şehir seçiniz.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent intentFromResult = result.getData();
                if (intentFromResult != null) {
                    imageData = intentFromResult.getData();
                    photoGallery.setImageURI(imageData);
                }
            }
        });
        permissonLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            } else {
                Toast.makeText(AddPostActivity.this, "İzin vermeden post ekleyemezsiniz.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}