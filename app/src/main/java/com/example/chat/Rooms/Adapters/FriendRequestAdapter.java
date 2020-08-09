package com.example.chat.Rooms.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chat.Friends;
import com.example.chat.R;
import com.example.chat.mFirebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestVH> {

    private Context context;
    private List<Friends> requestList;
    mFirebase fb;
    public FriendRequestAdapter(Context context, List<Friends> requestList) {
        this.context = context;
        this.requestList = requestList;
        this.fb=new mFirebase(context);
    }

    @NonNull
    @Override
    public FriendRequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.request_layout, parent, false);
        return new FriendRequestVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestVH holder, int position) {
        Glide.with(context)
                .load(requestList.get(position).getProfile())
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.avatar)
                .into(holder.imgFriendProfile);

        holder.friend_name.setText(requestList.get(position).getName());

        holder.accept_request.setOnClickListener(v -> {

            fb.Request(requestList.get(position).getKey(), FirebaseAuth.getInstance().getUid(), context.getString(R.string.friend));
            fb.Request(FirebaseAuth.getInstance().getUid(), requestList.get(position).getKey(), context.getString(R.string.friend));
        });

        holder.cancel_request.setOnClickListener(v -> {
            fb.Request(requestList.get(position).getKey(), FirebaseAuth.getInstance().getUid(), context.getString(R.string.removed_Friend));
            fb.Request(FirebaseAuth.getInstance().getUid(), requestList.get(position).getKey(), context.getString(R.string.removed_Friend));
        });
    }

    @Override
    public int getItemCount() {
        return (requestList == null) ? 0 : requestList.size();
    }

    class FriendRequestVH extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_img)
        ImageView imgFriendProfile;
        @BindView(R.id.txt_name)
        TextView friend_name;
        @BindView(R.id.accept_request)
        ImageButton accept_request;
        @BindView(R.id.cancel_request)
        ImageButton cancel_request;

        FriendRequestVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
