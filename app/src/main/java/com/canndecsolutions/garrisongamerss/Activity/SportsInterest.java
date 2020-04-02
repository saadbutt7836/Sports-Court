package com.canndecsolutions.garrisongamerss.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Models.SportsUrls;
import com.canndecsolutions.garrisongamerss.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class SportsInterest extends AppCompatActivity implements View.OnClickListener {

    //    CASTINGS
    private CheckBox Cast_Cri_Checkb, Cast_FtBall_Checkb, Cast_Tennis_Checkb, Cast_TbTennis_Checkb, Cast_Badminton_Checkb, Cast_Squash_Checkb;
    private ImageView Cast_Cricket_Img, Cast_Football_Img, Cast_Tennis_Img, Cast_TableTennis_Img, Cast_Badminton_Img, Cast_Squash_Img;
    private ImageView Cast_Done_Interest;


    //    LIST
    private ArrayList<String> interestList;

    //    FIREBASE DATABASE
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_interest);

        getSupportActionBar().hide();

        interestList = new ArrayList<>();

        FirebaseCasting();


//        CASTINGS
        CastingWidgets();

//        SPORTS IMAGES SET
        SetImages();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.Done_Interest:
                if (!interestList.isEmpty()) {
                    System.out.println(" " + interestList);
                    Toast.makeText(this, "selectiion", Toast.LENGTH_SHORT).show();

                    SportsInterest(interestList);
                } else {
                    Toast.makeText(this, "Select Atleast 1", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Cri_Checkb:
                if (Cast_Cri_Checkb.isChecked()) {
                    interestList.add("Cricket");
                    Toast.makeText(this, "Cricket", Toast.LENGTH_SHORT).show();
                } else {
                    interestList.remove("Cricket");
                }
                break;
            case R.id.FtBall_Checkb:
                if (Cast_FtBall_Checkb.isChecked()) {
                    interestList.add("Football");
                    Toast.makeText(this, "Football", Toast.LENGTH_SHORT).show();
                } else {
                    interestList.remove("Football");
                }
                break;
            case R.id.Tennis_Checkb:
                if (Cast_Tennis_Checkb.isChecked()) {
                    interestList.add("Tennis");
                    Toast.makeText(this, "Tennis", Toast.LENGTH_SHORT).show();
                } else {
                    interestList.remove("Tennis");
                }
                break;
            case R.id.TbTennis_Checkb:
                if (Cast_TbTennis_Checkb.isChecked()) {
                    interestList.add("Table Tennis");
                    Toast.makeText(this, "Table Tennis", Toast.LENGTH_SHORT).show();
                } else {
                    interestList.remove("Table Tennis");
                }
                break;
            case R.id.Badminton_Checkb:
                if (Cast_Badminton_Checkb.isChecked()) {
                    interestList.add("Badminton");
                    Toast.makeText(this, "Badminton", Toast.LENGTH_SHORT).show();
                } else {
                    interestList.remove("Badminton");
                }
                break;
            case R.id.Squash_Checkb:
                if (Cast_Squash_Checkb.isChecked()) {
                    interestList.add("Squash");
                    Toast.makeText(this, "Squash", Toast.LENGTH_SHORT).show();
                } else {
                    interestList.remove("Squash");
                }
                break;
        }
    }


    //    ===================================================== CALLING METHODS ============================================================

    private void CastingWidgets() {
        Cast_Cri_Checkb = (CheckBox) findViewById(R.id.Cri_Checkb);
        Cast_FtBall_Checkb = (CheckBox) findViewById(R.id.FtBall_Checkb);
        Cast_Tennis_Checkb = (CheckBox) findViewById(R.id.Tennis_Checkb);
        Cast_TbTennis_Checkb = (CheckBox) findViewById(R.id.TbTennis_Checkb);
        Cast_Badminton_Checkb = (CheckBox) findViewById(R.id.Badminton_Checkb);
        Cast_Squash_Checkb = (CheckBox) findViewById(R.id.Squash_Checkb);

        Cast_Cricket_Img = (ImageView) findViewById(R.id.Cricket_Img);
        Cast_Football_Img = (ImageView) findViewById(R.id.Football_Img);
        Cast_Tennis_Img = (ImageView) findViewById(R.id.Tennis_Img);
        Cast_TableTennis_Img = (ImageView) findViewById(R.id.TableTennis_Img);
        Cast_Badminton_Img = (ImageView) findViewById(R.id.Badminton_Img);
        Cast_Squash_Img = (ImageView) findViewById(R.id.Squash_Img);
        Cast_Done_Interest = (ImageView) findViewById(R.id.Done_Interest);


//        CLICK LISTENERS
        Cast_Done_Interest.setOnClickListener(this);

        Cast_Cri_Checkb.setOnClickListener(this);
        Cast_FtBall_Checkb.setOnClickListener(this);
        Cast_Tennis_Checkb.setOnClickListener(this);
        Cast_TbTennis_Checkb.setOnClickListener(this);
        Cast_Badminton_Checkb.setOnClickListener(this);
        Cast_Squash_Checkb.setOnClickListener(this);
    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");
    }

    private void SetImages() {

        Picasso.get().load(SportsUrls.CRICKET).into(Cast_Cricket_Img);
        Picasso.get().load(SportsUrls.FOOTBALL).into(Cast_Football_Img);
        Picasso.get().load(SportsUrls.TENNIS).into(Cast_Tennis_Img);
        Picasso.get().load(SportsUrls.TABLE_TENNIS).into(Cast_TableTennis_Img);
        Picasso.get().load(SportsUrls.BADMINTON).into(Cast_Badminton_Img);
        Picasso.get().load(SportsUrls.SQUASH).into(Cast_Squash_Img);

    }

    private void SportsInterest(ArrayList<String> interestList) {
        HashMap<String, Object> InterestMap = new HashMap<>();
        InterestMap.put("interests", interestList);

        UserRef.child(mAuth.getCurrentUser().getUid()).updateChildren(InterestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SportsInterest.this, "Interest Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SportsInterest.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SportsInterest.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
