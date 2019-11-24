package com.edufree.bookshoot.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.edufree.bookshoot.MainActivity;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.models.Author;
import com.edufree.bookshoot.utils.pConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class authorAdapter extends RecyclerView.Adapter<authorAdapter.mViewHolder> {

    private Context mContext;
    private ArrayList<Author> mAuthors;

    public authorAdapter(Context context, ArrayList<Author> authors) {
        mContext = context;
        mAuthors = authors;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.author_list_item,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        final Author author= mAuthors.get(position);

        //setUp authors....
        int auth_len=author.getAuthor_categories().size();
        StringBuilder sb=new StringBuilder();

        for(int a=0;a<auth_len;a++){
            sb.append(author.getAuthor_categories().get(a)+" , ");
        }

        holder.author_name.setText(author.getAuthor_name());
        //holder.author_categories.setText(sb.toString());
        //set image using picasson ...
        if(!author.getAuthor_thumbnail().isEmpty() && author.getAuthor_thumbnail()!=null){
            Picasso.get().load(author.getAuthor_thumbnail()).placeholder(R.drawable.authors_default).into(holder.author_image);
        }else{
            holder.author_image.setImageResource(R.drawable.authors_default);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMain=new Intent(mContext, MainActivity.class);
                openMain.putExtra(pConstants.CLICKED_AUTHOR__KEY,author.getAuthor_name());
                mContext.startActivity(openMain);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mAuthors.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder {
        TextView author_name,author_categories;
        CircleImageView author_image;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            author_name=(TextView)itemView.findViewById(R.id.author_name);
            //author_categories=(TextView)itemView.findViewById(R.id.author_categories);
            author_image=(CircleImageView)itemView.findViewById(R.id.author_image);

        }

    }

}
