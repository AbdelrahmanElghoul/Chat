package com.example.chat.Rooms;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.chat.R;
import com.example.chat.Register.MainActivity;
import com.example.chat.Rooms.Adapters.ViewPagerAdapter;
import com.example.chat.User;
import com.example.chat.ViewModel.FriendsViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RoomsActivity extends AppCompatActivity {

    @BindView(R.id.tab_layout)
    TabLayout tabs;
    @BindView(R.id.view_pager)
    ViewPager pages;
    @BindView(R.id.img_exit)
    ImageButton btn_logout;

    FriendsViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        ButterKnife.bind(this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addList(new FriendsFragment(), "add");

        viewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        btn_logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finishAffinity();
        });

        if (viewModel.getUser() == null)
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.User_KEY))
                    .child(MainActivity.currentUserID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Timber.d(String.valueOf(dataSnapshot));
                            User user = new User();

                            if (dataSnapshot.hasChild(getString(R.string.name)))
                                user.setName(dataSnapshot.child(getString(R.string.name)).getValue(String.class));
                            if (dataSnapshot.hasChild(getString(R.string.email)))
                                user.setEmail(dataSnapshot.child(getString(R.string.email)).getValue(String.class));
                            if (dataSnapshot.hasChild(getString(R.string.profile_IMG)))
                                user.setProfilePic(dataSnapshot.child(getString(R.string.profile_IMG)).getValue(String.class));

                            /*if (dataSnapshot.hasChild(getString(R.string.Friends_KEY))) {
                                List<Friends> friends = new ArrayList<>();
                                for (DataSnapshot d : dataSnapshot.child(getString(R.string.Friends_KEY)).getChildren()) {
                                    Friends tmp = d.getValue(Friends.class);
                                    tmp.setID(d.getKey());
                                    friends.add(tmp);
                                }
                                user.setFriends(friends);
                            }*/
                            viewModel.setUser(user);

                            pages.setAdapter(adapter);
                            tabs.setupWithViewPager(pages);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(RoomsActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

    }
}
