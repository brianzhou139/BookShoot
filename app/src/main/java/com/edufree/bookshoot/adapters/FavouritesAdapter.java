package com.edufree.bookshoot.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edufree.bookshoot.BookDetailActivity;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.utils.pConstants;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.BookViewHolder> {

    private Context mContext;
    private ArrayList<Book> books;

    public FavouritesAdapter(Context context, ArrayList<Book> books) {
        mContext = context;
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(mContext).inflate(R.layout.favourites_book_item,parent,false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        final Book book=books.get(position);
        holder.Bind(book);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent_detail=new Intent(mContext, BookDetailActivity.class);
                 intent_detail.putExtra(pConstants.BOOK_KEY,book);
                 //intent_detail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent_detail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        private TextView bkTitle,tvAuthors,tvRating;
        private ImageView tvImage;
        private KenBurnsView kn;
        //private TextView tvDate,tvPublisher;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            kn=(KenBurnsView)itemView.findViewById(R.id.foto_id);
            bkTitle=(TextView)itemView.findViewById(R.id.book_title);
            //tvAuthors=(TextView)itemView.findViewById(R.id.tvAuthors);
            //tvRating=(TextView)itemView.findViewById(R.id.tvRating);
            //tvImage=(ImageView)itemView.findViewById(R.id.tvImage);
            //tvPublisher=(TextView)itemView.findViewById(R.id.tvpublisher);
            //tvDate=(TextView)itemView.findViewById(R.id.tvpublishedDate);
        }

        public void Bind(Book book){
            /*
            String []auths=book.getAuthors();
            int auth_size=auths.length;

            if(auth_size==1){
                tvAuthors.setText(auths[0]);
            }else if(auth_size==2){
                tvAuthors.setText(auths[0]+", "+auths[1]);
            }else if(auth_size==3){
                tvAuthors.setText(auths[0]+", "+auths[1] + ", "+auths[2]);
            }else{
                if(auth_size>0 && auth_size>3){
                    tvAuthors.setText(auths[0]);
                }
            }

            //bvTitle.setText(book.getTitle());
            tvRating.setText(book.getAverageRating());
            //tvAuthors.setText(sb.toString());
            //tvPublisher.setText(book.getPublisher());
            //tvDate.setText(book.getPublishedDate());
            ///inflate image here
            */
            bkTitle.setText(book.getTitle());
            Picasso.get().load(book.getThumbnail()).placeholder(R.drawable.ic_book).into(kn);

        }

    }//end of BookView

}
