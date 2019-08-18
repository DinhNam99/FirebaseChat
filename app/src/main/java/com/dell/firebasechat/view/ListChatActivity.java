package com.dell.firebasechat.view;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dell.firebasechat.R;
import com.dell.firebasechat.adapter.UsersAdapter;

import com.dell.firebasechat.model.UIActionClass;
import com.dell.firebasechat.model.User;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListChatActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SearchView searchView;
    CircleImageView imageUser;
    public static ArrayList<User> userArrayList = new ArrayList<>();
    RecyclerView rcUser;
    UsersAdapter adapter;
    String username;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        imageUser = findViewById(R.id.imageUser);
        rcUser = findViewById(R.id.rcUser);
        rcUser.setHasFixedSize(true);
        rcUser.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(getApplicationContext(), userArrayList);
        rcUser.setAdapter(adapter);
        readData();



        //deleteuser
        removeUser();


        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    public void readData() {
        final FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (!user.getId().equals(users.getUid())) {
                        userArrayList.add(user);
                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listchat, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                final FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(ListChatActivity.this,ActivityFriend.class);
                intent.putExtra("idUser",users.getUid());
                startActivity(intent);
                break;
            case R.id.logout:
                Logout();
                break;
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.equals("")){
            Toast.makeText(ListChatActivity.this, "Please enter to query !!",Toast.LENGTH_SHORT).show();
        }
        adapter.getFilter().filter(newText);
        return false;
    }


    private void removeUser(){

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Drawable deleteDrawable = ContextCompat.getDrawable(ListChatActivity.this, R.drawable.ic_delete);
                reference = FirebaseDatabase.getInstance().getReference("Users");
                int position = viewHolder.getAdapterPosition();
                final User deletedModel = userArrayList.get(position);
                adapter.removeItem(position);
                try {
                    reference.child(deletedModel.getId()).setValue(null);
                    reference.child(deletedModel.getImage() + "").setValue(null);
                    reference.child(deletedModel.getUsername() + "").setValue(null);
                } catch (DatabaseException e) {

                }
            }


        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rcUser);
    }
    private void Logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListChatActivity.this);
        builder.setTitle("Do you want to log out ?");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = getIntent();
                String action = intent.getStringExtra("Logout");
                GoogleSignInOptions options = new GoogleSignInOptions
                        .Builder()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(),options);

                LoginManager.getInstance().logOut();
                googleSignInClient.signOut();
                startActivity(new Intent(ListChatActivity.this, LoginActivity.class));
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
