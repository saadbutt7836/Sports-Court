package com.canndecsolutions.garrisongamerss.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity implements View.OnClickListener {

    private EditText Cast_Email;
    private Button Cast_Forgot_Pass_Done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);


//        Casting Widgets
        WidgetCasting();

        Cast_Forgot_Pass_Done.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Forgot_Pass_Done:

                String email = Cast_Email.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show();
                } else {
                    ForgotPassword(email);
                    Toast.makeText(this, "oye: " + email, Toast.LENGTH_SHORT).show();
                }

        }
    }

    private void ForgotPassword(String email) {

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPass.this, "Please Check Your Email", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(ForgotPass.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //    ===================================================== CALLING METHODS ============================================================

    private void WidgetCasting() {
        Cast_Email = (EditText) findViewById(R.id.Email);
        Cast_Forgot_Pass_Done = (Button) findViewById(R.id.Forgot_Pass_Done);
    }


}
