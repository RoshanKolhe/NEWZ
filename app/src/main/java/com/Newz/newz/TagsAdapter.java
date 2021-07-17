package com.Newz.newz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagsViewHolder> {

    private List<Tags> listItems;
    public Context context;
    public Activity activity;

    public TagsAdapter(List<Tags> listItems, Context context, Activity activity) {
        this.listItems = listItems;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item_view,parent,false);
        return new TagsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TagsViewHolder holder, final int position) {
        final Tags listItem=listItems.get(position);
        holder.tags.setText("#"+listItem.getTagname());
        holder.tags.setAnimation(AnimationUtils.loadAnimation(context,R.anim.scale_out));
        holder.tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    StyleableToast.makeText(context, ""+holder.tags.getText(), Toast.LENGTH_LONG,R.style.successtoast).show();
                Intent intent=new Intent(context,Categories.class);
                intent.putExtra("tag",holder.tags.getText().toString());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class TagsViewHolder extends RecyclerView.ViewHolder {
        public TextView tags;
        public TagsViewHolder(@NonNull View itemView) {
            super(itemView);
            tags = itemView.findViewById(R.id.tags);
        }
    }
}
