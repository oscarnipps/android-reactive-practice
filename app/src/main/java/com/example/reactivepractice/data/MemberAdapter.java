package com.example.reactivepractice.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reactivepractice.R;
import com.example.reactivepractice.data.model.Member;
import com.example.reactivepractice.databinding.FetchedMemberLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private Context mContext;
    private List<Member> items = new ArrayList<>();
    private FetchedMemberLayoutBinding memberLayoutBinding;

    public MemberAdapter(Context mContext, List<Member> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        memberLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.fetched_member_layout,parent,false);
        return new MemberViewHolder(memberLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        holder.binding.memberName.setText(items.get(position).getFirstName());
        holder.binding.memberEmail.setText(items.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        if (items == null || items.size() == 0) {
            return 0;
        } else {
            return items.size();
        }
    }

    public void setItems(List<Member> members) {
        this.items = members;
        notifyDataSetChanged();
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {

        public FetchedMemberLayoutBinding binding;

        public MemberViewHolder(@NonNull FetchedMemberLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
