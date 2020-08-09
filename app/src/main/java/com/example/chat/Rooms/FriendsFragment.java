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
import com.example.chat.mFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private List<Friends> pendingRequest;
    private List<Friends> friendsList;

    private FriendRequestAdapter requestAdapter;
    private FriendsAdapter friendsAdapter;
    private DatabaseReference friendsRef;
    private ChildEventListener friendsChildListener;
    private mFirebase fb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, v);
        assert getArguments() != null;
        user = getArguments().getParcelable(getString(R.string.User_KEY));
        friendsList = new ArrayList<>();
        requestList = new ArrayList<>();
        pendingRequest=new ArrayList<>();
        fb=new mFirebase(getContext());
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_add.setOnClickListener(v -> AddFriendBtn(txt_Search.getText().toString()));


//        for(Friends f:requestList){
//            if(f.getFriendState().equals(getContext().getString(R.string.pendingRequest))){
//                pendingRequest.add(f);
//            }
//        }

        requestAdapter = new FriendRequestAdapter(getContext(), requestList);
        friendsAdapter = new FriendsAdapter(getContext(), friendsList);

        requestsRecyclerView.setHasFixedSize(true);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        requestsRecyclerView.setAdapter(requestAdapter);

        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerView.setAdapter(friendsAdapter);

        friendsChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Timber.tag("AddedData").d(String.valueOf(dataSnapshot));
                Timber.tag("AddedS").d(dataSnapshot.getKey());

                Friends tmp = dataSnapshot.getValue(Friends.class);
                tmp.setKey(dataSnapshot.getKey());

                if (tmp.getFriendState().equals(getString(R.string.friend))) {
                    friendsList.add(tmp);
                    friendsAdapter.notifyDataSetChanged();
                    Timber.d("added friend");
                } else if (tmp.getFriendState().equals(getString(R.string.pendingRequest))) {
                    pendingRequest.add(tmp);
                    requestAdapter.notifyDataSetChanged();
                    Timber.d("added pending");
                }else if (tmp.getFriendState().equals(getString(R.string.RequestSent))) {
                    requestList.add(tmp);
                    //requestAdapter.notifyDataSetChanged();
                    Timber.d("added sent");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Timber.d(String.valueOf(dataSnapshot));
                Timber.d(s);
                Timber.d("child changed");

                Friends tmp = dataSnapshot.getValue(Friends.class);
                tmp.setKey(dataSnapshot.getKey());

                if (tmp.getFriendState().equals(getString(R.string.pendingRequest))) {
                    pendingRequest.add(tmp);
                    requestAdapter.notifyDataSetChanged();
                }
                else if (tmp.getFriendState().equals(getString(R.string.RequestSent))) {
                    requestList.add(tmp);
                    //requestAdapter.notifyDataSetChanged();
                }
                else if (tmp.getFriendState().equals(getString(R.string.friend))) {
                    boolean isRequest=false;

                    for (int i = 0; i < requestList.size(); i++) {
                        if (requestList.get(i).getKey().equals(tmp.getKey())) {
                            requestList.remove(i);
                            Timber.d("request removed");
                            isRequest=true;
                            break;
                        }
                    }

                    for (int i = 0; i < pendingRequest.size(); i++) {
                        if (pendingRequest.get(i).getKey().equals(tmp.getKey())) {
                            pendingRequest.remove(i);
                            Timber.d("pending request removed");
                            isRequest=true;
                            break;
                        }
                    }

                    if(isRequest)
                         friendsList.add(tmp);
                    requestAdapter.notifyDataSetChanged();
                    friendsAdapter.notifyDataSetChanged();
                }
                else{ // removed
                    for (int i = 0; i < requestList.size(); i++) {
                        if (requestList.get(i).getKey().equals(tmp.getKey())) {
                            requestList.remove(i);
                            Timber.d("request removed");
                            break;
                        }
                    }
                    for (int i = 0; i < pendingRequest.size(); i++) {
                        if (pendingRequest.get(i).getKey().equals(tmp.getKey())) {
                            pendingRequest.remove(i);
                            Timber.d("pending removed");
                            break;
                        }
                    }
                    requestAdapter.notifyDataSetChanged();
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

        friendsRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .child(FirebaseAuth.getInstance().getUid())
                .child(getString(R.string.Friends_KEY));

        friendsRef.addChildEventListener(friendsChildListener);
    }

    private void AddFriendBtn(String Email) {

        Timber.d("Add btn");
        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> Timber.d(instanceIdResult.getToken()));

        boolean isRequest=SearchInRequests(Email);
        Timber.d(String.valueOf(requestList.size()));
        if (Email.isEmpty() || Email.equals(user.getEmail()))  return;
        else if(isRequest) Toast.makeText(getContext(), getContext().getString(R.string.pendingRequest), Toast.LENGTH_SHORT).show();
        else if (friendsList == null || friendsList.size() == 0)  GetUserID(Email);
        else {
            boolean found = false;
            for (Friends friends : friendsList) {
                if (friends.getEmail().equals(Email)) {
                    if (getString(R.string.friend).equals(friends.getFriendState())) {
                        Toast.makeText(getContext(), getString(R.string.MSG_isFriend), Toast.LENGTH_SHORT).show();
                    }else {
                        AddFriendReceiver(user, friends);
                        AddFriendSender(friends);
                    }
                    found = true;
                    break;
                }

            }
            if (!found)
                GetUserID(Email);
        }

        txt_Search.getText().clear();
    }

    private boolean SearchInRequests(String Email) {
        for(int i=0;i<requestList.size();i++){
            if(requestList.get(i).getEmail().equals(Email)){
                Timber.d(requestList.get(i).getFriendState());
                 if (getString(R.string.pendingRequest).equals(requestList.get(i).getFriendState())) {
                    Toast.makeText(getContext(), getString(R.string.MSG_pendingRequest), Toast.LENGTH_SHORT).show();
                    return true;
                 }else{
                     fb.Request(requestList.get(i).getKey(), FirebaseAuth.getInstance().getUid(), getContext().getString(R.string.friend));
                     fb.Request(FirebaseAuth.getInstance().getUid(), requestList.get(i).getKey(), getContext().getString(R.string.friend));
                     friendsList.add(requestList.get(i));
                     requestList.remove(i);
                     Timber.d("request removed");
                     return true;
                 }
            }
        }
        return false;
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
        Timber.d(String.valueOf(user.getProfile()));
        data.put(getString(R.string.name), user.getName());
        data.put(getString(R.string.email), user.getEmail());
        data.put(getString(R.string.profile_IMG), user.getProfile());
        data.put(getString(R.string.friendState), getString(R.string.pendingRequest));

        userRef.updateChildren(data)
                .addOnSuccessListener(o -> {
                    Query ref = FirebaseDatabase.getInstance()
                            .getReference()
                            .child(getString(R.string.token))
                            .child(friend.getKey());

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Timber.d(String.valueOf(dataSnapshot));
                            if (dataSnapshot != null) {

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("api")
                                        .document("keys")
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                                    String ServerKey = String.valueOf(documentSnapshot.getData().get("fcm_server_key"));
                                                    Timber.e("Called %s", ServerKey);
                                                    new PushNotification(getContext(), ServerKey)
                                                            .Notify(getString(R.string.Notify_FRIEND_REQUEST_Title)
                                                                    , user.getName() + " " + getString(R.string.Notify_FRIEND_REQUEST_msg)
                                                                    , dataSnapshot.getValue(String.class));
                                                }
                                        );

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());

        Toast.makeText(getContext(), getString(R.string.MSG_Request_Sent), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        friendsRef.removeEventListener(friendsChildListener);
        Timber.d("Listener removed");
    }
}