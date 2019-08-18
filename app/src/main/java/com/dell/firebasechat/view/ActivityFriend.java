package com.dell.firebasechat.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.dell.firebasechat.R;

import com.dell.firebasechat.adapter.FriendAdapter;
import com.dell.firebasechat.model.Friend;
import com.dell.firebasechat.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityFriend extends AppCompatActivity {
    private RecyclerView recyclerListFrends;
    private DatabaseReference usersRef;
    ArrayList<User> friends = new ArrayList<>();
    FirebaseUser firebaseUser;
    FriendAdapter adapter;

    String idUser;
    Toolbar mToolbar;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add new friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerListFrends = findViewById(R.id.rcFriend);
        recyclerListFrends.setHasFixedSize(true);
        recyclerListFrends.setLayoutManager(new LinearLayoutManager(ActivityFriend.this));
        adapter = new FriendAdapter(ActivityFriend.this,friends);
        recyclerListFrends.setAdapter(adapter);
        Intent intent = getIntent();
        idUser = intent.getStringExtra("idUser");
        readFriend();

    }
    private void readFriend() {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(idUser)) {
                        Log.e("IDUSER", user.getId() + "");
                        friends.add(user);
                        adapter.notifyDataSetChanged();
                    }

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("friend"+"/"+idUser+"/"+user.getId());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (user.getId().equals(idUser)) {
                                friends.remove(user);
                                adapter.notifyDataSetChanged();
                            }else if(dataSnapshot.getValue() != null) {

                                friends.remove(user);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
