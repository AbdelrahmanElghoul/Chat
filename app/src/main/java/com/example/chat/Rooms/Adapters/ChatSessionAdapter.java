package com.example.chat.Rooms.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Messages;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatSessionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Messages> messagesList;
    private static final int User_TYPE=234;
    private static final int Friend_TYPE=456;

    public ChatSessionAdapter(Context context, List<Messages> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(viewType==User_TYPE) {
            View v=layoutInflater.inflate(R.layout.chat_sender_layout,parent,false);
            return new userViewHolder(v);
        }else{
            View v=layoutInflater.inflate(R.layout.chat_receiver_layout,parent,false);
            return new FriendViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof userViewHolder) {
            ((userViewHolder) holder).txtChat.setText(messagesList.get(position).getMessage());
            ((userViewHolder) holder).txtDate.setText(messagesList.get(position).getTimeStamp());
            ((userViewHolder) holder).layout.setOnClickListener(v -> {
                ((userViewHolder) holder).txtDate.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ((userViewHolder) holder).txtDate.setVisibility(View.VISIBLE);
                            }
                        });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((userViewHolder) holder).txtDate.animate()
//                                .translationY(((userViewHolder) holder).txtDate.getHeight())
                                .alpha(0.0f)
                                .setDuration(200)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        ((userViewHolder) holder).txtDate.setVisibility(View.GONE);
                                    }
                                });
                    }
                }, 1500);
            });
        } else {
            ((FriendViewHolder) holder).txtChat.setText(messagesList.get(position).getMessage());
            ((FriendViewHolder) holder).txtDate.setText(messagesList.get(position).getTimeStamp());
            ((FriendViewHolder) holder).layout.setOnClickListener(v -> {
                ((FriendViewHolder) holder).txtDate.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ((FriendViewHolder) holder).txtDate.setVisibility(View.VISIBLE);
                            }
                        });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((FriendViewHolder) holder).txtDate.animate()
//                                .translationY(((FriendViewHolder) holder).txtDate.getHeight())
                                .alpha(0.0f)
                                .setDuration(200)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        ((FriendViewHolder) holder).txtDate.setVisibility(View.GONE);
                                    }
                                });
                    }
                }, 1500);
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getSenderKey().equals(FirebaseAuth.getInstance().getUid())) {
            return User_TYPE;
        } else {
            return Friend_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    class userViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.txt_chat_sender)
        TextView txtChat;
        @BindView(R.id.txt_date_sender)
        TextView txtDate;
        @BindView(R.id.layout_sender)
        ConstraintLayout layout;
        userViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class FriendViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.txt_chat_receiver)
        TextView txtChat;
        @BindView(R.id.txt_date_receiver)
        TextView txtDate;
        @BindView(R.id.layout_receiver)
        ConstraintLayout layout;
        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
