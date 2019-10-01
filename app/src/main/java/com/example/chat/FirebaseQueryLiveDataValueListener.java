package com.example.chat;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class FirebaseQueryLiveDataValueListener extends LiveData<DataSnapshot> {

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public FirebaseQueryLiveDataValueListener(Query query) {
        this.query = query;
    }

    public FirebaseQueryLiveDataValueListener(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        Timber.d( "onActive");
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Timber.d( "onInactive");
        query.removeEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e(databaseError.toException(), "Can't listen to query %s", query);
        }
    }
}

