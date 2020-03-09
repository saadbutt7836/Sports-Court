package com.canndecsolutions.garrisongamerss.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Activity.EditProfileActivity;
import com.canndecsolutions.garrisongamerss.Authentication.LoginActivity;
import com.canndecsolutions.garrisongamerss.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    //    CONSTANST
    private static final int PERMISSION_CODE = 0;
    private static final int PICK_IMAGE_CODE = 1;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, PostRef;
    private StorageReference mStorage, UserStorage, ProfileStorage;


    private String userId = null,
            currProfImg = null;

    private Uri uri = null;


    private LinearLayout Cast_ProfileImg_Layout;
    private ImageView Cast_profile_image;
    private Button Cast_Edit_Profile_Btn;
    private TextView Cast_My_Posts, Cast_Change_Pass, Cast_Sign_Out;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseCasting();
        RetrieveUserData();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        WidgetsCasting(view);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ProfileImg_Layout:
                GalleryPermission();
                break;

            case R.id.Edit_Profile_Btn:
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
                break;

            case R.id.Sign_Out:
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GalleryImagePick();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == getActivity().RESULT_OK && requestCode == PICK_IMAGE_CODE) {

            Bundle extras = data.getExtras();
            Log.d("qq", "hello " + String.valueOf(requestCode) + " " + String.valueOf(resultCode) + " " + extras);

            Bitmap photo = null;

            if (extras != null) {
                photo = extras.getParcelable("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                byte[] finalImg = baos.toByteArray();

//                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), photo, "Title", null);

                // The stream to write to a file or directly using the photo


                ImageUploadToStorage(finalImg);
            }


        }
    }


    //        ===================================================== CALLING METHODS ============================================================


    private void WidgetsCasting(View view) {
        Cast_ProfileImg_Layout = (LinearLayout) view.findViewById(R.id.ProfileImg_Layout);
        Cast_profile_image = (ImageView) view.findViewById(R.id.profile_image);
        Cast_Edit_Profile_Btn = (Button) view.findViewById(R.id.Edit_Profile_Btn);
        Cast_My_Posts = (TextView) view.findViewById(R.id.My_Posts);
        Cast_Change_Pass = (TextView) view.findViewById(R.id.Change_Pass);
        Cast_Sign_Out = (TextView) view.findViewById(R.id.Sign_Out);


//        CLICK LISTENERS
        Cast_ProfileImg_Layout.setOnClickListener(this);
        Cast_Edit_Profile_Btn.setOnClickListener(this);
        Cast_Sign_Out.setOnClickListener(this);
    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");

        mStorage = FirebaseStorage.getInstance().getReference();

        UserStorage = mStorage.child("Users");
        ProfileStorage = UserStorage.child("Profiles/");

    }

    private void RetrieveUserData() {

        userId = mAuth.getCurrentUser().getUid();


        UserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profile_img")) {

                        currProfImg = dataSnapshot.child("profile_img").getValue().toString();
                    }

                    Picasso.get().load(currProfImg).into(Cast_profile_image);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void GalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                GalleryImagePick();
            }
        } else {
            GalleryImagePick();

        }
    }

    private void GalleryImagePick() {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_PICK);

        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 2);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PICK_IMAGE_CODE);

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void ImageUploadToStorage(byte[] finalImg) {

        final StorageReference filePath = ProfileStorage.child(userId + ".jpg");
        UploadTask uploadTask = filePath.putBytes(finalImg);


        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UriSavedToFirebase(uri);
                        }
                    });
                } else {
                    String error = task.getException().toString();
                    Toast.makeText(getActivity(), "error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void GetKeyOfCurrentUser(final Uri uri) {
//
//        UserRef.child(currentUserId).
//                addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            userKey = snapshot.getKey();
//
//                            UriSavedToFirebase(userKey, uri);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        String error = databaseError.getMessage();
//                        Toast.makeText(getActivity(), "error: " + error, Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }

    private void UriSavedToFirebase(Uri uri) {


        Map<String, Object> UpdateUri = new HashMap<>();
        UpdateUri.put("profile_img", uri.toString());

        UserRef.child(userId).updateChildren(UpdateUri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
