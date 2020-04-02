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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Models.Posts;
import com.canndecsolutions.garrisongamerss.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class PostUpload extends Fragment implements View.OnClickListener {


    //    CONSTANST
    private static final int PERMISSION_CODE = 0;
    private static final int PICK_IMAGE_CODE = 1;

    private EditText Cast_Post_Text;
    private Button Cast_Post_Btn;
    private ImageButton Cast_Image_Select;
    private ImageView Cast_Post_Img;
    private Spinner Cast_sp_SportsCategory;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, PostRef;
    private StorageReference mStorage, PostStorage, PostImgStorage;

    private String userId = null,
            saveableImgUrl = null,
            selectedSport = null,
            status = null;
    private int type = 0;

    private Uri postUri = null;
    private byte[] finalImg = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseCasting();

        RetrieveUserData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);


        WidgetsCasting(view);

        SportsMenu();


        return view;
    }


    private void WidgetsCasting(View view) {
        Cast_Post_Text = (EditText) view.findViewById(R.id.Post_Text);
        Cast_Image_Select = (ImageButton) view.findViewById(R.id.Image_Select);
        Cast_Post_Img = (ImageView) view.findViewById(R.id.Post_Img);
        Cast_Post_Btn = (Button) view.findViewById(R.id.Post_Btn);
        Cast_sp_SportsCategory = (Spinner) view.findViewById(R.id.sp_SportsCategory);


//        CLICK LISTENERS
        Cast_Post_Btn.setOnClickListener(this);
        Cast_Image_Select.setOnClickListener(this);
        Cast_Post_Text.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Image_Select:
                GalleryPermission();
                break;
            case R.id.Post_Btn:
                status = Cast_Post_Text.getText().toString().trim();

                if (status.isEmpty() && postUri == null) {

                    Toast.makeText(getActivity(), "Empty post not allowed", Toast.LENGTH_SHORT).show();

                } else if (postUri == null) {

//                    ONLY STATUS UPDATED SUCCESSFULLY

                    type = 0;
                    String extraUri = "";
                    PostUpload(status, extraUri, type);
                } else if (status.isEmpty()) {

//                    ONLY IMAGE UPDATED SUCCESSFULLY
                    type = 1;
                    status = "";
                    ImageUploadANDGetURL();
//                    PostUpload(status, ImageUploadANDGetURL(), type);

                } else if (!status.isEmpty() && postUri != null) {

                    //         BOTH STATUS AND IMAGE UPLOAD
                    type = 2;
                    ImageUploadANDGetURL();
//                    PostUpload(status, ImageUploadANDGetURL(), type);
                } else {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
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
            postUri = data.getData();

            ImageCompressed(postUri);
            Cast_Post_Img.setImageURI(postUri);


        }
    }


    //    ===================================================== CALLING METHODS ============================================================

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

//        DATABASE REFERENCES
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UserRef = mDatabase.child("Users");
        PostRef = mDatabase.child("Posts");


//        STORAGE REFERENCES
        mStorage = FirebaseStorage.getInstance().getReference();
        PostStorage = mStorage.child("Posts");
        PostImgStorage = PostStorage.child("images/");
    }

    private void RetrieveUserData() {

        userId = mAuth.getCurrentUser().getUid();
//        Query query = UserRef.orderByChild("uid").equalTo(userId);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                        String email = snapshot.child("email").getValue().toString();
//                        String name = snapshot.child("name").getValue().toString();
//                        String profileImg = snapshot.child("profile_img").getValue().toString();
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                String error = databaseError.getMessage();
//                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void SportsMenu() {

        //        COURSE ARRAY
        List<String> Sp_Category_Array = new ArrayList<>();
        Sp_Category_Array.add(0, "Cricket");
        Sp_Category_Array.add("Football");
        Sp_Category_Array.add("Badminton");
        Sp_Category_Array.add("Tennis");
        Sp_Category_Array.add("Table Tennis");
        Sp_Category_Array.add("Squash");

        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, Sp_Category_Array);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        Cast_sp_SportsCategory.setAdapter(adapter);

        Cast_sp_SportsCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("BSCS")) {

                } else {
                    selectedSport = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        startActivityForResult(intent, PICK_IMAGE_CODE);

    }

    private void ImageCompressed(Uri uri) {
        File actualImage = new File(getRealPathFromURI(uri));

        try {
            Bitmap CompressedImg = new Compressor(getActivity())
                    .setMaxWidth(250)
                    .setMaxHeight(250)
                    .setQuality(100)
                    .compressToBitmap(actualImage);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CompressedImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            finalImg = baos.toByteArray();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String ImageUploadANDGetURL() {

        final StorageReference imagePath = PostImgStorage.child(userId).child(UUID.randomUUID().toString() + ".jpg");
        final UploadTask uploadTask = imagePath.putBytes(finalImg);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        saveableImgUrl = uri.toString();
                        PostUpload(status, saveableImgUrl, type);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String error = e.getMessage();
                                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

        return saveableImgUrl;

    }

    private void PostUpload(String status, String postImg, int type) {

        String pid = mDatabase.push().getKey();


        Double timeStamp = Double.valueOf(System.currentTimeMillis());
        Posts post = new Posts(pid, userId, status, postImg, selectedSport, timeStamp, type);

        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/Posts/" + pid, postValues);
//        childUpdates.put("/User-Posts/" + userId + "/" + pid, postValues);star


        mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Post Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Cast_Post_Text.setText("");
                postUri = null;
                Cast_Post_Img.setImageURI(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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

}
