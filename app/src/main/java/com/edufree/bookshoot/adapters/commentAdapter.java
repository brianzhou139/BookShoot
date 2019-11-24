package com.edufree.bookshoot.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edufree.bookshoot.R;
import com.edufree.bookshoot.models.Comment;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.mViewHolder> {

    private Context mContext;
    private ArrayList<Comment> mList;

    public commentAdapter(Context context, ArrayList<Comment> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_list_item,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        final Comment comment=mList.get(position);
        holder.username.setText(comment.getUsername());
        holder.comment.setText(comment.getContent());

        if(comment.getRating().isEmpty() || comment.getRating()==null){

        }else{
            float rating= Float.parseFloat(comment.getRating());
            holder.rating.setRating(rating);
        }

        //set image using picasson ...
        Picasso.get().load(comment.getThumbnail()).placeholder(R.drawable.ic_comment).into(holder.profile);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder {
        TextView username,comment;
        private RatingBar rating;
        CircleImageView profile;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            username=(TextView)itemView.findViewById(R.id.ad_username);
            comment=(TextView)itemView.findViewById(R.id.ad_comment);
            rating=(RatingBar) itemView.findViewById(R.id.rating_of_bookComment_item);
            profile=(CircleImageView)itemView.findViewById(R.id.ad_image);
        }

    }

}
