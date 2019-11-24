package com.edufree.bookshoot.adapters;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.edufree.bookshoot.BookDetailActivity;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.utils.pConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private static final String TAG="BooksAdapter";
    private Context mContext;
    private ArrayList<Book> books;

    public BooksAdapter(Context context, ArrayList<Book> books) {
        mContext = context;
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(mContext).inflate(R.layout.book_list_item2,parent,false);
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

        private TextView tvTitle,tvAuthors;
        private RatingBar tvRating;
        private ImageView tvImage;
        //private TextView tvDate,tvPublisher;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.tvTitle);
            tvAuthors=(TextView)itemView.findViewById(R.id.tvAuthors);
            tvRating=(RatingBar) itemView.findViewById(R.id.mm_rating_of_book1_item);
            tvImage=(ImageView)itemView.findViewById(R.id.tvImage);
            //tvPublisher=(TextView)itemView.findViewById(R.id.tvpublisher);
            //tvDate=(TextView)itemView.findViewById(R.id.tvpublishedDate);
        }

        public void Bind(Book book){
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

            tvTitle.setText(book.getTitle());

            ///check to see if theres is a rating
            if(book.getAverageRating().isEmpty() || book.getAverageRating()==null){

            }else{
                float rating= Float.parseFloat(book.getAverageRating());
                tvRating.setRating(rating);
                Log.d(TAG,"###################################");
                Log.d(TAG,"title : "+ book.getTitle());
                Log.d(TAG,"rating : "+ book.getAverageRating());
            }

            //tvAuthors.setText(sb.toString());
            //tvPublisher.setText(book.getPublisher());
            //tvDate.setText(book.getPublishedDate());
            ///inflate image here
            if(book.getThumbnail().isEmpty() || book.getThumbnail()==null){
                tvImage.setImageResource(R.drawable.ic_book);
            }else{
                Picasso.get().load(book.getThumbnail()).placeholder(R.drawable.ic_book).into(tvImage);
            }

        }

    }//end of BookView

}
