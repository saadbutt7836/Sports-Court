package com.canndecsolutions.garrisongamerss.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.canndecsolutions.garrisongamerss.Adapters.SlideShowAdapter;
import com.canndecsolutions.garrisongamerss.Models.SlideShowModelClass;
import com.canndecsolutions.garrisongamerss.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SportsInfoActivity extends AppCompatActivity implements View.OnClickListener {

    //WIDGETS
    private TextView Cast_TextView, Cast_About;
    private ImageView Cast__Back_Press;
    private ViewFlipper Cast_ViewFlipper;
    private ViewPager Cast_ViewPager;

    //LISTs
    private ArrayList<String> arrayList;
    private SlideShowAdapter slideShowAdapter;

    //    DATABASE REFERENCES
    private DatabaseReference mDatabase, SportsInfoRef;


    //    STRINGS
    private String title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_info);

        getSupportActionBar().hide();


//        CASTINGS
        WidgetsCasting();

        FirebaseCasting();

        RetrieveUserInfo();

//        for (String images : image) {
//            FlipperImages(images);
//        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Back_Press:
                onBackPressed();
                break;
        }
    }


    //    ===================================================== CALLING METHODS ============================================================

    private void WidgetsCasting() {

        Cast_TextView = (TextView) findViewById(R.id.Info_Title);
        Cast__Back_Press = (ImageView) findViewById(R.id.Back_Press);
//        Cast_ViewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper);
        Cast_ViewPager = (ViewPager) findViewById(R.id.ViewPager);
        Cast_About = (TextView) findViewById(R.id.About);


//             ARRAY INITIALIZE
        arrayList = new ArrayList<>();


//        GET VALUE AND SET TITLE
        title = getIntent().getStringExtra("view");
        Cast_TextView.setText(title);


//        CLICK LISTENERS
        Cast__Back_Press.setOnClickListener(this);

    }

    private void FirebaseCasting() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        SportsInfoRef = mDatabase.child("Sports-Info");


    }

    private void RetrieveUserInfo() {

        SportsInfoRef.child(title).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    String about = dataSnapshot.child("about").getValue().toString();

//                    SET THE ABOUT SECTION OF SPORTS
                    Cast_About.setText(about);

                    for (DataSnapshot snapshot : dataSnapshot.child("images").getChildren()) {

                        arrayList.add(snapshot.getValue(String.class));


                    }

//                    SET ADAPTER
                    slideShowAdapter = new SlideShowAdapter(SportsInfoActivity.this, arrayList);
                    Cast_ViewPager.setAdapter(slideShowAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(SportsInfoActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    private void FlipperImages(String image) {
//        ImageView imageView = new ImageView(this);
//        imageView.setImageURI(Uri.parse(image));
//
//        Cast_ViewFlipper.addView(imageView);
//        Cast_ViewFlipper.setFlipInterval(4000);
//        Cast_ViewFlipper.setAutoStart(true);
//
//
//        Cast_ViewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
//        Cast_ViewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
//
//    }


}
