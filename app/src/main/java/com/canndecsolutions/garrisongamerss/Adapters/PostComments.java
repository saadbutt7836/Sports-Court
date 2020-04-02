package com.canndecsolutions.garrisongamerss.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.canndecsolutions.garrisongamerss.Models.PostComment;
import com.canndecsolutions.garrisongamerss.R;
import com.canndecsolutions.garrisongamerss.Utility.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostComments extends RecyclerView.Adapter<PostComments.CommentsViewHolder> {

    private Context context;
    private List<PostComment> commentsList;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef, PostRef, PostCommentsRef;

    private String userId = null;

    public PostComments(Context context, List<PostComment> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_comments_layout, null);

        FirebaseCasting();

        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsViewHolder holder, int position) {



        final PostComment model = commentsList.get(position);


//                GET POST OWNER KEY AND RETRIEVE INFORMATION
        GetUserKeyAndShowInfo(model.getPosted_by(), holder);


        //                SETS THE VALUES OF POSTS
        ShowCommets(model, holder);


    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    //    ======================================================== VIEW HOLDER ======================================================


    public class CommentsViewHolder extends RecyclerView.ViewHolder {

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


    //    ======================================================== CALLING METHODS ======================================================


    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserRef = mDatabase.child("Users");
        PostRef = mDatabase.child("Posts");
        PostCommentsRef = mDatabase.child("Posts-Comments");

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
                Toast.makeText(context, "error: " + error, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void ShowCommets(PostComment model, CommentsViewHolder holder) {

        holder.Cast_Comment_Text.setText(model.getComment());

        holder.Cast_TimeStamp.setText(Utility.TimeStampHandle(model.getTimestamp().longValue()));
    }


}