package com.example.chat.Rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chat.R;
import com.example.chat.Rooms.Adapters.ViewPagerAdapter;
import com.example.chat.SettingFragment;
import com.example.chat.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RoomsFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabs;
    @BindView(R.id.view_pager)
    ViewPager pages;
    @BindView(R.id.txt_app_bar)
    TextView AppBar;
    @BindView(R.id.avatar)
    ImageView imgProfile;

    private User user ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_rooms,container,false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());


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

                        FriendsFragment friendsFragment = new FriendsFragment();
                        Bundle b = new Bundle();
                        b.putParcelable(getString(R.string.User_KEY), user);
                        friendsFragment.setArguments(b);

                        ChatListFragment chatListFragment = new ChatListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(getString(R.string.User_KEY), user);
                        chatListFragment.setArguments(bundle);

                        adapter.addList(chatListFragment, getString(R.string.Messages_KEY));
                        adapter.addList(friendsFragment, getString(R.string.friend));
                        pages.setAdapter(adapter);
                        tabs.setupWithViewPager(pages);

                        Glide.with(getContext())
                                .load(user.getProfile())
                                .apply(RequestOptions.circleCropTransform())
                                .error(R.drawable.avatar)
                                .into(imgProfile);
                        Timber.d(user.getProfile());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });



        toolbar.setOnMenuItemClickListener(this::MenuCallBack);


    }

    private boolean MenuCallBack(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_setting:
                Timber.tag("menu").d("Setting");
                ((OpenChatFragment) getContext() ).openFragment(user,new SettingFragment());
                break;
            case R.id.menu_logout:
                Timber.tag("menu").d("logout");
                LogOut();
                break;
            default:
                break;
        }
        return true;
    }

    private void LogOut(){

        new Thread(() -> {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        FirebaseAuth.getInstance().signOut();
        getActivity().finishAffinity();
    }

}
