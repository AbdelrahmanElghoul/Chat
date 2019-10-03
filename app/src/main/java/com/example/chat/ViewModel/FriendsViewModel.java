package com.example.chat.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.chat.FirebaseQueryLiveDataChildListener;
import com.example.chat.R;
import com.example.chat.Register.MainActivity;
import com.example.chat.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsViewModel extends AndroidViewModel {

    private User user;

    private FirebaseQueryLiveDataChildListener friendsListener;

    public FirebaseQueryLiveDataChildListener getFriendsListener() {
        return friendsListener;
    }

    public FriendsViewModel(@NonNull Application application) {
        super(application);

        DatabaseReference friend_ref = FirebaseDatabase.getInstance()
                .getReference()
                .child(application.getString(R.string.User_KEY))
                .child(MainActivity.currentUserID)
                .child(application.getString(R.string.Friends_KEY));

        friendsListener =new FirebaseQueryLiveDataChildListener(friend_ref);

    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser(){
        return user;
    }

}