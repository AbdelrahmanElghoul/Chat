package com.example.chat.Rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Friends;
import com.example.chat.R;
import com.example.chat.Register.MainActivity;
import com.example.chat.Rooms.Adapters.FriendRequestAdapter;
import com.example.chat.Rooms.Adapters.FriendsAdapter;
import com.example.chat.User;
import com.example.chat.ViewModel.FriendsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class FriendsFragment extends Fragment {

    private DatabaseReference userRef;
    private FriendsViewModel viewModel;
    @BindView(R.id.btn_add_friend)
    ImageButton btn_add;
    @BindView(R.id.txt_search)
    EditText txt_Search;
    @BindView(R.id.friends_list)
    RecyclerView friendsRecyclerView;
    @BindView(R.id.friends_request)
    RecyclerView requestsRecyclerView;

    private List<Friends> requestList = new ArrayList<>();
    private List<Friends> friendsList = new ArrayList<>();
    private FriendRequestAdapter requestAdapter;
    private FriendsAdapter friendsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);
        btn_add.setOnClickListener(v -> AddFriendBtn(txt_Search.getText().toString()));

        requestAdapter = new FriendRequestAdapter(getContext(), requestList);
        friendsAdapter = new FriendsAdapter(getContext(), friendsList);

        requestsRecyclerView.setHasFixedSize(true);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        requestsRecyclerView.setAdapter(requestAdapter);

        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerView.setAdapter(friendsAdapter);

        viewModel.getFriendsListener().observe(this, dataSnapshot -> {
            boolean found = false;
            String Key = dataSnapshot.getKey();
            Timber.d(Key);

                for (int i = 0; i < viewModel.getUser().getFriends().size(); i++) {
                    Timber.e(String.valueOf(dataSnapshot));
                    if (viewModel.getUser().getFriends().get(i).getID().equals(Key)) {
                        Friends f=dataSnapshot.getValue(Friends.class);
                        f.setID(Key);
                        requestList.remove(viewModel.getUser().getFriends().get(i));
                        friendsList.remove(viewModel.getUser().getFriends().get(i));
                        if(getString(R.string.pendingRequest)
                                .equals(dataSnapshot.child(getString(R.string.friendState)).getValue(String.class)))
                        {
                            requestList.add(f);
                        }
                        viewModel.getUser().getFriends().get(i).UpdateFriend(f);
                        if(f.getFriendState().equals(getString(R.string.friend)))
                            friendsList.add(f);
                        found = true;
                        break;
                    }
                }

            if (!found) {
                Friends f = dataSnapshot.getValue(Friends.class);
                f.setID(Key);
                viewModel.getUser().AddFriend(f);
                if(f.getFriendState().equals(getString(R.string.pendingRequest)))
                    requestList.add(f);
                else if(f.getFriendState().equals(getString(R.string.friend)))
                    friendsList.add(f);
            }
            requestAdapter.notifyDataSetChanged();
            friendsAdapter.notifyDataSetChanged();
        });
    }

    private void AddFriendBtn(String Email) {
        if (Email.isEmpty() || Email.equals(viewModel.getUser().getEmail()))
            return;
//        Timber.d(Email);

        if (viewModel.getUser().getFriends() == null || viewModel.getUser().getFriends().size() == 0)
            GetUserID(Email);
        else {
            for (Friends friends : viewModel.getUser().getFriends()) {
                if (friends.getEmail().equals(Email)) {
                    if (getString(R.string.friend).equals(friends.getFriendState())) {
                        Toast.makeText(getContext(), getString(R.string.MSG_isFriend), Toast.LENGTH_SHORT).show();
                    } else if (getString(R.string.pendingRequest).equals(friends.getFriendState())) {
                        Toast.makeText(getContext(), getString(R.string.MSG_pendingRequest), Toast.LENGTH_SHORT).show();
                    }else if(getString(R.string.RequestSent).equals(friends.getFriendState())) {
                        Toast.makeText(getContext(), getString(R.string.MSG_Request_Sent), Toast.LENGTH_SHORT).show();
                    }
                    else if (getString(R.string.removed_Friend).equals(friends.getFriendState())) {
                        AddFriendReceiver(viewModel.getUser(), friends);
                        AddFriendSender(friends);
                    } else {
                        GetUserID(Email);
                    }
                    break;
                }
            }
        }
    }

    private void GetUserID(String Email) {
        Query ReceiverQuery = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .orderByChild(getString(R.string.email))
                .equalTo(Email);

        ReceiverQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Timber.d(String.valueOf(dataSnapshot));
                Friends Receive = null;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Receive = d.getValue(Friends.class);
                    Receive.setID(d.getKey());
                }
                if (Receive == null) {
                    Toast.makeText(getContext(), getString(R.string.MSG_NoUseR), Toast.LENGTH_SHORT).show();
                } else {
                    AddFriendSender(Receive);
                    AddFriendReceiver(viewModel.getUser(), Receive);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AddFriendSender(Friends friend) {

        //setting Request to sender
        userRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .child(MainActivity.currentUserID)
                .child(getString(R.string.Friends_KEY))
                .child(friend.getID())
        ;
        HashMap data = new HashMap();
        data.put(getString(R.string.name), friend.getName());
        data.put(getString(R.string.email), friend.getEmail());
        data.put(getString(R.string.profile_IMG), friend.getProfile());
        data.put(getString(R.string.friendState), getString(R.string.RequestSent));
        userRef.updateChildren(data).addOnFailureListener(e ->
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());

    }

    private void AddFriendReceiver(User user, Friends friend) {

        //setting Request to Receiver
        userRef = FirebaseDatabase.getInstance()
                .getReference()//db
                .child(getString(R.string.User_KEY))//Users
                .child(friend.getID())//User ID
                .child(getString(R.string.Friends_KEY))//Friends
                .child(MainActivity.currentUserID)//friends ID
        ;
        HashMap data = new HashMap();
        Timber.d(String.valueOf(user.getProfilePic()));
        data.put(getString(R.string.name), user.getName());
        data.put(getString(R.string.email), user.getEmail());
        data.put(getString(R.string.profile_IMG), user.getProfilePic());
        data.put(getString(R.string.friendState), getString(R.string.pendingRequest));
        userRef.updateChildren(data).addOnFailureListener(e ->
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());

    }
}
