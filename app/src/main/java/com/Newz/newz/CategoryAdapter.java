package com.Newz.newz;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryAdapter.ViewHolder1> {

    private List<Category> listItems;
    private Context context;
    Activity activity;
    SharedPreferences sharedPreferences;
    int sucess;
    private RequestQueue requestQueue;
    private String TAG_SUCESS="sucess";
    private String TAG_MESSAGE="message";
    private String tag_json_obj="json_obj_req";

    public CategoryAdapter(List<Category> listItems, Context context, Activity activity) {
        this.listItems = listItems;
        this.context = context;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_layout,parent,false);
        return new ViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder1 holder, final int position) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        requestQueue= Volley.newRequestQueue(context);
        final Category listItem=listItems.get(position);
        holder.title.setText(listItem.getTitle());
        holder.desc.setText(listItem.getShort_news());
        holder.date.setText(listItem.getDate());
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final String url="https://www.datamanagement.ml/bookmarkNews.php";
                    final String email=sharedPreferences.getString(context.getResources().getString(R.string.uemail),"");
                    StringRequest request=new StringRequest(Request.Method.POST,url , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jobj = new JSONObject(response);
                                sucess = jobj.getInt(TAG_SUCESS);
                                if (sucess == 1) {

                                    StyleableToast.makeText(context, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                                } else {
                                    StyleableToast.makeText(context, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                                }
                            } catch (JSONException e) {
                                StyleableToast.makeText(context, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            StyleableToast.makeText(context, error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    }){
                        public Map<String,String> getParams(){
                            Map<String, String> params=new HashMap<String, String>();
                            params.put("useremail",email);
                            params.put("newsid",listItem.getId().toString());
                            return params;
                        }
                    };
                    request.setRetryPolicy(new DefaultRetryPolicy(10000,1,1.0f));
                    requestQueue.add(request);
                }catch(Exception e){
                    StyleableToast.makeText(context, "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
                }
            }
        });
        try {
            holder.v.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    //activity.startActivity(new Intent(activity, DetailsActivity.class));
                    Intent intent = new Intent(activity,DetailsActivity.class);
                    Category listItem=listItems.get(position);
                    intent.putExtra("id",listItem.getId());
                    intent.putExtra("image",listItem.getPhoto());
                    //Toast.makeText(context, ""+listItem.getPhoto(), Toast.LENGTH_SHORT).show();
                    intent.putExtra("title",listItem.getTitle());
                    intent.putExtra("details",listItem.getDetail_news());
                    intent.putExtra("date",listItem.getDate());
                    intent.putExtra("link",listItem.getNews_link());
                    intent.putExtra("category",listItem.getCategory());
                    intent.putExtra("short_news",listItem.getShort_news());
                    intent.putExtra("credits",listItem.getOriginal_article());
                    intent.putExtra("tags",listItem.getTags());
                    Pair pair=new Pair<View,String>(holder.newsimg,"detailimg");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,pair);
                    activity.startActivity(intent,options.toBundle());
                }
            });
        }catch (Exception e){
            StyleableToast.makeText(context, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
        }
        holder.share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"*"+listItem.getTitle().toString()+"*"+"\n"
                        + listItem.getShort_news().toString()+"\n\n"+"Check out This application To Earn While Reading : "
                        + "https://play.google.com/store/apps/details?id=com.example.newz");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, "Share Via");
                activity.startActivity(shareIntent );


            }
        });
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E4E2E2"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();

        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        Glide.with(activity).load(listItem.getPhoto())
                .placeholder(shimmerDrawable)
                .into(holder.newsimg);

//        holder.newsimg.setAnimation(AnimationUtils.loadAnimation(context,R.anim.logo_fade_in));
//        holder.newscard.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition));


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public  class ViewHolder1 extends RecyclerView.ViewHolder{
        public TextView title,desc,date;
        public View v;
        ImageView newsimg;
        CardView newscard;
        Button bookmark,share;
        RelativeLayout mcontainer;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            v=itemView;
            title=itemView.findViewById(R.id.text_view_title);
            desc=itemView.findViewById(R.id.text_view_description);
            newsimg=itemView.findViewById(R.id.newsimg);
            newscard=itemView.findViewById(R.id.newscard);
            bookmark=itemView.findViewById(R.id.bookmark);
            share=itemView.findViewById(R.id.share);
            date=itemView.findViewById(R.id.date);
            mcontainer=itemView.findViewById(R.id.layoutcontainer);
        }
    }


}
