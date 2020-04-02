package com.canndecsolutions.garrisongamerss.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Suggestion extends Fragment implements View.OnClickListener {

    private EditText Cast_Suggest;
    private Button Cast_Send_Btn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, SuggestRef;

    private String userId = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseCasting();
//        RetrieveUserData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggestion, container, false);


//        CASTINGS
        WdigetsCasting(view);

//        CLICK LISTENER
        Cast_Send_Btn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Send_Btn:

                String suggest = Cast_Suggest.getText().toString().trim();
                if (suggest.isEmpty() || suggest.equals(" ")) {

                    Toast.makeText(getActivity(), "field must be filled", Toast.LENGTH_SHORT).show();
                } else {
                    SuggestionsSend(userId, suggest);
                }
                break;
        }
    }

    //    ===================================================== CALLING METHODS ============================================================


    private void WdigetsCasting(View view) {
        Cast_Suggest = (EditText) view.findViewById(R.id.Suggest);
        Cast_Send_Btn = (Button) view.findViewById(R.id.Send_Btn);

        Cast_Suggest.setText("");

    }

    private void SuggestionsSend(String suggestedBy, String suggest) {


        String key = mDatabase.push().getKey();
        Map<String, String> SuggestMap = new HashMap<>();

        SuggestMap.put("sid", key);
        SuggestMap.put("suggested_by", suggestedBy);
        SuggestMap.put("suggestion", suggest);


        SuggestRef.child(key).setValue(SuggestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Cast_Suggest.setText("");
                Toast.makeText(getActivity(), "Suggestion send successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");

        SuggestRef = mDatabase.child("Suggestions");

        userId = mAuth.getCurrentUser().getUid();
    }

//    private void RetrieveUserData() {
//
//        final String currentUserId = mAuth.getCurrentUser().getUid();
//
//        Query query = UserRef.orderByChild("uid").equalTo(currentUserId);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        currName = snapshot.child("email").getValue().toString();
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
//
//    }
}
