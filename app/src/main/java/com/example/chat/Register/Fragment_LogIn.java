package com.example.chat.Register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class Fragment_LogIn extends Fragment {

    @BindView(R.id.txt_create_new_account)
    TextView txtNewAcc;

    @BindView(R.id.log_in_email_txt)
    TextView Email;
    @BindView(R.id.log_in_password_txt)
    TextView Password;

    @BindView(R.id.btn_log_in)
    Button LogIn;
    private FirebaseAuth firebaseAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_log_in,container,false);
        ButterKnife.bind(this,v);
        firebaseAuth=firebaseAuth.getInstance();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NavigateMainFrame navigateMainFrame=(NavigateMainFrame) getContext();
        txtNewAcc.setOnClickListener(v->  navigateMainFrame.LoadFragment(new Fragment_Register()));

        LogIn.setOnClickListener(v->LogIn());
    }
    boolean ValidateViews(){
        boolean validate=true;
        if(Email.getText().toString().isEmpty() || !MainActivity.isEmailValid(Email.getText().toString())) {
            Email.setError(getString(R.string.wrong_email_format));
            validate=false;
        }
        if(Password.getText().toString().isEmpty()){
            Password.setError(getString(R.string.password_error_msg));
            validate=false;
        }
        return validate;
    }

    void LogIn(){

        if(!ValidateViews()) return;
        firebaseAuth.signInWithEmailAndPassword(Email.getText().toString(),Password.getText().toString())
                .addOnSuccessListener(authResult-> MainActivity.currentUserID = firebaseAuth.getUid()
                        )
                .addOnFailureListener(e-> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Timber.e(e);}
        );
    }
}
