package com.example.chat.Register;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chat.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


public class Fragment_Register extends Fragment {


    @BindView(R.id.txt_have_an_account_log_in)
    TextView txtLogIn;
    @BindView(R.id.register_email_txt)
    EditText Email;

    @BindView(R.id.register_password_txt_layout)
    TextInputLayout PasswordLayout;
    @BindView(R.id.register_password_txt)
    EditText Password;
    @BindView(R.id.btn_register)
    Button Register;
    @BindView(R.id.register_img)
    ImageView img;
    @BindView(R.id.register_name_txt)
    EditText Name;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference profileUserRef;
    private StorageReference storageReference;
    private Uri imgUri;

    private static final int GET_IMAGE_RESULT=1932;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, v);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NavigateMainFrame navigateMainFrame = (NavigateMainFrame) getContext();
        txtLogIn.setOnClickListener(v -> navigateMainFrame.LoadFragment(new Fragment_LogIn()));
        Register.setOnClickListener(v -> btnRegister());
        img.setOnClickListener(v->{
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,GET_IMAGE_RESULT);
        });

    }

    private boolean ValidateViews() {
        boolean validate = true;
        if (Email.getText().toString().isEmpty() || !MainActivity.isEmailValid(Email.getText().toString())) {
            Email.setError(getString(R.string.wrong_email_format));
            validate = false;
        }
        if (Name.getText().toString().isEmpty()) {
            Name.setError(getString(R.string.Empty_field_msg));
            validate = false;
        }
        if (Password.getText().toString().isEmpty() || Password.getText().toString().length()<6) {
            PasswordLayout.setError(getString(R.string.password_error_msg));
            validate = false;
        }
        return validate;
    }

    private void btnRegister() {
        if (!ValidateViews()) return;
        Register(Email.getText().toString(), Password.getText().toString());
    }

    private void Register(final String Email, final String Password) {

        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnSuccessListener(authResult -> {
                    MainActivity.currentUserID = firebaseAuth.getUid();
                    addingToDatabase(Name.getText().toString());
                    UploadProfileImg();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Timber.e(e);
                });

    }

    private void addingToDatabase(String name) {

        profileUserRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(MainActivity.currentUserID);
        HashMap user = new HashMap();
        user.put("name", name);
        profileUserRef.updateChildren(user).addOnFailureListener(e ->
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());


    }

    private void UploadProfileImg() {
        if (imgUri == null) return;

        final StorageReference filePath = storageReference.child(MainActivity.currentUserID + ".jpg");

        filePath.putFile(imgUri)
                .addOnSuccessListener(task ->
                        filePath.getDownloadUrl()
                                .addOnSuccessListener(uri ->
                                        profileUserRef.child("profileIMG").setValue(uri.toString())
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show())
                                ).addOnFailureListener(e ->
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show()));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GET_IMAGE_RESULT && resultCode==RESULT_OK && data!=null){
            imgUri=data.getData();

            Picasso.get().load(imgUri)
                    .error(R.drawable.avatar)
                    .into(img, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e)
                        {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            Timber.d(e);
                        }
                    });
        }
    }

}


