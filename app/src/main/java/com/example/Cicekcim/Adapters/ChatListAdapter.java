package com.example.Cicekcim.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Cicekcim.Models.ChatList;
import com.example.Cicekcim.R;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListHolder> {

    private ArrayList<ChatList> chatLists;
    private ChatListAdapter.OnNoteListener mOnNoteListener;

    public ChatListAdapter(ArrayList<ChatList> chatLists, ChatListAdapter.OnNoteListener onNoteListener) {
        this.chatLists = chatLists;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.chatlist_item, parent, false);

        return new ChatListAdapter.ChatListHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListHolder chatListHolder, int position) {

        chatListHolder.lastMessage.setText(chatLists.get(position).lastMessage);
        chatListHolder.userName.setText(chatLists.get(position).userName);
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    class ChatListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView userName, lastMessage;
        private ChatListAdapter.OnNoteListener onNoteListener;

        public ChatListHolder(@NonNull View itemView, ChatListAdapter.OnNoteListener onNoteListener) {
            super(itemView);

            userName = itemView.findViewById(R.id.chatListUserName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
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
