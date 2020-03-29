package com.example.chat.Rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Messages;
import com.example.chat.R;
import com.example.chat.Rooms.Adapters.ChatListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ChatListFragment extends Fragment {

    @BindView(R.id.recycler_chat_list)
    RecyclerView recyclerView;

    private ChatListAdapter adapter;
    private List<ChatListData> data;
    private DatabaseReference ref;
    private ChildEventListener childEventListener;
    private LinearLayoutManager layout;
    private ValueEventListener listener;
    private Query tmpQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        ButterKnife.bind(this, view);
        data = new ArrayList<>();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Timber.tag("ChildAdded").d(String.valueOf(dataSnapshot));
                FirebaseData(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Timber.tag("ChildChanged").d(String.valueOf(dataSnapshot));
                FirebaseData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .child(FirebaseAuth.getInstance().getUid())
                .child(getString(R.string.Friends_KEY));

        ref.orderByChild(getString(R.string.new_message)).equalTo("true");
        ref.addChildEventListener(childEventListener);
        setUI();

    }

    private void setUI() {
        adapter = new ChatListAdapter(getContext(), data);
        layout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onStop() {
        super.onStop();
        if(ref!=null)
        ref.removeEventListener(childEventListener);
        if(tmpQuery!=null)
        tmpQuery.removeEventListener(listener);
    }

    private void FirebaseData(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.hasChild(getString(R.string.new_message))) return;

        ChatListData tmp = dataSnapshot.getValue(ChatListData.class);
        tmp.setKey(dataSnapshot.getKey());

        if (tmp.getSessionID() == null) return;

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Timber.tag("Triggered").d(String.valueOf(dataSnapshot));
                if(dataSnapshot.getValue()==null)
                    return;
                Messages msg = dataSnapshot.getChildren().iterator().next().getValue(Messages.class);

                tmp.setMessage(msg.getMessage());
                for (int i = 0; i < data.size(); i++) {
                    Timber.tag("data").d(data.get(i).getKey());
                    Timber.tag("tmp").d(tmp.getKey());
                    if (data.get(i).getKey().equals(tmp.getKey())) {
                        Timber.tag("Before").d(String.valueOf(data.size()));
                        data.remove(i);
                        Timber.tag("After").d(String.valueOf(data.size()));
                        break;
                    }
                }

                if (tmp.isNew_message())
                    data.add(0, tmp);
                else
                    data.add(tmp);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        tmpQuery = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(getString(R.string.Messages_KEY))
                .child(tmp.getSessionID())
                .orderByKey()
                .limitToLast(1);

        tmpQuery.addValueEventListener(listener);

    }
}
