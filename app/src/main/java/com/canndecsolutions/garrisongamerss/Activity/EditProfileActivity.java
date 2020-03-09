package com.canndecsolutions.garrisongamerss.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView Cast_Back_Press;
    private Button Cast_Update_Btn;
    private EditText Cast_FullName, Cast_Phone_No;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef;

    private String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().hide();

        WidgetsCastings();

        FirebaseCasting();

        RetrieveUserData();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Back_Press:
                onBackPressed();
                break;
            case R.id.Update_Btn:
                String updateName = Cast_FullName.getText().toString().trim();
                String updatePhoneNo = Cast_Phone_No.getText().toString().trim();

                if (!updateName.isEmpty() && !updatePhoneNo.isEmpty()) {

                    UpdateUserData(updateName, updatePhoneNo);
                } else {
                    Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    //        ===================================================== CALLING METHODS ============================================================
    private void WidgetsCastings() {
        Cast_Back_Press = (ImageView) findViewById(R.id.Back_Press);
        Cast_FullName = (EditText) findViewById(R.id.Full_Name);
        Cast_Phone_No = (EditText) findViewById(R.id.Phone_No);
        Cast_Update_Btn = (Button) findViewById(R.id.Update_Btn);

        Cast_Back_Press.setOnClickListener(this);
        Cast_Update_Btn.setOnClickListener(this);

    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");

    }

    private void RetrieveUserData() {

        userId = mAuth.getCurrentUser().getUid();

        UserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = null, phoneNo = null;

                    if (dataSnapshot.hasChild("name")) {

                        fullName = dataSnapshot.child("name").getValue().toString();

                    }
                    if (dataSnapshot.hasChild("telephone")) {

                        phoneNo = dataSnapshot.child("telephone").getValue().toString();
                    }


//                        SET USER DATA TO EDIT TEXT

                    Cast_FullName.setText(fullName);
                    Cast_Phone_No.setText(phoneNo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(EditProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void UpdateUserData(String name, String phone) {
        Map<String, Object> UpdateMap = new HashMap<>();

        UpdateMap.put("name", name);
        UpdateMap.put("telephone", phone);

        UserRef.child(userId).updateChildren(UpdateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

//                    BACK TO USER PROFILE
                    onBackPressed();
                } else {
                    String error = task.getException().toString();
                    Toast.makeText(EditProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
