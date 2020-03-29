package com.example.chat.Rooms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chat.R;
import com.example.chat.Register.AuthActivity;
import com.example.chat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;
import timber.log.Timber;

public class RoomsActivity extends AppCompatActivity implements OpenChatFragment {

    private static final String CHAT_TAG = ChatFragment.class.getSimpleName();
    private static final String ROOM_TAG = RoomsFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rooms);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
        Firebase.setPersistence();

        if (FirebaseAuth.getInstance().getUid() == null)
            startActivity(new Intent(this, AuthActivity.class));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getUid() == null)
            return;
        Timber.d(String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Timber.d("if");
        } else {
            Timber.d("Else");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.rooms_frame, new RoomsFragment())
                    .addToBackStack(ROOM_TAG)
                    .commit();
        }

    }

    @Override
    public void onBackPressed() {
        Timber.tag("Back").d(String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
        boolean RoomsFrag = false;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            RoomsFrag = getSupportFragmentManager().popBackStackImmediate(ROOM_TAG, 0);
        }
        if (!RoomsFrag)
            finishAffinity();
    }

    @Override
    public void openFragment(User friends, Fragment fragment) {
        Bundle b = new Bundle();
        b.putParcelable(getString(R.string.Friends_KEY), friends);
        fragment.setArguments(b);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                        R.anim.slide_out_right, R.anim.slide_in_right)
                .replace(R.id.rooms_frame, fragment)
                .addToBackStack(CHAT_TAG)
                .commit();
    }

}

class Firebase {

    private static boolean persistence = false;

    static void setPersistence() {
        if (!persistence) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            persistence = true;
        }
    }
}
