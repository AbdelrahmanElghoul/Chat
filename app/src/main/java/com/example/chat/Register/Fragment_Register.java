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


public class Fragment_Register extends Fragment {


    @BindView(R.id.txt_have_an_account_log_in)
    TextView txtLogIn;
    @BindView(R.id.register_email_txt)
    TextView Email;
    @BindView(R.id.register_password_txt)
    TextView Password;

    @BindView(R.id.btn_register)
    Button Register;

    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_register,container,false);
        ButterKnife.bind(this,v);
        firebaseAuth=FirebaseAuth.getInstance();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NavigateMainFrame navigateMainFrame=(NavigateMainFrame) getContext();
        txtLogIn.setOnClickListener(v-> navigateMainFrame.LoadFragment(new Fragment_LogIn()));
        Register.setOnClickListener(v-> btnRegister());

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

    void btnRegister(){
        if(!ValidateViews()) return;
        Register(Email.getText().toString(),Password.getText().toString());
    }

    void Register(final String Email,final String Password){

        firebaseAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnSuccessListener(authResult -> MainActivity.currentUserID=firebaseAuth.getUid() )
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Timber.e(e);
                });

    }

}
