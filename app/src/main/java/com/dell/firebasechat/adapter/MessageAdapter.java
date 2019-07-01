package com.dell.firebasechat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dell.firebasechat.R;
import com.dell.firebasechat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int TYPE_SENDER = 0;
    public static final int TYPE_RECEIVER = 1;

    List<Message> chatsList;
    Context context;

    FirebaseUser firebaseUser;
    public MessageAdapter (Context context, List<Message> chatsList){
        this.context = context;
        this.chatsList = chatsList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_SENDER){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        Message chats = chatsList.get(position);
        Log.d("Message", chats.getMessage() +"");
        holder.tvMess.setText(chats.getMessage());
        if(holder.tvMess.getText().equals("")){
            holder.tvMess.setVisibility(View.GONE);
        }
        if(!chats.getImageMess().equals("")){
            Picasso.get().load(chats.getImageMess()).into(holder.imageMess);
            holder.imageMess.setVisibility(View.VISIBLE);

        }

        //time
        long timestampString =  Long.parseLong(chatsList.get(position).getTime());
        final String value = new java.text.SimpleDateFormat("HH:mm:ss").
                format(new java.util.Date(timestampString));
        holder.tvTime.setText(value);

       Log.e("Position", position+"");

      if(position>0) {
          if (Long.parseLong(chatsList.get(position).getTime()) - Long.parseLong(chatsList.get(position - 1).getTime()) <= 8000) {
              if (holder.getItemViewType() == TYPE_RECEIVER) {
                  holder.imageFriend.setVisibility(View.INVISIBLE);
                  holder.tvMess.setBackgroundResource(R.drawable.draw_chat_left2);
              } else if (holder.getItemViewType() == TYPE_SENDER) {
                  holder.tvMess.setBackgroundResource(R.drawable.draw_chat_rigth2);
              }
          }

      }
        //seen
        if(position == chatsList.size()-1){
            if(chatsList.get(position).isSeen()){
                holder.tvSeen.setText("Seen");
            }else{
                holder.tvSeen.setText("");
            }
        }else{
            holder.tvSeen.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tvTime.getVisibility() == View.VISIBLE){
                    holder.tvTime.setVisibility(View.GONE);
                }else {
                    holder.tvTime.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMess,tvTime, tvSeen;
        CircleImageView imageFriend;
        ConstraintLayout layout;
        ImageView imageMess;

        public MessageViewHolder(View itemView) {
            super(itemView);
            imageFriend = itemView.findViewById(R.id.igFriend);
            tvSeen = itemView.findViewById(R.id.tvSeen);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMess = itemView.findViewById(R.id.tvMessage);
            layout = itemView.findViewById(R.id.layoutchat);
            imageMess = itemView.findViewById(R.id.igtest);
        }

    }
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatsList.get(position).getSender().equals(firebaseUser.getUid())){
            return TYPE_SENDER;
        }else{
            return TYPE_RECEIVER;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

