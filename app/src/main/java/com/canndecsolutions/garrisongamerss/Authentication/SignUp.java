package com.canndecsolutions.garrisongamerss.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Activity.SportsInterest;
import com.canndecsolutions.garrisongamerss.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private EditText Cast_Full_Name, Cast_Email, Cast_Pass, Cast_Phone_No;
    private TextView Cast_Already_Acc;
    private Button Cast_SignUp_Btn;

    //    VARIABLES
    private String currentUserId = null,
            fullName = null,
            email = null,
            password = null,
            phoneNo = null;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


//        REMOVE HEADER
        getSupportActionBar().hide();


//        CALLING CASTING METHODS
        FirebaseCasting();

        CastingWidgets();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Already_Acc:
                startActivity(new Intent(this, Login.class));
                break;

            case R.id.SignUp_Btn:


                fullName = Cast_Full_Name.getText().toString().trim();
                email = Cast_Email.getText().toString().trim();
                password = Cast_Pass.getText().toString().trim();
                phoneNo = Cast_Phone_No.getText().toString().trim();

                if (!fullName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneNo.isEmpty()) {
                    if (isValidEmailId(Cast_Email.getText().toString().trim())) {
//                        done

                    } else {
                        Toast.makeText(this, "invalid email", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (password.length() < 5) {
                        Toast.makeText(this, "Password length must be greater than 5", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (phoneNo.length() > 13) {
                        Cast_Phone_No.setError("Phone no length exceeded");
                        Cast_Phone_No.requestFocus();
                        break;
                    }


                    UserRegistration(fullName, email, password, phoneNo);


                } else {
                    Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, SportsInterest.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);

                }


                break;
        }
    }


    //    ===================================================== CALLING METHODS ============================================================


    private void CastingWidgets() {


        Cast_Full_Name = (EditText) findViewById(R.id.Full_Name);
        Cast_Email = (EditText) findViewById(R.id.Email);
        Cast_Pass = (EditText) findViewById(R.id.Pass);
        Cast_Phone_No = (EditText) findViewById(R.id.Phone_No);
        Cast_Already_Acc = (TextView) findViewById(R.id.Already_Acc);
        Cast_SignUp_Btn = (Button) findViewById(R.id.SignUp_Btn);


        //  CLICK LISTENERS
        Cast_Already_Acc.setOnClickListener(this);
        Cast_SignUp_Btn.setOnClickListener(this);

    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");
    }

    private boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private void UserRegistration(final String fullName, final String email, final String password, final String phoneNo) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SavedUserData(fullName, email, password, phoneNo);

                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(SignUp.this, "Error: " + error, Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }

    private void SavedUserData(String fullName, String email, String pass, String phoneNo) {

        String defaultImg = "https://firebasestorage.googleapis.com/v0/b/garrison-gamerss.appspot.com/o/defaultimg.png?alt=media&token=1c0da905-41d1-4c02-b841-0a7f5e6848fa";
        currentUserId = mAuth.getCurrentUser().getUid();


        Map<String, String> UserDataMap = new HashMap<>();
        UserDataMap.put("uid", currentUserId);
        UserDataMap.put("name", fullName);
        UserDataMap.put("email", email);
        UserDataMap.put("profile_img", defaultImg);
        UserDataMap.put("password", pass);
        UserDataMap.put("telephone", phoneNo);





        UserRef.child(currentUserId).setValue(UserDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUp.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, SportsInterest.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                } else {

                    String error = task.getException().toString();
                    Toast.makeText(SignUp.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
