package com.example.foxtz.gameplan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        TextView organizer = findViewById(R.id.organizerTextView);
        TextView game = findViewById(R.id.gameTextView);
        TextView category = findViewById(R.id.categoryTextView);
        TextView date = findViewById(R.id.dateTextView);
        TextView time = findViewById(R.id.timeTextView);
        TextView city = findViewById(R.id.cityTextView);
        TextView players = findViewById(R.id.playersTextView);
        TextView description = findViewById(R.id.descriptionTextView);

        Button joinLeaveButton = findViewById(R.id.joinLeaveButton);
        joinLeaveButton.setText("loading");
        joinLeaveButton.setEnabled(false);
        joinLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ViewPost.this, "TODO", Toast.LENGTH_SHORT).show();
            }
        });


        Post post = (Post) getIntent().getSerializableExtra("Post");

        organizer.setText(post.getUserName());
        game.setText(post.getGame());
        category.setText(post.getCategory());
        date.setText(post.getDate());
        time.setText(post.getTime());
        city.setText(post.getCity());
        players.setText(post.getCurrNumPlayers() + " out of " + post.getDesiredNumPlayers());
        description.setText(post.getDescription());

        String dataTimeString = post.getDate() + "/" + post.getTime();//here
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refPosts = database.getReference(dataTimeString);
        refPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
