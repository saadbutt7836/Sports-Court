package com.canndecsolutions.garrisongamerss.Sheets;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.canndecsolutions.garrisongamerss.Adapters.CommentsAdapter;
import com.canndecsolutions.garrisongamerss.Models.CommentsModelClass;
import com.canndecsolutions.garrisongamerss.R;
import com.canndecsolutions.garrisongamerss.Utility.Utility;
import com.canndecsolutions.garrisongamerss.databinding.CommentBottomsheetViewBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FullScreenBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {


    private BottomSheetBehavior bottomSheetBehavior;
    private CommentBottomsheetViewBinding bi;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, PostRef, PostCommentsRef;

    private FirebaseRecyclerAdapter<CommentsModelClass, FullScreenBottomSheet.CommentsViewHolder> adapter;
    private FirebaseRecyclerOptions<CommentsModelClass> options;

    private CircleImageView Cast_Send_Btn;
    private EditText Cast_Comments_EditText;
    private RecyclerView Cast_RecyclerView;
    private TextView Cast_Word_Counter;


    private String postId = null,
            userId = null;


    //    ARRAYLIST
    private final List<CommentsModelClass> commentsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private CommentsAdapter commentsAdapter;

    private Query query = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        //inflating layout
        View view = View.inflate(getContext(), R.layout.comment_bottomsheet_view, null);

        //setting layout with bottom sheet
        bottomSheet.setContentView(view);

        //binding views to data binding.
        bi = DataBindingUtil.bind(view);


        //        Getting Value From Home Fragment
        Bundle mArgs = getArguments();
        postId = mArgs.getString("postId");


        WidgetCastings(view, bi);

        FirebaseCasting();


        //        Array List Adapter Layout Manager
//        RecyclerViewHandling();


        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));

        //setting Peek at the 16:9 ratio keyline of its parent.
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        //setting max height of bottom sheet

        bi.RecyclerView.setMinimumHeight((Resources.getSystem().getDisplayMetrics().heightPixels));

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    showView(bi.appBarLayout, getActionBarSize());
//                    hideAppBar(bi.RecyclerView);

                }
//                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
//                    hideAppBar(bi.appBarLayout);
////                    showView(bi.profileLayout, getActionBarSize());
//                }

                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss();
                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        return bottomSheet;
    }

    @Override
    public void onStart() {
        super.onStart();

        WordCounter();


//        FIREBASECJILD();


        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        //        Array List Adapter Layout Manager
        RecyclerViewHandling();

        RetrieveComments();

//        FirebaseAdapter();

//        adapter.startListening();


    }


//    @Override
//    public void onStop() {
//        super.onStop();
//
//        adapter.stopListening();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelBtn:
                dismiss();
                break;
            case R.id.Send_Btn:
                String comment = Cast_Comments_EditText.getText().toString().trim();

                if (!comment.isEmpty()) {
                    SaveCommentsToFirebase(comment);

                } else {
                    Toast.makeText(getActivity(), "Something went wrong ", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

//===================================================== FIREBASE + CHILD EVENT LISTENER ===================================================

    private void FIREBASECJILD() {
        query = PostCommentsRef.child(postId);


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {
//                Cast_RecyclerView.smoothScrollToPosition(adapter.getItemCount());
//                Log.d("print", String.valueOf(adapter.getItemCount()));

                CommentsModelClass modelClass = dataSnapshot.getValue(CommentsModelClass.class);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(childEventListener);
        extraadap();
    }

    private void extraadap() {


//        ===================== RECYCLER OPTIONS===========================
        options = new FirebaseRecyclerOptions.Builder<CommentsModelClass>().setQuery(query, CommentsModelClass.class).build();
        adapter = new FirebaseRecyclerAdapter<CommentsModelClass, CommentsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder holder, final int position, @NonNull final CommentsModelClass model) {


//                GET POST OWNER KEY AND RETRIEVE INFORMATION
                GetUserKeyAndShowInfo(model.getPosted_by(), holder);

//                SETS THE VALUES OF POSTS
                ShowComments(model, holder);


            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_comments_layout, parent, false);

                CommentsViewHolder viewHolder = new CommentsViewHolder(view);

                return viewHolder;
            }
        };

        Cast_RecyclerView.setAdapter(adapter);


//        Layout Manager Handling
        Cast_RecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        Cast_RecyclerView.setLayoutManager(linearLayoutManager);

    }


    //    ======================================================== CALLING METHODS ======================================================

    private void WidgetCastings(View view, CommentBottomsheetViewBinding bi) {

        Cast_Send_Btn = (CircleImageView) view.findViewById(R.id.Send_Btn);
        Cast_Comments_EditText = (EditText) view.findViewById(R.id.Comments_EditText);
        Cast_RecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        Cast_Word_Counter = (TextView) view.findViewById(R.id.Word_Counter);

//        CLICK LISTENERS
        bi.cancelBtn.setOnClickListener(this);
        Cast_Send_Btn.setOnClickListener(this);


    }

    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");
        PostRef = mDatabase.child("Posts");
        PostCommentsRef = mDatabase.child("Posts-Comments");

    }

    private void FirebaseAdapter() {


        options = new FirebaseRecyclerOptions.Builder<CommentsModelClass>().setQuery(PostCommentsRef.child(postId), CommentsModelClass.class).build();
        adapter = new FirebaseRecyclerAdapter<CommentsModelClass, CommentsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder holder, final int position, @NonNull final CommentsModelClass model) {


                //                GET POST OWNER KEY AND RETRIEVE INFORMATION
                GetUserKeyAndShowInfo(model.getPosted_by(), holder);

//                SETS THE VALUES OF POSTS
                ShowComments(model, holder);


            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_comments_layout, parent, false);

                CommentsViewHolder viewHolder = new CommentsViewHolder(view);

                return viewHolder;
            }
        };


        Cast_RecyclerView.setAdapter(adapter);

        adapter.startListening();
