package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {
    private List<User> userList;
    private List<User> userListFull;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public UserAdapter(List<User> userList, OnDeleteClickListener listener) {
        this.userList = userList;
        this.userListFull = new ArrayList<>(userList);
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvType.setText(user.getType());

        String details = "";
        switch (user.getType()) {
            case "Student":
                details = user.getDepartment() + " - " + user.getClassYear();
                break;
            case "Admin":
                details = "Role: " + user.getDepartment();
                break;
        }
        holder.tvDetails.setText(details);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<User> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(userListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (User user : userListFull) {
                        if (user.getName().toLowerCase().contains(filterPattern) ||
                                user.getType().toLowerCase().contains(filterPattern)) {
                            filteredList.add(user);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userList.clear();
                userList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvDetails;
        ImageButton btnDelete;

        UserViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}