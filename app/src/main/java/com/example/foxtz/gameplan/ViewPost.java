package com.example.foxtz.gameplan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ViewPost extends AppCompatActivity {

    TextView organizerText;
    TextView gameText;
    TextView categoryText;
    TextView dateText;
    TextView timeText;
    TextView cityText;
    TextView playersText;
    TextView descriptionText;
    Button joinButton;
    TextView goingText;

    boolean canJoin;
    boolean isHistory;
    boolean isHosting;
    String dateTimePath;
    String postID;
    int desiredNumPlayers;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        organizerText = findViewById(R.id.organizerTextView);
        gameText = findViewById(R.id.gameTextView);
        categoryText = findViewById(R.id.categoryTextView);
        dateText = findViewById(R.id.dateTextView);
        timeText = findViewById(R.id.timeTextView);
        cityText = findViewById(R.id.cityTextView);
        playersText = findViewById(R.id.playersTextView);
        descriptionText = findViewById(R.id.descriptionTextView);
        goingText = findViewById(R.id.goingText);
        goingText.setTextSize(32);

        joinButton = findViewById(R.id.joinButton);
        joinButton.setText("loading");
        joinButton.setEnabled(false);
        joinButton.setVisibility(View.GONE);
        goingText.setVisibility(View.GONE);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //updating user's attending posts with new post
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference refPostAll = database.getReference("posts/" + dateTimePath + "/" + postID);
                DatabaseReference refPostUser = database.getReference("users/" + userID + "/attending/" + dateTimePath + "/" + postID);
                copyAtDB(refPostAll,refPostUser);

                //add userID to list of attending users
                //refPostAll.child("joined").child(userID).setValue(true);

                //update number of current players in original post (at DB) and on this activity
                final DatabaseReference refPlayersNum = refPostAll.child("currentNumPlayers");
                refPlayersNum.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int currentPlayers = Integer.valueOf(dataSnapshot.getValue().toString());
                        currentPlayers++;
                        refPlayersNum.setValue(currentPlayers);
                        joinButton.setEnabled(false);
                        joinButton.setText("going");
                        playersText.setText(currentPlayers + " out of " + desiredNumPlayers);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        Post post = (Post) getIntent().getSerializableExtra("Post");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        isHosting = post.getUserID().equals(userID);
        desiredNumPlayers = post.getDesiredNumPlayers();
        canJoin = getIntent().getBooleanExtra("canJoin",false);
        isHistory = getIntent().getBooleanExtra("isHistory",false);

        String date = String.valueOf(post.getYear()) + "/" + post.getMonth() + "/" +  String.format("%02d", post.getDay());
        String time = post.getTime();
        dateTimePath = date + "/" + time;
        postID = post.getId();
        showPostDataFromDB(post);
        setJoinButtonStatus(canJoin);
    }

    public void showPostDataFromDB(final Post post){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refPost = database.getReference("posts/" + dateTimePath + "/" + postID);
        refPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> postMap = (HashMap<String, Object>) dataSnapshot.getValue();
                String postID = dataSnapshot.getKey();
                Post fullPost = new Post(postMap.get("category").toString(), postMap.get("game").toString(), post.getHour(), post.getMinutes(),
                        post.getDay(), post.getMonth(), post.getYear(), postMap.get("city").toString(), postMap.get("userID").toString(),
                        postMap.get("currentNumPlayers").toString(), postMap.get("desiredNumPlayers").toString(),
                        postMap.get("description").toString(), postMap.get("user_name").toString(), postID);

                organizerText.setText(fullPost.getUserName());
                gameText.setText(fullPost.getGame());
                categoryText.setText(fullPost.getCategory());
                dateText.setText(fullPost.getDate());
                timeText.setText(fullPost.getTime());
                cityText.setText(fullPost.getCity());
                playersText.setText(fullPost.getCurrNumPlayers() + " out of " + fullPost.getDesiredNumPlayers());
                descriptionText.setText(fullPost.getDescription());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setJoinButtonStatus(final Boolean canJoin){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refPost = database.getReference("users/" + userID + "/attending/" + dateTimePath + "/" + postID);
        refPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(canJoin && !dataSnapshot.exists()){
                    joinButton.setText("Join");
                    joinButton.setEnabled(true);
                    joinButton.setVisibility(View.VISIBLE);
                } else {
                    joinButton.setEnabled(false);
                    joinButton.setVisibility(View.GONE); // View.INVISIBLE reserves space for the item

                    goingText.setVisibility(View.VISIBLE);
                    if(isHosting){
                        if(isHistory){
                            goingText.setText("You hosted this event");
                        } else {
                            goingText.setText("You are hosting this event");
                        }
                    } else {
                        if(isHistory){
                            goingText.setText("You attended this event");
                        } else {
                            goingText.setText("Attending");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void copyAtDB(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(ViewPost.this, "Copy failed", Toast.LENGTH_SHORT).show();
                            System.out.println("Copy failed");
                        } else {
                            Toast.makeText(ViewPost.this, "Copy success", Toast.LENGTH_SHORT).show();
                            System.out.println("Success");

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
