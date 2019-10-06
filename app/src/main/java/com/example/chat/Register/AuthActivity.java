package com.example.chat.Register;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chat.R;
import com.example.chat.Rooms.RoomsActivity;
import com.example.chat.openRoomsActivity;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity implements NavigateMainFrag, openRoomsActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        if(FirebaseAuth.getInstance().getUid()!= null){
            LogIn();
        }
        else
            LoadFragment(new Fragment_LogIn());

    }

    @Override
    public void LoadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_mainActivity, fragment).commit();

    }

    static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher( email ).matches();
    }

    @Override
    public void LogIn() {
        Intent intent=new Intent(this, RoomsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
