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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Friends;
import com.example.chat.Notification.PushNotification;
import com.example.chat.R;
import com.example.chat.Rooms.Adapters.FriendRequestAdapter;
import com.example.chat.Rooms.Adapters.FriendsAdapter;
import com.example.chat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class FriendsFragment extends Fragment {

    private DatabaseReference userRef;

    @BindView(R.id.btn_add_friend)
    ImageButton btn_add;
    @BindView(R.id.txt_search)
    EditText txt_Search;
    @BindView(R.id.friends_list)
    RecyclerView friendsRecyclerView;
    @BindView(R.id.friends_request)
    RecyclerView requestsRecyclerView;

    private User user;
    private List<Friends> requestList;
    private List<Friends> friendsList;
    private FriendRequestAdapter requestAdapter;
    private FriendsAdapter friendsAdapter;
    private DatabaseReference friendsRef;
    private ChildEventListener friendsChildListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, v);
        assert getArguments() != null;
        user=getArguments().getParcelable(getString(R.string.User_KEY));
        friendsList = new ArrayList<>();
        requestList = new ArrayList<>();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_add.setOnClickListener(v -> AddFriendBtn(txt_Search.getText().toString()));

        requestAdapter = new FriendRequestAdapter(getContext(), requestList);
        friendsAdapter = new FriendsAdapter(getContext(), friendsList);

        requestsRecyclerView.setHasFixedSize(true);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        requestsRecyclerView.setAdapter(requestAdapter);

        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerView.setAdapter(friendsAdapter);

        friendsChildListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Timber.tag("AddedData").d(String.valueOf(dataSnapshot));
                Timber.tag("AddedS").d(dataSnapshot.getKey());

                Friends tmp=dataSnapshot.getValue(Friends.class);
                tmp.setKey(dataSnapshot.getKey());

                if(tmp.getFriendState().equals(getString(R.string.friend))) {
                    friendsList.add(tmp);
                    friendsAdapter.notifyDataSetChanged();
                }
                else if(tmp.getFriendState().equals(getString(R.string.pendingRequest))) {
                    requestList.add(tmp);
                    requestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Timber.d(String.valueOf(dataSnapshot));
                Timber.d(s);
                Friends tmp=dataSnapshot.getValue(Friends.class);
                tmp.setKey(dataSnapshot.getKey());
                if(tmp.getFriendState().equals(getString(R.string.pendingRequest))){
                    requestList.add(tmp);
                    requestAdapter.notifyDataSetChanged();
                }
                else if(tmp.getFriendState().equals(getString(R.string.friend))){
                    for(int i=0;i<requestList.size();i++){
                        if(requestList.get(i).getKey().equals(tmp.getKey())){
                            requestList.remove(i);
                        break;
                        }
                    }
                    friendsList.add(tmp);
                    requestAdapter.notifyDataSetChanged();
                    friendsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        friendsRef=FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .child(FirebaseAuth.getInstance().getUid())
                .child(getString(R.string.Friends_KEY));

        friendsRef.addChildEventListener(friendsChildListener);

    }

    private void AddFriendBtn(String Email) {
        Timber.d("Add btn");

        new PushNotification().Notify(getContext(),"Title","Test Message",null,null);

        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> Timber.d(instanceIdResult.getToken()));

        if (Email.isEmpty() || Email.equals(user.getEmail()))
            return;

        if (friendsList == null || friendsList.size() == 0)
            GetUserID(Email);
        else {
            boolean found=false;
            for (Friends friends : friendsList) {
                if (friends.getEmail().equals(Email)) {
                    if (getString(R.string.friend).equals(friends.getFriendState())) {
                        Toast.makeText(getContext(), getString(R.string.MSG_isFriend), Toast.LENGTH_SHORT).show();
                    } else if (getString(R.string.pendingRequest).equals(friends.getFriendState())) {
                        Toast.makeText(getContext(), getString(R.string.MSG_pendingRequest), Toast.LENGTH_SHORT).show();

                    }else if(getString(R.string.RequestSent).equals(friends.getFriendState())) {
                        Toast.makeText(getContext(), getString(R.string.MSG_Request_Sent), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        AddFriendReceiver(user, friends);
                        AddFriendSender(friends);
                    }
                    found=true;
                    break;
                }

            }
            if(!found)
              GetUserID(Email);
        }
    }

    private void GetUserID(String Email) {

        Query ReceiverQuery = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .orderByChild(getString(R.string.email))
                .equalTo(Email);

        Timber.d("Searching");
        ReceiverQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Timber.d(String.valueOf(dataSnapshot));
                Friends Receive = null;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Receive = d.getValue(Friends.class);
                    Receive.setKey(d.getKey());
                }
                if (Receive == null) {
                    Toast.makeText(getContext(), getString(R.string.MSG_NoUseR), Toast.LENGTH_SHORT).show();
                } else {
                    AddFriendSender(Receive);
                    AddFriendReceiver(user, Receive);
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
                .child(FirebaseAuth.getInstance().getUid())
                .child(getString(R.string.Friends_KEY))
                .child(friend.getKey())
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
                .child(friend.getKey())//User ID
                .child(getString(R.string.Friends_KEY))//Friends
                .child(FirebaseAuth.getInstance().getUid())//friends ID
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


    @Override
    public void onStop() {
        super.onStop();
        friendsRef.removeEventListener(friendsChildListener);
        Timber.d("Listener removed");
    }
}