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
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


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

    LinearLayoutManager chatLayout;
    Friends friends;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_chat,container,false);
        ButterKnife.bind(this,v);
        friends=getArguments().getParcelable(getString(R.string.Friends_KEY));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SetUI();

        btnBack.setOnClickListener( v-> getFragmentManager().popBackStackImmediate());
        btnSend.setOnClickListener(v-> {
            AddMessage(txtChat.getText().toString());
            txtChat.getText().clear();
        });
    }

    private void CreateChatSession() {

       friends.setSessionID(generateKey());
       FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.Messages_KEY))
                .child(friends.getSessionID());

        setMessageID(FirebaseAuth.getInstance().getUid(),friends.getKey(),friends.getSessionID());
        setMessageID(friends.getKey(),FirebaseAuth.getInstance().getUid(),friends.getSessionID());

    }

    private void AddMessage(String Message){
        if(Message==null)
            return;
        if(friends.getSessionID()==null){
            CreateChatSession();
        }

       DatabaseReference ref=FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.Messages_KEY))
                .child(friends.getSessionID());


        HashMap<String, Object> user = new HashMap<>();
        user.put(getString(R.string.sender), FirebaseAuth.getInstance().getUid());
        user.put(getString(R.string.message),Message);

        Date date= Calendar.getInstance().getTime();

        DateFormat myFormatObj =new DateFormat();
        String formattedDate = myFormatObj.format("dd-MM-yyyy HH:mm",date).toString();
        user.put(getString(R.string.timeStamp),formattedDate);

        ref.push().updateChildren(user).addOnFailureListener(e ->
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());


    }

    private void setMessageID(String UserID,String FriendID,String sessionID){
        DatabaseReference ref=FirebaseDatabase
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

    void SetUI(){
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
                    }
                });

        txtAppbar.setText(friends.getName());
        chatRecycler.setHasFixedSize(true);
        chatLayout=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        chatRecycler.setLayoutManager(chatLayout);


    }

    String generateKey(){
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .push().getKey();

    }

}
