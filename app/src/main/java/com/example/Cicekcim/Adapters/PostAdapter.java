package com.example.Cicekcim.Adapters;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Cicekcim.Models.Post;
import com.example.Cicekcim.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<Post> postList;
    private OnNoteListener mOnNoteListener;
    private String who;

    public PostAdapter(ArrayList<Post> postList,String who,OnNoteListener onNoteListener) {
        this.postList = postList;
        this.who = who;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.post_item, parent, false);

        return new PostHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder postHolder, int position) {
        String imageurl = postList.get(position).url;
        String imagedesc = postList.get(position).description;
        String useremail = postList.get(position).useremail;
        String postcity = postList.get(position).postcity;
        String postid = postList.get(position).postid;

        postHolder.postDesc.setText(imagedesc);
        postHolder.postCity.setText(postcity);
        Picasso.get().load(imageurl).into(postHolder.imageView);

        postHolder.firebaseFirestore.collection("Kullanicilar").document(useremail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String username = task.getResult().getString("KullaniciAdi");
                postHolder.userName.setText(username);
            }
        });
        if (who.equals("me")){
            postHolder.menuButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.item_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(item -> {

                    Snackbar.make(postHolder.menuButton, "Bu fotoğraf silinsin mi?", Snackbar.LENGTH_LONG)
                            .setAction("Evet", v1 -> {
                                postHolder.firebaseFirestore.collection("Posts").document(postid).delete().addOnSuccessListener(unused -> {
                                    StorageReference storageReference = postHolder.firebaseStorage.getReferenceFromUrl(imageurl);
                                    storageReference.delete().addOnSuccessListener(unused1 -> Toast.makeText(v1.getContext(), "Fotoğraf silindi.", Toast.LENGTH_SHORT).show());
                                });
                            }).show();
                    return false;
                });
            });
        }else {
            postHolder.menuButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView, menuButton;
        private TextView postDesc, postCity, userName;
        private OnNoteListener onNoteListener;
        private FirebaseFirestore firebaseFirestore;
        private FirebaseStorage firebaseStorage;

        public PostHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.postitemimage);
            postCity = itemView.findViewById(R.id.postitemcity);
            postDesc = itemView.findViewById(R.id.postitemdesc);
            userName = itemView.findViewById(R.id.postitemusername);
            menuButton = itemView.findViewById(R.id.menubutton);
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
