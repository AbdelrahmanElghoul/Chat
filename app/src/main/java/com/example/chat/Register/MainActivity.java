package com.example.chat.Register;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chat.R;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigateMainFrame {

    public static String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        LoadFragment(new Fragment_LogIn());
    }

    @Override
    public void LoadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_mainActivity, fragment).commit();

    }

    static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher( email ).matches();
    }
}
