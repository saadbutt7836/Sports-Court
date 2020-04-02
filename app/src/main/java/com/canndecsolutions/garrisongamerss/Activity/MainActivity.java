package com.canndecsolutions.garrisongamerss.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Authentication.Login;
import com.canndecsolutions.garrisongamerss.Fragments.Home;
import com.canndecsolutions.garrisongamerss.Fragments.Booking;
import com.canndecsolutions.garrisongamerss.Fragments.PostUpload;
import com.canndecsolutions.garrisongamerss.Fragments.Settings;
import com.canndecsolutions.garrisongamerss.Fragments.Suggestion;
import com.canndecsolutions.garrisongamerss.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView Cast_Profile_Img;
    private ImageView Cast_Profile_btn;

    private BottomNavigationView Cast_BotmNavView;
    private FrameLayout Cast_Main_Frame;

    //    FRAGMENTS
    private Home Cast_Home_Frame;
    private Booking Cast_Offers_Frame;
    private PostUpload Cast_Post_Frame;
    private Suggestion Cast_Suggest_Frame;
    private Settings Cast_Profile_Frame;

    //    FIREBASE REFERENCES
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef;


    //    VARIABLES
    private String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide();

//        CASTINGS
        WidgetsCasting();

        FirebaseCasting();

//        GET USER INFO

        Cast_BotmNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.home:
                        SetFragment(Cast_Home_Frame);
                        return true;

                    case R.id.offers:
                        SetFragment(Cast_Offers_Frame);
                        return true;

                    case R.id.post:
                        SetFragment(Cast_Post_Frame);
                        return true;

                    case R.id.suggestions:
                        SetFragment(Cast_Suggest_Frame);
                        return true;

                    case R.id.profile:
                        SetFragment(Cast_Profile_Frame);
                        return true;

                    default:
                        return false;
                }


            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Profile_Btn:
                Intent intent = new Intent(this, UsersProfile.class);
                intent.putExtra("User_Id", userId);
                startActivity(intent);
                break;
        }
    }
    //    ===================================================== CALLING METHODS ============================================================

    private void WidgetsCasting() {


        Cast_Profile_Img = (CircleImageView) findViewById(R.id.Profile_Img);
        Cast_Profile_btn = (ImageView) findViewById(R.id.Profile_Btn);

        Cast_BotmNavView = (BottomNavigationView) findViewById(R.id.BotmNavView);
        Cast_Main_Frame = (FrameLayout) findViewById(R.id.Main_Frame);

        Cast_Home_Frame = new Home();
        Cast_Offers_Frame = new Booking();
        Cast_Post_Frame = new PostUpload();
        Cast_Suggest_Frame = new Suggestion();
        Cast_Profile_Frame = new Settings();

        SetFragment(Cast_Home_Frame);


        Cast_Profile_btn.setOnClickListener(this);
    }

    private void updateUI(FirebaseUser currentUser) {

        if (currentUser == null) {
            startActivity(new Intent(this, Login.class));

        } else {
            userId = mAuth.getCurrentUser().getUid();
            UserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChild("interests")) {

                        String profileImg = dataSnapshot.child("profile_img").getValue().toString();
                        Picasso.get().load(profileImg).into(Cast_Profile_Img);

                    } else {
                        Intent intent = new Intent(MainActivity.this, SportsInterest.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(intent);
                        System.out.println("Select your interest");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String error = databaseError.getMessage();
                    Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
//        else {
//            RetrieveUserInfo();
//            userId = mAuth.getCurrentUser().getUid();
//        }
    }

    private void SetFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.Main_Frame, fragment);
        fragmentTransaction.commit();

    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");


    }

//    private void RetrieveUserInfo() {
//        String currentUserId = mAuth.getCurrentUser().getUid();
//
//        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if () {
//
//                    String profileImg = dataSnapshot.child("profile_img").getValue().toString();
//                    Picasso.get().load(profileImg).into(Cast_Profile_Img);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                String error = databaseError.getMessage();
//                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


}
