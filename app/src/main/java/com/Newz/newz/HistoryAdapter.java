package com.Newz.newz;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder>{
    private List<HistoryHelper> listItems;
    private Context context;
    Activity activity;
    public HistoryAdapter(List<HistoryHelper> listItems, Context context, Activity activity) {
        this.listItems = listItems;
        this.context = context;
        this.activity=activity;
    }
    @NonNull
    @Override
    public HistoryAdapter.HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout,parent,false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, final int position) {
        final HistoryHelper listItem=listItems.get(position);
        Log.d("listitem",""+listItem);
        holder.date.setText(listItem.getDate());
       // holder.pointsEarned.setText(listItem.getPointsEarned());
        String type=listItem.getType();
        if(type.equals("Redeem")){
            holder.type.setTextColor(context.getResources().getColor(R.color.orange));
            holder.pointsEarned.setTextColor(context.getResources().getColor(R.color.orange));
            holder.pointsEarned.setText(listItem.getPointsEarned());
        }else{
            holder.pointsEarned.setText("+ "+listItem.getPointsEarned());
        }
        holder.type.setText(listItem.getType());
//        try {
//            holder.v.setOnClickListener(new View.OnClickListener() {
//                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, "you clicked"+position, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }catch (Exception e){
//            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
//        }

        holder.historycard.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition));

    }

    @Override
    public int getItemCount() {
        return  listItems.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        public TextView date,pointsEarned,type;
        public View v;
        CardView historycard;
        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            v=itemView;
            date=itemView.findViewById(R.id.date);
            pointsEarned=itemView.findViewById(R.id.pointsEarned);
            type=itemView.findViewById(R.id.type);
            historycard=itemView.findViewById(R.id.hcard);


        }
    }
}
