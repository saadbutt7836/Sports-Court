package com.canndecsolutions.garrisongamerss.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Fragments.HomeFragment;
import com.canndecsolutions.garrisongamerss.Models.PostsModelClass;
import com.canndecsolutions.garrisongamerss.R;
import com.canndecsolutions.garrisongamerss.Sheets.FullScreenBottomSheet;
import com.canndecsolutions.garrisongamerss.Utility.Utility;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UsersProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView Cast_Back_Press;

    //    FIREBASE REFERENCES
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, PostRef;
    private StorageReference mStorage, UserStorage, ProfileStorage;


    //    FIREBASE ADAPTER
    private FirebaseRecyclerAdapter<PostsModelClass, PostsViewHolder> adapter;
    private FirebaseRecyclerOptions<PostsModelClass> options;


    //    VARIABLES
    private String userId = null,
            postUserId = null,
            currName = null,
            currEmail = null,
            currPhoneNo = null,
            currProfImg = null;


    //    URIS
    private Uri uri = null;


    //    CASTINGS
    private ImageView Cast_profile_image;
    private TextView Cast_Name, Cast_Email, Cast_Phone_No;
    private RecyclerView Cast_RecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        getSupportActionBar().hide();

        WidgetCasting();

        FirebaseCasting();

        RetrieveUserData();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Back_Press:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        FIREBASE ADAPTER
        FirebaseAdapter();
    }

    //    ===================================================== CALLING METHODS ============================================================

    private void WidgetCasting() {
        Cast_Back_Press = (ImageView) findViewById(R.id.Back_Press);
        Cast_profile_image = (ImageView) findViewById(R.id.profile_image);
        Cast_Name = (TextView) findViewById(R.id.Name);
        Cast_Email = (TextView) findViewById(R.id.Email);
        Cast_Phone_No = (TextView) findViewById(R.id.Phone_No);
        Cast_RecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);


//       GET INTENT STRING
        postUserId = getIntent().getStringExtra("User_Id");


//        CLICK LISTENERS
        Cast_Back_Press.setOnClickListener(this);

    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");
        PostRef = mDatabase.child("Posts");

        mStorage = FirebaseStorage.getInstance().getReference();

        UserStorage = mStorage.child("Users");
        ProfileStorage = UserStorage.child("Profiles/");


    }

    private void RetrieveUserData() {

        userId = mAuth.getCurrentUser().getUid();

        UserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("email")) {


                        currEmail = dataSnapshot.child("email").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("name")) {

                        currName = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("telephone")) {

                        currPhoneNo = dataSnapshot.child("telephone").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("profile_img")) {

                        currProfImg = dataSnapshot.child("profile_img").getValue().toString();
                    }


                    Cast_Name.setText(currName);
                    Cast_Email.setText(currEmail);
                    Cast_Phone_No.setText(currPhoneNo);
                    Picasso.get().load(currProfImg).into(Cast_profile_image);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(UsersProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void FirebaseAdapter() {
        Query query = PostRef.orderByChild("posted_by").equalTo(postUserId);
        options = new FirebaseRecyclerOptions.Builder<PostsModelClass>().setQuery(query, PostsModelClass.class).build();
        adapter = new FirebaseRecyclerAdapter<PostsModelClass, PostsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, final int position, @NonNull final PostsModelClass model) {


                if (model.stars.containsKey(userId)) {
                    // Unstar the post and remove self from stars

                    holder.Cast_Unlike_Img.setImageResource(R.drawable.star);

                }

//                ==================================== CLICK LISTENERS ================================

                final String pId = model.getPid();

//                STAR TRANSACTIONS HANDLE
                holder.Cast_Like_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        PostRef.child(pId).runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                PostsModelClass p = mutableData.getValue(PostsModelClass.class);
                                if (p == null) {
                                    return Transaction.success(mutableData);
                                }

                                if (p.stars.containsKey(userId)) {
                                    // Unstar the post and remove self from stars
                                    p.starCount = p.starCount - 1;
                                    p.stars.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    holder.Cast_Unlike_Img.setImageResource(R.drawable.star);

                                } else {
                                    // Star the post and add self to stars
                                    p.starCount = p.starCount + 1;
                                    p.stars.put(userId, true);

                                    holder.Cast_Unlike_Img.setImageResource(R.drawable.unstar);


                                }

                                // Set value and report transaction success
                                mutableData.setValue(p);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                                // Transaction completed
                                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                            }
                        });
                    }
                });


//                GET POST OWNER KEY AND RETRIEVE INFORMATION
                GetUserKeyAndShowInfo(model.getPosted_by(), holder);

//                SETS THE VALUES OF POSTS
                ShowPostData(model, holder);

                holder.Cast_Comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle args = new Bundle();
                        args.putString("postId", model.getPid());

                        FullScreenBottomSheet bottomSheet = new FullScreenBottomSheet();
                        bottomSheet.setArguments(args);
                        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());


                    }
                });

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);

                PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        Cast_RecyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        Cast_RecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void GetUserKeyAndShowInfo(String postedBy, final PostsViewHolder holder) {

        Query query = UserRef.orderByChild("uid").equalTo(postedBy);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userKey = snapshot.getKey();
                    SetValueOfPostedUser(userKey, holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(UsersProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void SetValueOfPostedUser(String key, final PostsViewHolder holder) {
        UserRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String fullName = dataSnapshot.child("name").getValue().toString();
                String profileImg = dataSnapshot.child("profile_img").getValue().toString();


                holder.Cast_Full_Name.setText(fullName);
                Picasso.get().load(profileImg).into(holder.Cast_Profile_Img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(UsersProfileActivity.this, "error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ShowPostData(PostsModelClass model, PostsViewHolder holder) {
        int type = model.getType();


//                CHECKING WHICH TYPE OF POST COMING
        if (type == 0) {

            holder.Cast_Post_Text.setText(model.getStatus());

        } else if (type == 1) {

            holder.Cast_Post_Text.setVisibility(View.GONE);
            Picasso.get().load(model.getPost_image()).into(holder.Cast_Post_Img);

        } else if (type == 2) {

            holder.Cast_Post_Text.setText(model.getStatus());
            Picasso.get().load(model.getPost_image()).into(holder.Cast_Post_Img);
        }

        holder.Cast_Post_TimeStamp.setText(Utility.TimeStampHandle((long) model.getTimestamp()));
    }


//    ======================================================== ViewHolder ======================================================

    public static class PostsViewHolder extends RecyclerView.ViewHolder {

        private TextView Cast_Full_Name, Cast_Post_Text, Cast_Post_TimeStamp;
        private ImageView Cast_Post_Img, Cast_Profile_Img, Cast_Comments, Cast_Unlike_Img;
        private LinearLayout Cast_Like_Btn;


        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            View view = itemView;
            Cast_Full_Name = (TextView) view.findViewById(R.id.Full_Name);
            Cast_Post_Text = (TextView) view.findViewById(R.id.Post_Text);
            Cast_Post_TimeStamp = (TextView) view.findViewById(R.id.Post_TimeStamp);
            Cast_Profile_Img = (ImageView) view.findViewById(R.id.Profile_Img);
            Cast_Post_Img = (ImageView) view.findViewById(R.id.Post_Img);
            Cast_Comments = (ImageView) view.findViewById(R.id.Comments);
            Cast_Like_Btn = (LinearLayout) view.findViewById(R.id.Like_Btn);
            Cast_Unlike_Img = (ImageView) view.findViewById(R.id.Unlike_Img);

        }
    }


}
