package com.example.chat.Register;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chat.R;
import com.example.chat.Rooms.RoomsActivity;
import com.example.chat.openRoomsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigateMainFrame , openRoomsActivity {

    public static String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if(FirebaseAuth.getInstance().getUid()!= null){
            currentUserID=FirebaseAuth.getInstance().getUid();
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
        startActivity(intent);
    }
}
