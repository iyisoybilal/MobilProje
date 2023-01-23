package com.example.Cicekcim.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Cicekcim.Models.Message;
import com.example.Cicekcim.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    private final ArrayList<Message> messageList;
    private String meEmail;


    public MessageAdapter(ArrayList<Message> messageList, String meEmail) {
        this.messageList = messageList;
        this.meEmail = meEmail;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.message_item_right, parent, false);
            return new MessageAdapter.MessageHolder(view);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.message_item_left, parent, false);
            return new MessageAdapter.MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageHolder messageHolder, int position) {
        messageHolder.showMessage.setText(messageList.get(position).message);

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder {

        public TextView showMessage;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).sender.equals(meEmail)) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }
}
