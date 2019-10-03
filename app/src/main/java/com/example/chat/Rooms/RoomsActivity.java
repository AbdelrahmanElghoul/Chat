package com.example.chat.Rooms;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.R;

public class RoomsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        getSupportFragmentManager().beginTransaction().add(R.id.rooms_frame,new RoomsFragment()).commit();
    }
}
