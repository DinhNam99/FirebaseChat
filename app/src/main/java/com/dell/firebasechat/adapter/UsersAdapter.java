package com.dell.firebasechat.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dell.firebasechat.R;
import com.dell.firebasechat.model.User;
import com.dell.firebasechat.view.ChatActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> implements Filterable {

    public  List<User> users;
    List<User> usersFilter;
    Context context;



    public UsersAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
        this.usersFilter = users;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        UserHolder holder = new UserHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        final User user = users.get(position);
        holder.tvUser.setText(user.getUsername());
        holder.igUser.setImageResource(R.drawable.account);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Uid", user.getId());
                intent.putExtra("UserName", user.getUsername());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersFilter.size();
    }


    public void removeItem(int position) {
        users.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, users.size());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    usersFilter = users;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User row : users) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    usersFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = usersFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                usersFilter = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class UserHolder extends RecyclerView.ViewHolder {

        CircleImageView igUser;
        TextView tvUser;
        LinearLayout linearLayout;

        public UserHolder(View itemView) {
            super(itemView);
            igUser = itemView.findViewById(R.id.igUserItem);
            tvUser = itemView.findViewById(R.id.tvItemUser);
            linearLayout = itemView.findViewById(R.id.linea);
        }
    }

}
