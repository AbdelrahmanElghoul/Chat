package com.example.chat.Rooms.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.CircleTransform;
import com.example.chat.Friends;
import com.example.chat.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsVH> {

    private Context context;
    private List<Friends> friendsList;

    public FriendsAdapter(Context context, List<Friends> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
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
                .error(R.drawable.avatar)
                .transform(new CircleTransform())
                .into(holder.friend_avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        Timber.d(e);
                    }
                });

        holder.friend_name.setText(friendsList.get(position).getName());

        holder.accept_request.setVisibility(View.GONE);

        holder.cancel_request.setVisibility(View.GONE);
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
        @BindView(R.id.accept_request)
        ImageButton accept_request;
        @BindView(R.id.cancel_request)
        ImageButton cancel_request;

        FriendsVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
