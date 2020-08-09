package com.example.chat.Register;

import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chat.EnumContainer;
import com.example.chat.mFirebase;
import com.example.chat.R;
import com.example.chat.User;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class Fragment_Register extends Fragment {


    @BindView(R.id.txt_have_an_account_log_in)
    TextView txtLogIn;
    @BindView(R.id.register_email_txt)
    EditText txtEmail;
    @BindView(R.id.register_password_txt_layout)
    TextInputLayout PasswordLayout;
    @BindView(R.id.register_password_txt)
    EditText txtPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.register_img)
    ImageView imgProfile;
    @BindView(R.id.register_name_txt)
    EditText txtName;

    private String Email;
    private String Name;
    private String Password;

//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference profileUserRef;
//    private StorageReference storageReference;
    private Uri imgUri;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtLogIn.setOnClickListener(v -> ((NavigateMainFrag) getContext()).LoadFragment(new Fragment_LogIn()));
        btnRegister.setOnClickListener(v -> btnRegister());
        imgProfile.setOnClickListener(v -> GetImageFromGallery());

    }

    private void GetImageFromGallery() {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,EnumContainer.RequestCode.GET_IMAGE_RESULT.Value);
    }

    private boolean ValidateViews() {
        Name= txtName.getText().toString();
        Email=txtEmail.getText().toString();
        Password=txtPassword.getText().toString();

        boolean validate = true;

        if (Email.isEmpty() || !AuthActivity.isEmailValid(txtEmail.getText().toString())) {
            txtEmail.setError(getString(R.string.wrong_email_format));
            validate = false;
        }
        if (Name.isEmpty()) {
            txtName.setError(getString(R.string.Empty_field_msg));
            validate = false;
        }
        if (Password.isEmpty() || txtPassword.getText().toString().length()<6) {
            PasswordLayout.setError(getString(R.string.password_error_msg));
            validate = false;
        }

        return validate;
    }

    private void btnRegister() {
        if (!ValidateViews()) return;
        Register(new User(Name,Email,null),Password,imgUri);
    }

    private void Register(User user,String Password,Uri imgUri) {

        ProgressDialog progressDialog=ProgressDialog.show(getContext(), "",
                "creating account. Please wait...", true);
        new mFirebase(getContext()).Register(user,Password,imgUri);
        progressDialog.dismiss();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==EnumContainer.RequestCode.GET_IMAGE_RESULT.Value && resultCode==RESULT_OK && data!=null){
            imgUri=data.getData();
            Glide.with(getContext())
                    .load(imgUri)
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.avatar)
                    .into(imgProfile);
        }
    }
}


