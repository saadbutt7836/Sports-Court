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

import com.canndecsolutions.garrisongamerss.Activity.MainActivity;
import com.canndecsolutions.garrisongamerss.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText Cast_Email, Cast_Pass;
    private TextView Cast_Forgot_Pass, Cast_Create_acc;
    private Button Cast_Login_Btn;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //        CALLING CASTING METHODS

        mAuth = FirebaseAuth.getInstance();

        WidgetsCasting();

        Cast_Forgot_Pass.setOnClickListener(this);
        Cast_Create_acc.setOnClickListener(this);
        Cast_Login_Btn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Create_acc:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;

            case R.id.Forgot_Pass:
                startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
                break;

            case R.id.Login_Btn:
                String email = Cast_Email.getText().toString().trim();
                String pass = Cast_Pass.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show();
                } else {

                    UserLogin(email, pass);
                }
                break;
        }
    }

    //    ===================================================== CALLING METHODS ============================================================

    private void WidgetsCasting() {

        getSupportActionBar().hide();

        Cast_Email = (EditText) findViewById(R.id.Email);
        Cast_Pass = (EditText) findViewById(R.id.Pass);
        Cast_Forgot_Pass = (TextView) findViewById(R.id.Forgot_Pass);
        Cast_Create_acc = (TextView) findViewById(R.id.Create_acc);
        Cast_Login_Btn = (Button) findViewById(R.id.Login_Btn);
    }


    private void UserLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "SignIn Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


}
