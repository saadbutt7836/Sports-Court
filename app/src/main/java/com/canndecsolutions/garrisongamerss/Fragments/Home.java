package com.canndecsolutions.garrisongamerss.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canndecsolutions.garrisongamerss.Activity.UsersProfile;
import com.canndecsolutions.garrisongamerss.Models.Posts;
import com.canndecsolutions.garrisongamerss.R;
import com.canndecsolutions.garrisongamerss.Sheets.FullScreenBottomSheet;
import com.canndecsolutions.garrisongamerss.Utility.Utility;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class Home extends Fragment {

    private RecyclerView Cast_Recycler_View;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, InternetConnRef, PostRef;


    private FirebaseRecyclerAdapter<Posts, PostsViewHolder> adapter;
    private FirebaseRecyclerOptions<Posts> options;

    private ArrayList<String> userIntersts;


    //===============================


//    FirebaseRecyclerPagingAdapter<PostsModelClass, PostsViewHolder> mAdapter;
//    private SwipeRefreshLayout mSwipeRefreshLayout;
    //    ================================


    private String userId = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userIntersts = new ArrayList<>();

        FirebaseCasting();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CastingWidgets(view);

//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mAdapter.refresh();
//            }
//        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            userId = mAuth.getCurrentUser().getUid();

            RetrieveUserInterstsArray();

        }

//        FIREBASE ADAPTER
        FirebaseAdapter();


    }

    //Stop Listening Adapter
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
//        mAdapter.stopListening();
    }


//    ===================================================== CALLING METHODS ============================================================


    private void CastingWidgets(View view) {
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        Cast_Recycler_View = (RecyclerView) view.findViewById(R.id.Recycler_View);

        Cast_Recycler_View.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        Cast_Recycler_View.setLayoutManager(linearLayoutManager);

//        FirebasePagingAdapter();

    }


    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");
        PostRef = mDatabase.child("Posts");


//        InternetConnRef.onDisconnect().setValue("Internet Connection Problem");

    }
//=========================================

