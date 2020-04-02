package com.canndecsolutions.garrisongamerss.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Models.Posts;
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

public class UsersProfile extends AppCompatActivity implements View.OnClickListener {

    private ImageView Cast_Back_Press;

    //    FIREBASE REFERENCES
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, PostRef;
    private StorageReference mStorage, UserStorage, ProfileStorage;


    //    FIREBASE ADAPTER
    private FirebaseRecyclerAdapter<Posts, PostsViewHolder> adapter;
    private FirebaseRecyclerOptions<Posts> options;


    //    VARIABLES
    private String currentUserId = null,
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

        currentUserId = mAuth.getCurrentUser().getUid();

        UserRef.child(postUserId).addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(UsersProfile.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void FirebaseAdapter() {
        Query query = PostRef.orderByChild("posted_by").equalTo(postUserId);
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, final int position, @NonNull final Posts model) {


                if (model.stars.containsKey(currentUserId)) {
                    // Unstar the post and remove self from stars

                    holder.Cast_Unlike_Img.setImageResource(R.drawable.star);

                }

                //                ====================================   SETS THE VALUES OF POSTS  ========================================
                int type = model.getType();

                holder.Cast_Post_TimeStamp.setText(Utility.TimeStampHandle(model.getTimestamp()));


//                CHECKING WHICH TYPE OF POST COMING
                if (type == 0) {
//                    Only Text Messages
                    holder.Cast_Post_Text.setText(model.getStatus());
                    Picasso.get().load((Uri) null).into(holder.Cast_Post_Img);
                } else if (type == 1) {
//                    Only Images
                    Picasso.get().load(model.getPost_image()).into(holder.Cast_Post_Img);
                    holder.Cast_Post_Text.setVisibility(View.GONE);
                } else if (type == 2) {
//                    Both Text and Images
                    holder.Cast_Post_Text.setText(model.getStatus());
                    Picasso.get().load(model.getPost_image()).into(holder.Cast_Post_Img);
                }


//                GET POST OWNER KEY AND RETRIEVE INFORMATION
                SetValueOfPostedUser(model.getPosted_by(), holder);


//                ==================================== CLICK LISTENERS ================================

                final String pId = model.getPid();

//                STAR TRANSACTIONS HANDLE
                holder.Cast_Like_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        OnStarClicked(PostRef.child(pId), pId, holder);

                        if (model.stars.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            // Unstar the post and remove self from stars

                            holder.Cast_Unlike_Img.setImageResource(R.drawable.star);

                        } else {
//                            // Star the post and add self to stars
                            holder.Cast_Unlike_Img.setImageResource(R.drawable.unstar);

                        }
                    }
                });


//                POSTS COMMENTS HANDLE
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


    private void OnStarClicked(DatabaseReference PostRef, String pId, final PostsViewHolder holder) {
        PostRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Posts p = mutableData.getValue(Posts.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(currentUserId)) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());


                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(currentUserId, true);


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
                Toast.makeText(UsersProfile.this, "error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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
