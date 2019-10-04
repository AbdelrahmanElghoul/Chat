package com.example.chat.Rooms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.Friends;
import com.example.chat.R;
import com.example.chat.Register.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RoomsActivity extends AppCompatActivity implements OpenChatFragment{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        if(FirebaseAuth.getInstance().getUid()==null)
            startActivity(new Intent(this, AuthActivity.class));
        else
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.rooms_frame,new RoomsFragment())
                    .commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rooms_frame,new RoomsFragment())
                .commit();

    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0)
            getSupportFragmentManager().popBackStackImmediate();
        else
            super.onBackPressed();

    }

    @Override
    public void openFragment(Friends friends) {

        ChatFragment chatFragment=new ChatFragment();
        Bundle b=new Bundle();
        b.putParcelable(getString(R.string.Friends_KEY),friends);
        chatFragment.setArguments(b);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rooms_frame,chatFragment)
                .addToBackStack(null)
                .commit();
    }
}
