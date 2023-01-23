package com.example.Cicekcim.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Cicekcim.Adapters.MessageAdapter;
import com.example.Cicekcim.Models.Message;
import com.example.Cicekcim.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String userEmail, userName, meEmail, meUserName;
    private ArrayList<Message> messageList;
    private ImageButton sendButton;
    private EditText sendText;
    private TextView userNameText;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("useremail");
        userName = intent.getStringExtra("username");

        messageList = new ArrayList<>();

        sendButton = findViewById(R.id.sendMessageButton);
        sendText = findViewById(R.id.sendMessageText);
        recyclerView = findViewById(R.id.chatActicityRecyclerView);
        userNameText = findViewById(R.id.chatActicityUsername);
        recyclerView.setHasFixedSize(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        meEmail = firebaseAuth.getCurrentUser().getEmail();

        getMeUsername();
        readMessage(meEmail, userEmail);

        userNameText.setText(userName);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(messageList,meEmail);
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> {
            String message = sendText.getText().toString();

            if (!message.equals("")) {
                sendMessage(userEmail, meEmail, message, meUserName, userName);
                sendText.setText("");
            }
        });
    }

    public void sendMessage(String useremail, String meemail, String message, String meusername, String username) {

        HashMap<String, Object> messageData = new HashMap<>();
        messageData.put("gönderen", meemail);
        messageData.put("alan", useremail);
        messageData.put("mesaj", message);
        messageData.put("date", FieldValue.serverTimestamp());

        HashMap<String, Object> dataMe = new HashMap<>();
        dataMe.put("username", username);
        dataMe.put("lastmessage", "Ben: " + message);
        dataMe.put("date", FieldValue.serverTimestamp());

        HashMap<String, Object> dataOther = new HashMap<>();
        dataOther.put("username", meusername);
        dataOther.put("lastmessage", message);
        dataOther.put("date", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Messages").add(messageData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseFirestore.collection("ChatList" + meemail).document(useremail).set(dataMe);
                firebaseFirestore.collection("ChatList" + useremail).document(meemail).set(dataOther);
            }
        });
    }

    private void readMessage(String meemail, String useremail) {
        firebaseFirestore.collection("Messages").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener((value, error) -> {
            if (value != null) {
                messageList.clear();
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Map<String, Object> data = documentSnapshot.getData();

                    String sender = (String) data.get("gönderen");
                    String receiver = (String) data.get("alan");

                    if (receiver.equals(meemail) && sender.equals(useremail)) {
                        String message = (String) data.get("mesaj");

                        Message messageData = new Message(receiver,sender,message);
                        messageList.add(messageData);

                    } else if (receiver.equals(useremail) && sender.equals(meemail)) {
                        String message = (String) data.get("mesaj");

                        Message messageData = new Message(receiver,sender,message);
                        messageList.add(messageData);
                    }
                    messageAdapter.notifyDataSetChanged();
                }
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    public void getMeUsername() {
        firebaseFirestore.collection("Kullanicilar").document(meEmail).addSnapshotListener((value, error) -> {
            if (value != null) {
                meUserName = value.getString("KullaniciAdi");
            }
        });
    }
}