//        Cast_RecyclerView.smoothScrollToPosition(adapter.getItemCount());
        Log.d("print", String.valueOf(adapter.getItemCount()));

//        Layout Manager Handling
        Cast_RecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        Cast_RecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void SaveCommentsToFirebase(String comment) {
        String commentKey = mDatabase.push().getKey();

        Map<String, Object> PostCommentsMap = new HashMap<>();
        PostCommentsMap.put("comid", commentKey);
        PostCommentsMap.put("comment", comment);
        PostCommentsMap.put("timestamp", System.currentTimeMillis());
        PostCommentsMap.put("posted_by", userId);

        PostCommentsRef.child(postId).child(commentKey).updateChildren(PostCommentsMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Commented", Toast.LENGTH_SHORT).show();

                            Cast_Comments_EditText.setText(null);
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void GetUserKeyAndShowInfo(String postedBy, final CommentsViewHolder holder) {

        Query query = UserRef.orderByChild("uid").equalTo(postedBy);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userKey = snapshot.getKey();
                    SetValueOfUser(userKey, holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(getActivity(), "error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void SetValueOfUser(String key, final CommentsViewHolder holder) {
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

    private void ShowComments(CommentsModelClass model, CommentsViewHolder holder) {

        holder.Cast_Comment_Text.setText(model.getComment());

        holder.Cast_TimeStamp.setText(Utility.TimeStampHandle(model.getTimestamp().longValue()));
    }

    private void WordCounter() {
        Cast_Comments_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String currentText = editable.toString();
                int currentLength = currentText.length();
                currentLength = 180 - currentLength;

                Cast_Word_Counter.setText(" " + currentLength);
            }
        });
    }

    private void hideAppBar(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);

    }

    private void showView(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize() {
        final TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = (int) array.getDimension(0, 0);
        return size;
    }


//    ===================================================================================================================

    //    RecyclerView Handling
    private void RecyclerViewHandling() {
        commentsAdapter = new CommentsAdapter(getActivity(), commentsList);


        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        Cast_RecyclerView.setLayoutManager(linearLayoutManager);
        Cast_RecyclerView.setAdapter(commentsAdapter);
    }


    private void RetrieveComments() {


        commentsList.clear();
        PostCommentsRef.child(postId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        CommentsModelClass messagesModelClass = dataSnapshot.getValue(CommentsModelClass.class);
                        commentsList.add(messagesModelClass);
                        commentsAdapter.notifyDataSetChanged();


                        Cast_RecyclerView.smoothScrollToPosition(commentsList.size());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    //    ======================================================== ViewHolder ======================================================


    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        private TextView Cast_Full_Name, Cast_Comment_Text, Cast_TimeStamp;
        private ImageView Cast_Profile_Img;


        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            View view = itemView;
            Cast_Full_Name = (TextView) view.findViewById(R.id.Name);
            Cast_TimeStamp = (TextView) view.findViewById(R.id.TimeStamp);
            Cast_Profile_Img = (ImageView) view.findViewById(R.id.Profile_Img);
            Cast_Comment_Text = (TextView) view.findViewById(R.id.Comment_Body);

        }
    }


}
