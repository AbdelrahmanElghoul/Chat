package com.example.chat.Rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.chat.CircleTransform;
import com.example.chat.R;
import com.example.chat.Rooms.Adapters.ViewPagerAdapter;
import com.example.chat.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RoomsFragment extends Fragment {

    @BindView(R.id.tab_layout)
    TabLayout tabs;
    @BindView(R.id.view_pager)
    ViewPager pages;
    @BindView(R.id.img_exit)
    ImageButton btn_logout;
    @BindView(R.id.txt_app_bar)
    TextView AppBar;
    @BindView(R.id.avatar)
    ImageView avatar;
    private User user ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_rooms,container,false);
        ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        btn_logout.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            FirebaseAuth.getInstance().signOut();
            getActivity().finishAffinity();
        });

        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.User_KEY))
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Timber.d(String.valueOf(dataSnapshot));
                        user = new User();

                        if (dataSnapshot.hasChild(getString(R.string.name)))
                            user.setName(dataSnapshot.child(getString(R.string.name)).getValue(String.class));
                        if (dataSnapshot.hasChild(getString(R.string.email)))
                            user.setEmail(dataSnapshot.child(getString(R.string.email)).getValue(String.class));
                        if (dataSnapshot.hasChild(getString(R.string.profile_IMG)))
                            user.setProfilePic(dataSnapshot.child(getString(R.string.profile_IMG)).getValue(String.class));

                        AppBar.setText(user.getName());

                        FriendsFragment friendsFragment=new FriendsFragment();
                        Bundle b=new Bundle();
                        b.putParcelable(getString(R.string.User_KEY),user);
                        friendsFragment.setArguments(b);

                        ChatListFragment chatListFragment=new ChatListFragment();
                        Bundle bundle=new Bundle();
                        bundle.putParcelable(getString(R.string.User_KEY),user);
                        chatListFragment.setArguments(bundle);

                        adapter.addList(chatListFragment, getString(R.string.Messages_KEY));
                        adapter.addList(friendsFragment, getString(R.string.friend));
                        pages.setAdapter(adapter);
                        tabs.setupWithViewPager(pages);

                        Picasso.get()
                                .load(user.getProfilePic())
                                .transform(new CircleTransform())
                                .into(avatar, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        avatar.setImageResource(R.drawable.avatar);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}
