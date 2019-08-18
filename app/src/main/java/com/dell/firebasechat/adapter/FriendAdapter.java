package com.dell.firebasechat.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dell.firebasechat.R;
import com.dell.firebasechat.model.User;
import com.dell.firebasechat.view.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {

    List<User> users;
    Context context;
    public FriendAdapter(Context context, List<User> users){
        this.users = users;
        this.context = context;
    }
    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        FriendHolder holder = new FriendHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        final User user = users.get(position);
        holder.tvUser.setText(user.getUsername());
        holder.igUser.setImageResource(R.drawable.account);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Do you want to add friend ?");
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        addFriend(user.getId(),user.getUsername());
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("Uid", user.getId());
                        intent.putExtra("UserName", user.getUsername());
                        Log.e("Uid",user.getId());
                        context.startActivity(intent);
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void addFriend(String idfriend,String username){
        final String userLoggedIn = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //final String newFriendEncodedEmail = EmailEncoding.commaEncodePeriod(newFriendEmail);
        final DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friend"
                + "/" + userLoggedIn);
        //Add friends to current users friends list
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",idfriend);
        hashMap.put("username", username);
        friendsRef.child(idfriend).setValue(hashMap);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class FriendHolder extends RecyclerView.ViewHolder{

        CircleImageView igUser;
        TextView tvUser;
        LinearLayout linearLayout;

        public FriendHolder(View itemView) {
            super(itemView);
            igUser = itemView.findViewById(R.id.igUserItem);
            tvUser = itemView.findViewById(R.id.tvItemUser);
            linearLayout = itemView.findViewById(R.id.linea);
        }
    }
}
