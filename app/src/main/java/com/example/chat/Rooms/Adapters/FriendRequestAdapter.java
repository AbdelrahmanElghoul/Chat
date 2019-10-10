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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestVH> {

    private Context context;
    private List<Friends> requestList;

    public FriendRequestAdapter(Context context, List<Friends> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public FriendRequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.request_layout, parent, false);
        return new FriendRequestVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestVH holder, int position) {
        Picasso.get().load(requestList.get(position).getProfile())
                .error(R.drawable.avatar)
                .transform(new CircleTransform())
                .into(holder.friend_avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        Timber.e(e);
                    }
                });

        holder.friend_name.setText(requestList.get(position).getName());

        holder.accept_request.setOnClickListener(v -> {
            Request(requestList.get(position).getKey(), FirebaseAuth.getInstance().getUid(), context.getString(R.string.friend));
            Request(FirebaseAuth.getInstance().getUid(), requestList.get(position).getKey(), context.getString(R.string.friend));
        });

        holder.cancel_request.setOnClickListener(v -> {
            Request(requestList.get(position).getKey(), FirebaseAuth.getInstance().getUid(), context.getString(R.string.removed_Friend));
            Request(FirebaseAuth.getInstance().getUid(), requestList.get(position).getKey(), context.getString(R.string.removed_Friend));
        });
    }

    private void Request(String Id1, String Id2, String state) {
        DatabaseReference userRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(context.getString(R.string.User_KEY))
                .child(Id1)
                .child(context.getString(R.string.Friends_KEY))
                .child(Id2);
        HashMap data = new HashMap();
        data.put(context.getString(R.string.friendState), state);
        userRef.updateChildren(data).addOnFailureListener(e ->
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());

    }

    @Override
    public int getItemCount() {
        return (requestList == null) ? 0 : requestList.size();
    }

    class FriendRequestVH extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_img)
        ImageView friend_avatar;
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
