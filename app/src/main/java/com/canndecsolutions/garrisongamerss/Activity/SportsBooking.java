package com.canndecsolutions.garrisongamerss.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.canndecsolutions.garrisongamerss.Adapters.SportsSlideShow;
import com.canndecsolutions.garrisongamerss.Dialog.BookingComment;
import com.canndecsolutions.garrisongamerss.Dialog.BookingConfirmation;
import com.canndecsolutions.garrisongamerss.Models.Users;
import com.canndecsolutions.garrisongamerss.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SportsBooking extends AppCompatActivity implements View.OnClickListener, BookingComment.AddCommentListener, BookingConfirmation.BookingConfirmationListener {

    //WIDGETS
    private TextView Cast_TextView, Cast_About;
    private ImageView Cast__Back_Press;
    private ViewFlipper Cast_ViewFlipper;
    private ViewPager Cast_ViewPager;
    private Button Cast_Add_Com_Btn, Cast_Booking_Btn;

    //LISTs
    private ArrayList<String> arrayList;
    private SportsSlideShow sportsSlideShow;

    //    DATABASE REFERENCES
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UsersRef, BookingRef, SportsInfoRef;


    //    STRINGS
    private String userId = "",
            sportsMsg = "",
            title = "";

    //    BOOLEAN
    private Boolean confirmation = false;

    //    MODEL CLASS
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_booking);

        getSupportActionBar().hide();


//        CASTINGS
        WidgetsCasting();

        FirebaseCasting();

        RetrieveSportsInfo();

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
            case R.id.Add_Com_Btn:
                BookingComment();
                break;
            case R.id.Booking_Btn:

                BookingConfirmation bookingConfirmation = new BookingConfirmation();
                bookingConfirmation.show(getSupportFragmentManager(), "Booking Confirmation");

                break;
        }
    }

    @Override
    public void SendText(String message) {
        sportsMsg = message;
    }


    @Override
    public void ConfirmClick(Boolean confirm) {
        confirmation = confirm;

        if (confirmation == true) {
            BookingHandle();
            confirmation = false;
        }
    }

    //    ===================================================== CALLING METHODS ============================================================

    private void WidgetsCasting() {

        Cast_TextView = (TextView) findViewById(R.id.Info_Title);
        Cast__Back_Press = (ImageView) findViewById(R.id.Back_Press);
//        Cast_ViewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper);
        Cast_ViewPager = (ViewPager) findViewById(R.id.ViewPager);
        Cast_About = (TextView) findViewById(R.id.About);
        Cast_Add_Com_Btn = (Button) findViewById(R.id.Add_Com_Btn);
        Cast_Booking_Btn = (Button) findViewById(R.id.Booking_Btn);


//             ARRAY INITIALIZE
        arrayList = new ArrayList<>();


//        GET VALUE AND SET TITLE
        title = getIntent().getStringExtra("view");
        Cast_TextView.setText(title);


//        CLICK LISTENERS
        Cast__Back_Press.setOnClickListener(this);
        Cast_Add_Com_Btn.setOnClickListener(this);
        Cast_Booking_Btn.setOnClickListener(this);
    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        SportsInfoRef = mDatabase.child("Sports-Info");
        UsersRef = mDatabase.child("Users");
        BookingRef = mDatabase.child("Booking-Details");


    }

    private void BookingHandle() {

        HashMap<String, Object> BookMap = new HashMap<>();
        BookMap.put("name", users.getName());
        BookMap.put("email", users.getEmail());
        BookMap.put("telephone", users.getTelephone());
        BookMap.put("booked_by", userId);
        BookMap.put("category", title);
        BookMap.put("message", sportsMsg);

        String bookingKey = mDatabase.push().getKey();
        BookingRef.child(bookingKey).updateChildren(BookMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SportsBooking.this, "Booking Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(SportsBooking.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void BookingComment() {
        BookingComment bookingComment = new BookingComment();
        bookingComment.show(getSupportFragmentManager(), "Add Comment");

    }

    private void RetrieveUserInfo() {
        UsersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    users = dataSnapshot.getValue(Users.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(SportsBooking.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RetrieveSportsInfo() {

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
                    sportsSlideShow = new SportsSlideShow(SportsBooking.this, arrayList);
                    Cast_ViewPager.setAdapter(sportsSlideShow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(SportsBooking.this, "Error: " + error, Toast.LENGTH_SHORT).show();
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
