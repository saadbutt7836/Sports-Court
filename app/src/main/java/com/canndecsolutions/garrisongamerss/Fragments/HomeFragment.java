package com.canndecsolutions.garrisongamerss.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Sheets.FullScreenBottomSheet;
import com.canndecsolutions.garrisongamerss.Models.PostsModelClass;
import com.canndecsolutions.garrisongamerss.R;
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
import com.squareup.picasso.Picasso;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, PostRef;

    private FirebaseRecyclerAdapter<PostsModelClass, PostsViewHolder> adapter;
    private FirebaseRecyclerOptions<PostsModelClass> options;

    private RecyclerView Cast_RecyclerView;


    private String userId = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseCasting();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CastingWidgets(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);

//        FIREBASE ADAPTER
        FirebaseAdapter();
    }


//    ===================================================== CALLING METHODS ============================================================


    private void CastingWidgets(View view) {

        Cast_RecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);

        Cast_RecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        Cast_RecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");
        PostRef = mDatabase.child("Posts");

        userId = mAuth.getCurrentUser().getUid();

    }

    private void FirebaseAdapter() {

        options = new FirebaseRecyclerOptions.Builder<PostsModelClass>().setQuery(PostRef, PostsModelClass.class).build();
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


//                POSTS COMMENTS HANDLE
                holder.Cast_Comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle args = new Bundle();
                        args.putString("postId", model.getPid());

                        FullScreenBottomSheet bottomSheet = new FullScreenBottomSheet();
                        bottomSheet.setArguments(args);
                        bottomSheet.show(getFragmentManager(), bottomSheet.getTag());
                    }
                });


//                GET POST OWNER KEY AND RETRIEVE INFORMATION
                GetUserKeyAndShowInfo(model.getPosted_by(), holder);


                //                ====================================   SETS THE VALUES OF POSTS  ========================================

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
                Toast.makeText(getActivity(), "error: " + error, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


//    private void onStarClicked(DatabaseReference postRef, final PostsViewHolder holder) {
//        postRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                PostsModelClass p = mutableData.getValue(PostsModelClass.class);
//                if (p == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                if (p.stars.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                    // Unstar the post and remove self from stars
//                    p.starCount = p.starCount - 1;
//                    p.stars.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                    holder.Cast_Unlike_Img.setImageResource(R.drawable.ic_send_button);
//
//                } else {
//                    // Star the post and add self to stars
//                    p.starCount = p.starCount + 1;
//                    p.stars.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
//
//                    holder.Cast_Unlike_Img.setImageResource(R.drawable.unstar);
//
//
//                }
//
//                // Set value and report transaction success
//                mutableData.setValue(p);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b,
//                                   DataSnapshot dataSnapshot) {
//                // Transaction completed
//                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
//            }
//        });
//
//    }


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
