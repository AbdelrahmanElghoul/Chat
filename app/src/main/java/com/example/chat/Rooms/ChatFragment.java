package com.example.chat.Rooms;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.CircleTransform;
import com.example.chat.Friends;
import com.example.chat.Messages;
import com.example.chat.Notification.PushNotification;
import com.example.chat.R;
import com.example.chat.Rooms.Adapters.ChatSessionAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class ChatFragment extends Fragment {

    @BindView(R.id.avatar)
    ImageView imgAvatar;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.txt_app_bar)
    TextView txtAppbar;
    @BindView(R.id.recycler_chat)
    RecyclerView chatRecycler;
    @BindView(R.id.btn_send)
    ImageButton btnSend;
    @BindView(R.id.txt_chat)
    EditText txtChat;
    private DatabaseReference chat_ref;
    private LinearLayoutManager chatLayout;
    private Friends friends;
    private ChildEventListener chatListener;
    private ChatSessionAdapter chatAdapter;
    private List<Messages> messages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, v);
        friends = getArguments().getParcelable(getString(R.string.Friends_KEY));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SetUI();

        if (friends.getSessionID() == null) {
            CreateChatSession();
        }
        btnBack.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());
        btnSend.setOnClickListener(v -> {
            AddMessage(txtChat.getText().toString());
            txtChat.getText().clear();
        });

        setNewMessage(FirebaseAuth.getInstance().getUid(), friends.getKey(), false);
        setNewMessage(friends.getKey(), FirebaseAuth.getInstance().getUid(), false);

        chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Timber.d(String.valueOf(dataSnapshot));
                Messages tmp = dataSnapshot.getValue(Messages.class);
                messages.add(tmp);
                chatAdapter.notifyDataSetChanged();
                chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        chat_ref = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.Messages_KEY))
                .child(friends.getSessionID());

        chat_ref.addChildEventListener(chatListener);
    }

    private void CreateChatSession() {

        friends.setSessionID(generateKey());
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.Messages_KEY))
                .child(friends.getSessionID());

        setMessageID(FirebaseAuth.getInstance().getUid(), friends.getKey(), friends.getSessionID());
        setMessageID(friends.getKey(), FirebaseAuth.getInstance().getUid(), friends.getSessionID());

    }

    private void AddMessage(String Message) {
        if (Message == null || Message.isEmpty())
            return;

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.Messages_KEY))
                .child(friends.getSessionID());


        HashMap<String, Object> user = new HashMap<>();
        user.put(getString(R.string.sender), FirebaseAuth.getInstance().getUid());
        user.put(getString(R.string.message), Message);

        Date date = Calendar.getInstance().getTime();

        DateFormat myFormatObj = new DateFormat();
        String formattedDate = myFormatObj.format("dd-MM-yyyy HH:mm", date).toString();
        user.put(getString(R.string.timeStamp), formattedDate);

        ref.push().updateChildren(user).addOnFailureListener(e ->
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.token))
                .child(friends.getKey());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)  return;
                     Timber.tag("token").d(String.valueOf(dataSnapshot.getValue()));
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("api")
                            .document("keys")
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                        String ServerKey = String.valueOf(documentSnapshot.getData().get("fcm_server_key"));

                                        new PushNotification(getContext(), ServerKey)
                                                .Notify(friends.getName()
                                                        , Message
                                                        , dataSnapshot.getValue(String.class));
                                    }
                            );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setNewMessage(FirebaseAuth.getInstance().getUid(), friends.getKey(), true);
        setNewMessage(friends.getKey(), FirebaseAuth.getInstance().getUid(), true);


    }

    private void setNewMessage(String userID, String friendID, boolean read) {
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .child(userID)
                .child(getString(R.string.Friends_KEY))
                .child(friendID);

        HashMap<String, Object> user = new HashMap<>();
        user.put(getString(R.string.new_message), read);

        ref.updateChildren(user).addOnFailureListener(e ->
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void setMessageID(String UserID, String FriendID, String sessionID) {
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .child(UserID)
                .child(getString(R.string.Friends_KEY))
                .child(FriendID);

        HashMap<String, Object> user = new HashMap<>();
        user.put(getString(R.string.sessionID), sessionID);

        ref.updateChildren(user).addOnFailureListener(e ->
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void SetUI() {
        Picasso.get()
                .load(friends.getProfile())
                .transform(new CircleTransform())
                .into(imgAvatar, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        imgAvatar.setBackgroundResource(R.drawable.avatar);
                        Timber.e(e);
                    }
                });

        txtAppbar.setText(friends.getName());
        chatRecycler.setHasFixedSize(true);
        chatLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        chatRecycler.setLayoutManager(chatLayout);

        chatAdapter = new ChatSessionAdapter(getContext(), messages);
        chatRecycler.setAdapter(chatAdapter);
        if (chatAdapter.getItemCount() > 1)
            chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    public static String generateKey() {
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .push().getKey();

    }

    @Override
    public void onPause() {
        super.onPause();
        chat_ref.removeEventListener(chatListener);
    }

}
