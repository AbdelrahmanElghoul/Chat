package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import timber.log.Timber;

public class FirebaseQueryLiveDataChildListener extends LiveData<DataSnapshot> {

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public FirebaseQueryLiveDataChildListener(Query query) {
        this.query = query;
    }

    public FirebaseQueryLiveDataChildListener(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        Timber.d( "onActive");
        query.addChildEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Timber.d( "onInactive");
        query.removeEventListener(listener);
    }

    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            setValue(dataSnapshot);

            Timber.tag("AddedSnapShot").d(String.valueOf(dataSnapshot));//data
            Timber.tag("AddedKey").d(String.valueOf(dataSnapshot.getKey()));//friend key
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            setValue(dataSnapshot);

            Timber.tag("ChangedSnapShot").d(String.valueOf(dataSnapshot));//data
            Timber.tag("ChangedKey").d(String.valueOf(dataSnapshot.getKey()));//key
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e(databaseError.toException(), "Can't listen to query %s", query);
        }
    }
}
