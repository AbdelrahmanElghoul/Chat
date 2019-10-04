package com.example.chat.Rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.CircleTransform;
import com.example.chat.Friends;
import com.example.chat.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

    }

    void Back(){

    }
}