//    private void FirebasePagingAdapter() {
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance(1)
//                .setPageSize(10)
//                .build();
//
//
//        DatabasePagingOptions<PostsModelClass> moptions = new DatabasePagingOptions.Builder<PostsModelClass>()
//                .setLifecycleOwner(this)
//                .setQuery(PostRef, config, PostsModelClass.class)
//                .build();
//
//        mAdapter = new FirebaseRecyclerPagingAdapter<PostsModelClass, PostsViewHolder>(moptions) {
//
//            @Override
//            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, int i, @NonNull final PostsModelClass model) {
//
//                if (model.stars.containsKey(userId)) {
//                    // Unstar the post and remove self from stars
//
//                    holder.Cast_Unlike_Img.setImageResource(R.drawable.star);
//                }
//
//
//                //                ====================================   SETS THE VALUES OF POSTS  ========================================
//                int type = model.getType();
//
//                holder.Cast_Post_TimeStamp.setText(Utility.TimeStampHandle((long) model.getTimestamp()));
//
//
////                CHECKING WHICH TYPE OF POST COMING
//                if (type == 0) {
////                    Only Text Messages
//                    holder.Cast_Post_Text.setText(model.getStatus());
//                    Picasso.get().load((Uri) null).into(holder.Cast_Post_Img);
//                } else if (type == 1) {
////                    Only Images
//                    Picasso.get().load(model.getPost_image()).into(holder.Cast_Post_Img);
//                    holder.Cast_Post_Text.setVisibility(View.GONE);
//                } else if (type == 2) {
////                    Both Text and Images
//                    holder.Cast_Post_Text.setText(model.getStatus());
//                    Picasso.get().load(model.getPost_image()).into(holder.Cast_Post_Img);
//                }
//
//
////                GET POST OWNER KEY AND RETRIEVE INFORMATION
//                SetValueOfPostedUser(model.getPosted_by(), holder);
//
//
////                ==================================== CLICK LISTENERS ================================
//
//                final String pId = model.getPid();
//
////                STAR TRANSACTIONS HANDLE
//                holder.Cast_Like_Btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        OnStarClicked(PostRef.child(pId), pId, holder);
//
//                        if (model.stars.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                            // Unstar the post and remove self from stars
//
//                            holder.Cast_Unlike_Img.setImageResource(R.drawable.star);
//
//                        } else {
////                            // Star the post and add self to stars
//                            holder.Cast_Unlike_Img.setImageResource(R.drawable.unstar);
//
//                        }
//
//                    }
//                });
//
//
////                POSTS COMMENTS HANDLE
//                holder.Cast_Comments.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle args = new Bundle();
//                        args.putString("postId", model.getPid());
//
//                        FullScreenBottomSheet bottomSheet = new FullScreenBottomSheet();
//                        bottomSheet.setArguments(args);
//                        bottomSheet.show(getFragmentManager(), bottomSheet.getTag());
//                    }
//                });
//
////                USERS PROFILE CLICK HANDLING
//                holder.Cast_Users_profile.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(getActivity(), UsersProfile.class);
//                        intent.putExtra("User_Id", model.getPosted_by());
//
//                        startActivity(intent);
//                    }
//                });
//
//            }
//
//            @Override
//            protected void onLoadingStateChanged(@NonNull LoadingState state) {
//                switch (state) {
//                    case LOADING_INITIAL:
//                    case LOADING_MORE:
//                        // Do your loading animation
//                        mSwipeRefreshLayout.setRefreshing(true);
//                        break;
//
//                    case LOADED:
//                        // Stop Animation
//                        mSwipeRefreshLayout.setRefreshing(false);
//                        break;
//
//                    case FINISHED:
//                        //Reached end of Data set
//                        mSwipeRefreshLayout.setRefreshing(false);
//                        break;
//
//                    case ERROR:
//                        retry();
//                        break;
//                }
//
//
//            }
//
//            @NonNull
//            @Override
//            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
//
//                PostsViewHolder viewHolder = new PostsViewHolder(view);
//
//                return viewHolder;
//            }
//
//
//            @Override
//            protected void onError(@NonNull DatabaseError databaseError) {
//                mSwipeRefreshLayout.setRefreshing(false);
//                databaseError.toException().printStackTrace();
//                // Handle Error
//                retry();
//            }
//
//
//        };
//
//        Cast_Recycler_View.setAdapter(mAdapter);
//        mAdapter.startListening();
//    }

// ================================================

    private void RetrieveUserInterstsArray() {
        UserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("interests")) {
                    for (DataSnapshot snapshot : dataSnapshot.child("interests").getChildren()) {
                        userIntersts.add(snapshot.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void FirebaseAdapter() {

        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(PostRef, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, final int position, @NonNull final Posts model) {

//                if (userIntersts.contains(model.getCategory())) {
                System.out.println(userIntersts + " " + model.getCategory());


                if (model.stars.containsKey(userId)) {
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
                        bottomSheet.show(getFragmentManager(), bottomSheet.getTag());


                    }
                });

//                USERS PROFILE CLICK HANDLING
                holder.Cast_Users_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UsersProfile.class);
                        intent.putExtra("User_Id", model.getPosted_by());

                        startActivity(intent);
                    }
                });


//                } else {
//                    System.out.println("Out of your interests");

//                }
            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);

                PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        Cast_Recycler_View.setAdapter(adapter);
        adapter.startListening();

    }

    private void SetValueOfPostedUser(String postedBy, final PostsViewHolder holder) {
        UserRef.child(postedBy).addValueEventListener(new ValueEventListener() {
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

    private void OnStarClicked(DatabaseReference PostRef, String pId, final PostsViewHolder holder) {
        PostRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Posts p = mutableData.getValue(Posts.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(userId)) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(userId);

                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(userId, true);


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

//    ======================================================== ViewHolder ======================================================

    public static class PostsViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout Cast_Users_profile;
        private TextView Cast_Full_Name, Cast_Post_Text, Cast_Post_TimeStamp;
        private ImageView Cast_Post_Img, Cast_Profile_Img, Cast_Comments, Cast_Unlike_Img;
        private LinearLayout Cast_Post_View, Cast_Like_Btn;


        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            View view = itemView;

            Cast_Post_View = (LinearLayout) view.findViewById(R.id.Post_View);
            Cast_Users_profile = (RelativeLayout) view.findViewById(R.id.Users_profile);
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
