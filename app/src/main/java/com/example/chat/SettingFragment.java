package com.example.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


public class SettingFragment extends Fragment {

    @BindView(R.id.setting_img)
    ImageView imgProfile;
    @BindView(R.id.setting_back)
    ImageView btnBack;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_update_password)
    Button btnUpdate;
    @BindView(R.id.setting_name_txt)
    EditText txtName;
    @BindView(R.id.setting_email_txt)
    EditText txtEmail;
    @BindView(R.id.setting_old_password_txt)
    EditText txtOldPassword;
    @BindView(R.id.setting_new_password_txt)
    EditText txtNewPassword;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference profileUserRef;
    private StorageReference storageReference;
    private Uri imgUri;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, v);
        user = getArguments().getParcelable(getString(R.string.Friends_KEY));

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnBack.setOnClickListener(v-> btnBackListener());
        btnUpdate.setOnClickListener(v-> btnUpdateListener());
        btnSave.setOnClickListener(v-> btnSaveListener());
        imgProfile.setOnClickListener(v-> GetImageFromGallery());

    }

    private void GetImageFromGallery() {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,EnumContainer.RequestCode.GET_IMAGE_RESULT.Value);
    }

    private void btnBackListener(){

    }

    private void btnSaveListener(){
    }

    private void btnUpdateListener(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==EnumContainer.RequestCode.GET_IMAGE_RESULT.Value && resultCode==RESULT_OK && data!=null){
            imgUri=data.getData();

            Glide.with(getContext()).load(imgUri).apply(RequestOptions.circleCropTransform()).error(R.drawable.avatar).into(imgProfile);
        }
    }

    private void UploadProfileImg() {
        if (imgUri == null) return;

        final StorageReference filePath = storageReference.child(getString(R.string.profile_IMG)).child(FirebaseAuth.getInstance().getUid()+ ".jpg");

        filePath.putFile(imgUri)
                .addOnSuccessListener(task ->
                        filePath.getDownloadUrl()
                                .addOnSuccessListener(uri ->
                                        profileUserRef.child(getString(R.string.profile_IMG)).setValue(uri.toString())
                                                .addOnFailureListener(e ->{
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    Timber.e(e);
                                                })
                                ).addOnFailureListener(e ->{
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            Timber.e(e);
                        }));

    }

}
