package com.canndecsolutions.garrisongamerss;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Adapters.SlideShowAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HandlingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView Cast_ImageSelect;
    private Button Cast_ImageUpload;
    private EditText Cast_About;
    private Spinner Cast_sp_SportsCategory;

    //    LISTs
    private List<Uri> imageList;
    private List<String> imageUrisList;

    //    STRING ARRAY
    private String imageArray[];

    //    STRINGS
    private String selectSport = null;

    //    DATABASE REFERENCES
    private DatabaseReference mDatabase, SportsInfoRef;
    private StorageReference mStorage, AdminStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handling);


//        CASTINGS
        WidgetsCasting();

        SportsMenu();

        FirebaseCasting();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ImageSelect:
                Toast.makeText(this, "q", Toast.LENGTH_SHORT).show();
                OpenGallery();
                break;

            case R.id.ImageUpload:
                String about = Cast_About.getText().toString().trim();
                if (!imageList.isEmpty()) {

                    for (int i = 0; i < imageList.size(); i++) {
                        UploadImage(i, about);
                    }
                }
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {


            ClipData clipData = data.getClipData();

            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageList.add(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Uri imageUri = data.getData();

                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageList.add(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }

            imageArray = new String[imageList.size()];


        }

    }

    //    ===================================================== CALLING METHODS ============================================================

    private void WidgetsCasting() {

        Cast_ImageSelect = (ImageView) findViewById(R.id.ImageSelect);
        Cast_ImageUpload = (Button) findViewById(R.id.ImageUpload);
        Cast_About = (EditText) findViewById(R.id.About);
        Cast_sp_SportsCategory = (Spinner) findViewById(R.id.sp_SportsCategory);

//             ARRAY INITIALIZE
        imageUrisList = new ArrayList<>();
        imageList = new ArrayList<>();

//        CLICK LISTENERS
        Cast_ImageSelect.setOnClickListener(this);
        Cast_ImageUpload.setOnClickListener(this);

    }

    private void FirebaseCasting() {


        mDatabase = FirebaseDatabase.getInstance().getReference();
        SportsInfoRef = mDatabase.child("Sports-Info");

        mStorage = FirebaseStorage.getInstance().getReference();
        AdminStorage = mStorage.child("Admin");


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

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Sp_Category_Array);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        Cast_sp_SportsCategory.setAdapter(adapter);

        Cast_sp_SportsCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("BSCS")) {

                } else {
                    selectSport = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void OpenGallery() {
        imageList.clear();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void UploadImage(final int i, final String about) {

        final StorageReference SportsRef = AdminStorage.child(selectSport).child(UUID.randomUUID().toString() + ".jpg");

        SportsRef.putFile(imageList.get(i))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        SportsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

//                              URI SAVED IN ARRAY
                                imageArray[i] = uri.toString();

//                                URI SAVED TO FIREBASE
                                UrlSavedToFireabse(about);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String error = e.getMessage();
                                Toast.makeText(HandlingActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(HandlingActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void UrlSavedToFireabse(String about) {

        imageUrisList = new ArrayList<>(Arrays.asList(imageArray));


        HashMap<String, Object> InfoMap = new HashMap<>();

        InfoMap.put("about", about);
        InfoMap.put("images", imageUrisList);


        SportsInfoRef.child(selectSport).setValue(InfoMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (imageUrisList.size() == imageList.size()) {
                                Toast.makeText(HandlingActivity.this, "hurrah", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HandlingActivity.this, "error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}
