package com.edufree.bookshoot.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.edufree.bookshoot.database.BookContract;
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
                saveClickedBook(book);
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

            if(book.getAuthors()!=null){
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

    }//end of BookView ........

    private void saveClickedBook(Book book){
        ArrayList<Book> newRecos=new ArrayList<>();
        ArrayList<Book> clickedBooks=getClickedBooks();

        if(true){
            Boolean isAvailable=false;
            for(Book books:clickedBooks){

                if(books.getId().matches(book.getId())){
                    isAvailable=true;
                }
            }

            if(!isAvailable){
                newRecos.add(book);
            }

        }

        ///Save the Books into the SQL_lite Database yeah........
        for(Book c_book:newRecos){
            insertClickedBook(c_book);
        }

    }
    private ArrayList<Book> getClickedBooks(){
        ArrayList<Book> likedBooks=new ArrayList<>();

        String[] projection = {
                BookContract.SavedBooksEntry._ID,
                BookContract.SavedBooksEntry.COLUMN_BOOK_ID,
                BookContract.SavedBooksEntry.COLUMN_BOOK_ISBN,
                BookContract.SavedBooksEntry.COLUMN_BOOK_TITLE,
                BookContract.SavedBooksEntry.COLUMN_BOOK_SUBTITLE,
                BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHER,
                BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE,
                BookContract.SavedBooksEntry.COLUMN_BOOK_DESCRIPTION,
                BookContract.SavedBooksEntry.COLUMN_BOOK_AUTHOR,
                BookContract.SavedBooksEntry.COLUMN_BOOK_AUTHOR2,
                BookContract.SavedBooksEntry.COLUMN_BOOK_THUMBNAIL,
                BookContract.SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING
        };

        // Filter results. Make these null if you want to query all rows
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;	// Ascending or Descending ...


        Uri uri= BookContract.ClickedBooksEntry.CONTENT_URI;
        Cursor cursor=mContext.getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);

        Log.d(TAG, "From Content Provider >>>> CURSOR OBJECT YEAH " );

        if (cursor != null) {
            int indexID=cursor.getColumnIndex(BookContract.ClickedBooksEntry._ID);
            int index_Book_id=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_ID);
            int index_Book_isbn=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_ISBN);
            int index_Book_title=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_TITLE);
            int index_Book_subtitle=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_SUBTITLE);
            int index_Book_publisher=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_PUBLISHER);
            int index_Book_publishedDate=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE);
            int index_Book_description=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_DESCRIPTION);
            int index_Book_thumbnail=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_THUMBNAIL);
            int index_Book_averageRating=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_AVERAGE_RATING);
            int index_Book_author=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_AUTHOR);
            int index_Book_author2=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_AUTHOR);

            while (cursor.moveToNext()) {	// Cursor iterates through all rows
                //String cont=cursor.getString(indexCountry);
                //Log.d("TITO","******* cont : "+ cont);
                String book_id=cursor.getString(index_Book_id);
                String book_isbn=cursor.getString(index_Book_isbn);
                String book_title=cursor.getString(index_Book_title);
                String book_subtitle=cursor.getString(index_Book_subtitle);
                String book_publisher=cursor.getString(index_Book_publisher);
                String book_publishedDate=cursor.getString(index_Book_publishedDate);
                String book_description=cursor.getString(index_Book_description);
                String book_thumbnail=cursor.getString(index_Book_thumbnail);
                String book_averageRating=cursor.getString(index_Book_averageRating);
                String book_author=cursor.getString(index_Book_author);
                String book_author2=cursor.getString(index_Book_author2);

                String[] authors = new String[2];
                if(book_author!=null && book_author2!=null){
                    authors= new String[]{book_author, book_author2};
                }else if(book_author!=null && book_author2==null){
                    authors= new String[]{book_author,""};
                }else if(book_author==null && book_author2!=null){
                    authors= new String[]{"",book_author2};
                }else{
                    authors= new String[]{"",""};
                }

                Book mNewBook=new Book();
                mNewBook.setId(book_id);
                mNewBook.setIsbn(book_isbn);
                mNewBook.setTitle(book_title);
                mNewBook.setSubtitle(book_subtitle);
                mNewBook.setPublisher(book_publisher);
                mNewBook.setPublishedDate(book_publishedDate);
                mNewBook.setDescription(book_description);
                mNewBook.setThumbnail(book_thumbnail);
                mNewBook.setAverageRating(book_averageRating);
                mNewBook.setAuthors(authors);

                Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@_from_ClickedBooks _table_table");
                Log.i(TAG, "clicked_id : " +mNewBook.getId());
                Log.i(TAG, "clicked_isbn : " +mNewBook.getIsbn());
                Log.i(TAG, "clicked_title : " +mNewBook.getTitle());
                Log.i(TAG, "clicked_publisher : " +mNewBook.getPublisher());
                Log.i(TAG, "clicked_publisher_date : " +mNewBook.getPublishedDate());
                Log.i(TAG, "clicked_description : " +mNewBook.getDescription());
                Log.i(TAG, "clicked_thumbnail : " +mNewBook.getThumbnail());
                Log.i(TAG, "clicked_averageRating : " +mNewBook.getAverageRating());
                likedBooks.add(mNewBook);

            }

            cursor.close();
            Log.i(TAG, "Im DOne yeah");

        }
        return likedBooks;
    }
    private void insertClickedBook(Book mBook){
        String book_author=null;
        String book_author2=null;

        String []auths=mBook.getAuthors();
        if(auths!=null){
            int auth_size=auths.length;

            if(auth_size==1){
                book_author=auths[0];
            }else if(auth_size==2){
                book_author2=auths[1];
            }else{
                if(auth_size>0 && auth_size>3){
                    book_author=auths[0];
                    book_author2=auths[1];
                }
            }

        }

        String book_id=mBook.getId();
        String book_isbn=mBook.getIsbn();
        String book_title=mBook.getTitle();
        String book_subtitle=mBook.getSubtitle();
        String book_publisher=mBook.getPublisher();
        String book_publishedDate=mBook.getPublishedDate();
        String book_description=mBook.getDescription();
        String book_thumbnail=mBook.getThumbnail();
        String book_averageRating=mBook.getAverageRating();
        ContentValues contentValues = new ContentValues();

        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_ID, book_id);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_ISBN, book_isbn);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_TITLE, book_title);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_SUBTITLE, book_subtitle);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHER, book_publisher);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE, book_publishedDate);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_DESCRIPTION, book_description);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_THUMBNAIL, book_thumbnail);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING, book_averageRating);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_AUTHOR,book_author);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_AUTHOR2,book_author2);

        Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@_clicked_book_insert_yeah____________>");
        Uri uri= BookContract.ClickedBooksEntry.CONTENT_URI;
        Uri uriRowInserted=mContext.getContentResolver().insert(uri,contentValues);
        Log.i(TAG, "From Content Provider yeah**Insert_Clicked_Book : " + uriRowInserted);
    }

}
