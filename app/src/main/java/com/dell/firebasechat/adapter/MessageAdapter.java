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
import android.widget.TextView;

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

        Message message = chatsList.get(position);
        if(position == 0 && position == chatsList.size()-1){
            if (holder.getItemViewType() == TYPE_RECEIVER) {
                holder.tvMess.setBackgroundResource(R.drawable.draw_chat_left4);
            } else if (holder.getItemViewType() == TYPE_SENDER) {
                holder.tvMess.setBackgroundResource(R.drawable.draw_chat_rigth4);
            }
        }
        if(position == 0){
            if(position+1<chatsList.size()){
                Message message1 = chatsList.get(position+1);
                if(message1.getSender().equals(message.getSender()) && message1.getReceiver().equals(message.getReceiver())){
                    if(checkTime(message.getTime(),message1.getTime())){
                        changeHolder(holder,R.drawable.draw_chat_left,R.drawable.draw_chat_rigth);
                    }else{
                        changeHolder2(holder,R.drawable.draw_chat_left4,R.drawable.draw_chat_rigth4);
                    }
                }else{
                    changeHolder2(holder,R.drawable.draw_chat_left4,R.drawable.draw_chat_rigth4);
                }
            }
        }
        if(position == 1){
            if(position>0){
                Message message_1 = chatsList.get(position-1);
                if(message_1.getSender().equals(message.getSender()) && message_1.getReceiver().equals(message.getReceiver())){
                    if(checkTime(message.getTime(),message_1.getTime())){
                        changeHolder2(holder,R.drawable.draw_chat_left2,R.drawable.draw_chat_rigth2);
                    }
                }
            }
        }
        if(position == chatsList.size()-1&& position>0){
            Message message_1 = chatsList.get(position-1);
            if(message_1.getSender().equals(message.getSender()) && message_1.getReceiver().equals(message.getReceiver())){
                if(checkTime(message.getTime(),message_1.getTime())){
                    changeHolder(holder,R.drawable.draw_chat_left2,R.drawable.draw_chat_rigth2);
                }
            }
        }

        if(position!=chatsList.size()-1){
            if(position+1<chatsList.size() && position-1>=0){
                Message message_1 = chatsList.get(position-1);
                Message message1 = chatsList.get(position+1);
                if((message1.getSender().equals(message.getSender()) && message1.getReceiver().equals(message.getReceiver())) && (!message_1.getSender().equals(message.getSender()) && !message_1.getReceiver().equals(message.getReceiver()))){
                    if(checkTime(message1.getTime(),message.getTime())){
                        changeHolder(holder,R.drawable.draw_chat_left,R.drawable.draw_chat_rigth);
                    }else {
                        changeHolder(holder,R.drawable.draw_chat_left4,R.drawable.draw_chat_rigth4);
                    }
                }
                if((!message1.getSender().equals(message.getSender()) && !message1.getReceiver().equals(message.getReceiver())) && (message_1.getSender().equals(message.getSender()) && message_1.getReceiver().equals(message.getReceiver()))){
                    if(checkTime(message.getTime(),message_1.getTime())){
                        changeHolder2(holder,R.drawable.draw_chat_left2,R.drawable.draw_chat_rigth2);
                    }
                }
                if((message1.getSender().equals(message.getSender()) && message1.getReceiver().equals(message.getReceiver())) && (message_1.getSender().equals(message.getSender()) && message_1.getReceiver().equals(message.getReceiver()))){
                    if(Long.parseLong(message.getTime())-Long.parseLong(message_1.getTime())>8000 && checkTime(message1.getTime(),message.getTime())){
                        changeHolder2(holder,R.drawable.draw_chat_left,R.drawable.draw_chat_rigth);
                    }
                    if(Long.parseLong(message1.getTime())-Long.parseLong(message.getTime())>8000 && checkTime(message.getTime(),message_1.getTime())){
                        changeHolder(holder,R.drawable.draw_chat_left2,R.drawable.draw_chat_rigth2);
                    }
                    if(Long.parseLong(message.getTime())-Long.parseLong(message_1.getTime())>8000 && Long.parseLong(message1.getTime())-Long.parseLong(message.getTime())>8000){

                        changeHolder(holder,R.drawable.draw_chat_left4,R.drawable.draw_chat_rigth4);
                    }
                    if(checkTime(message.getTime(),message_1.getTime()) && checkTime(message1.getTime(),message.getTime())){
                        changeHolder(holder,R.drawable.draw_chat_left3,R.drawable.draw_chat_rigth3);
                    }
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

    public void changeHolder (MessageViewHolder holder, int t1, int t2){
            if (holder.getItemViewType() == TYPE_RECEIVER) {
                holder.imageFriend.setVisibility(View.INVISIBLE);
                holder.tvMess.setBackgroundResource(t1);
            } else if (holder.getItemViewType() == TYPE_SENDER) {
                holder.tvMess.setBackgroundResource(t2);
            }
    }
    public void changeHolder2 (MessageViewHolder holder, int t1, int t2){
        if (holder.getItemViewType() == TYPE_RECEIVER) {
            holder.tvMess.setBackgroundResource(t1);
        } else if (holder.getItemViewType() == TYPE_SENDER) {
            holder.tvMess.setBackgroundResource(t2);
        }
    }

    public boolean checkTime(String t1, String t2){
        if(Long.parseLong(t1)-Long.parseLong(t2)<=8000) return true;
        return false;
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

    public void removeItem(int position) {
        chatsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, chatsList.size());
    }
}

