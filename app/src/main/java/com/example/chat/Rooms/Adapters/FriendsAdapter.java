package com.example.chat.Rooms.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.CircleTransform;
import com.example.chat.Friends;
import com.example.chat.R;
import com.example.chat.Rooms.OpenChatFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsVH> {

    OpenChatFragment chatFragment;
    private Context context;
    private List<Friends> friendsList;

    public FriendsAdapter(Context context, List<Friends> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
        chatFragment=(OpenChatFragment) context;
    }

    @NonNull
    @Override
    public FriendsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friend_layout, parent, false);
        return new FriendsAdapter.FriendsVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsVH holder, int position) {

        Picasso.get().load(friendsList.get(position).getProfile())
                .transform(new CircleTransform())
                .into(holder.friend_avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.friend_avatar.setImageDrawable(context.getDrawable(R.drawable.avatar));
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        Timber.e(e);
                    }
                });
        holder.friend_name.setText(friendsList.get(position).getName());
        holder.layout.setOnClickListener(v -> {
            chatFragment.openFragment(friendsList.get(position));

        });
    }

    @Override
    public int getItemCount() {
        return (friendsList == null) ? 0 : friendsList.size();
    }

    class FriendsVH extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_img)
        ImageView friend_avatar;
        @BindView(R.id.txt_name)
        TextView friend_name;
        @BindView(R.id.friends_layout)
        View layout;

        FriendsVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
