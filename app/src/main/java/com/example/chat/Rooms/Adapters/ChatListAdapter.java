package com.example.chat.Rooms.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.Rooms.ChatFragment;
import com.example.chat.Rooms.ChatListData;
import com.example.chat.Rooms.OpenChatFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListVH> {

    private Context context;
    private List<ChatListData> friendsList;
    OpenChatFragment chatFragment;

    public ChatListAdapter(Context context, List<ChatListData> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
        chatFragment=(OpenChatFragment) context;
    }

    @NonNull
    @Override
    public ChatListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.chat_list_layout,parent,false);;
        return new ChatListVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListVH holder, int position) {
        Picasso.get()
                .load(friendsList.get(position).getProfile())
                .into(holder.imgAvatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        Timber.tag("ChatList").d("avatar uploaded");
                    }

                    @Override
                    public void onError(Exception e) {
                            holder.imgAvatar.setImageResource(R.drawable.avatar);
                            Timber.tag("ChatList").e(e);
                    }
                });
        Timber.tag("img").d(friendsList.get(position).getMessage());

        holder.txtName.setText(friendsList.get(position).getName());
        holder.txtMessage.setText(friendsList.get(position).getMessage());
        if(friendsList.get(position).isNew_message()){
            holder.txtMessage.setTypeface(null, Typeface.BOLD);
            holder.txtName.setTypeface(null, Typeface.BOLD);

        }
        holder.layout.setOnClickListener(v-> chatFragment.openFragment(friendsList.get(position),new ChatFragment()));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class ChatListVH extends RecyclerView.ViewHolder{
        @BindView(R.id.chat_list_avatar)
        ImageView imgAvatar;
        @BindView(R.id.chat_list_layout)
        View layout;
        @BindView(R.id.chat_list_name)
        TextView txtName;
        @BindView(R.id.chat_list_message)
        TextView txtMessage;
        ChatListVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
