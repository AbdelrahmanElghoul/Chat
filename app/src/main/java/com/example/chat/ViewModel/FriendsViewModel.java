package com.example.chat.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.chat.R;
import com.example.chat.Register.MainActivity;
import com.example.chat.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class FriendsViewModel extends AndroidViewModel {

    private User user;
    private ChildEventListener mChildEventListener;
    public FriendsViewModel(@NonNull Application application) {
        super(application);

        user=new User();
        Toast.makeText(application, "Loading", Toast.LENGTH_SHORT).show();
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getApplication().getString(R.string.User_KEY))
                .child(MainActivity.currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d:dataSnapshot.getChildren()){
                            user.setName((String) d.child(getApplication().getString(R.string.name)).getValue());
                            Timber.d(user.getName());
                            Toast.makeText(application, user.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(application, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public User getUser() {
        return user;
    }
}
