package com.example.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

public class mFirebase {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference profileUserRef;
    private StorageReference storageReference;
    private Context context;

    public mFirebase(Context context){
        this.context=context;
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
    }

    private void UploadProfileImg(Uri imgUri) {
        final StorageReference filePath = storageReference.child(context.getString(R.string.profile_IMG)).child(FirebaseAuth.getInstance().getUid()+ ".jpg");

        final String[] imgPAth = {null};
        filePath.putFile(imgUri)
                .addOnSuccessListener(task -> filePath.getDownloadUrl()
                        .addOnSuccessListener(uri -> imgPAth[0] =uri.getPath())
                        .addOnFailureListener(e ->{
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            Timber.e(e);
                        }));

        profileUserRef.child(context.getString(R.string.profile_IMG)).setValue(imgPAth)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, context.getString(R.string.image_uploading_success), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->{

                    Toast.makeText(context, context.getString(R.string.image_uploading_fail), Toast.LENGTH_LONG).show();
                    Timber.e(e);
                });

        
    }

    public void Register(User user,String Password,Uri imgUri) {

        ProgressDialog progressDialog=ProgressDialog.show(context, "",
                "creating account. Please wait...", true);

        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), Password)
                .addOnSuccessListener(authResult -> {

                    Timber.d(firebaseAuth.getUid());
                    addingToDatabase(user.getEmail(),user.getEmail());
                    UploadProfileImg(imgUri);
                    openRoomsActivity open = (openRoomsActivity) context;
                    open.LogIn();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    Timber.e(e);
                });
        progressDialog.dismiss();

    }

    private void addingToDatabase(String Email,String name) {

        profileUserRef = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.User_KEY))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        HashMap<String, Object> user = new HashMap<>();
        user.put(context.getString(R.string.name), name);
        user.put(context.getString(R.string.email),Email);
        profileUserRef.updateChildren(user).addOnFailureListener(e ->
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());

    }

    public void Request(String Id1, String Id2, String state) {
        DatabaseReference userRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(context.getString(R.string.User_KEY))
                .child(Id1)
                .child(context.getString(R.string.Friends_KEY))
                .child(Id2);
        HashMap data = new HashMap();
        data.put(context.getString(R.string.friendState), state);
        userRef.updateChildren(data).addOnFailureListener(e ->
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());

    }

}
