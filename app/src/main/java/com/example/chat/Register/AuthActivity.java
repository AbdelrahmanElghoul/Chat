package com.example.chat.Register;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chat.R;
import com.example.chat.Rooms.RoomsActivity;
import com.example.chat.openRoomsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.ButterKnife;
import timber.log.Timber;

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
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            Timber.tag("load").d(String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
            getSupportFragmentManager().popBackStack();
        }
        else
            Timber.tag("load").d("loaded"+getSupportFragmentManager().getBackStackEntryCount());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_mainActivity, fragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>1){
            Timber.tag("back").d(String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
            getSupportFragmentManager().popBackStack();
        }
        else
            finishAffinity();
    }

    @Override
    public void LogIn() {
        addToken();
        Intent intent=new Intent(this, RoomsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

    static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher( email ).matches();
    }

    void addToken(){
         String token =
        FirebaseInstanceId
                .getInstance()
                .getToken();

        Timber.tag("token").d(token);

        if(token==null) return;
        DatabaseReference ref= FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.token));

        HashMap<String,Object> map=new HashMap<>();
        map.put(FirebaseAuth.getInstance().getUid(), token);
        ref.updateChildren(map).addOnFailureListener(e->{
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Timber.e(e);
        });
    }



}
