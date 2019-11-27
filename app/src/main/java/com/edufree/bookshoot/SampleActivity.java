package com.edufree.bookshoot;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.hardware.camera2.params.RecommendedStreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.edufree.bookshoot.database.BookContract;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.database.BookContract.SavedBooksEntry;
import com.edufree.bookshoot.shitData.fakeData;

import java.util.ArrayList;

public class SampleActivity extends AppCompatActivity {
    private static final String TAG="SampleActivity";
    private ArrayList<Book> mBooks=new ArrayList<>();
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        mBooks= fakeData.getShitBooksForDB_LIKED();
        //mBooks=fakeData.getShitBooksForDB_SEARCHED();
        //mBooks=fakeData.getShitBooksForDB_RECOMMENDED();

        btnSave=(Button)findViewById(R.id.saved_id);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //insertLikedBook(mBooks.get(2));
                //ArrayList<Book> books=getLikedBooks();
                //insertClickedBook(mBooks.get(0));
                //ArrayList<Book> books=getClickedBooks();
                //insertRecommendedBook(mBooks.get(1));
                //ArrayList<Book> books=getRecommendedBooks();
            }
        });

    }

    private void insertLikedBook(Book mBook){
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
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_ID, book_id);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_ISBN, book_isbn);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_TITLE, book_title);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_SUBTITLE, book_subtitle);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_PUBLISHER, book_publisher);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE, book_publishedDate);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_DESCRIPTION, book_description);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_THUMBNAIL, book_thumbnail);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING, book_averageRating);


        Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Uri uri=SavedBooksEntry.CONTENT_URI;
        Uri uriRowInserted=getContentResolver().insert(uri,contentValues);
        Log.i(TAG, "From Content Provider yeah *** : " + uriRowInserted);
    }
    private ArrayList<Book> getLikedBooks(){
        ArrayList<Book> likedBooks=new ArrayList<>();

        String[] projection = {
                SavedBooksEntry._ID,
                SavedBooksEntry.COLUMN_BOOK_ID,
                SavedBooksEntry.COLUMN_BOOK_ISBN,
                SavedBooksEntry.COLUMN_BOOK_TITLE,
                SavedBooksEntry.COLUMN_BOOK_SUBTITLE,
                SavedBooksEntry.COLUMN_BOOK_PUBLISHER,
                SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE,
                SavedBooksEntry.COLUMN_BOOK_DESCRIPTION,
                SavedBooksEntry.COLUMN_BOOK_AUTHOR,
                SavedBooksEntry.COLUMN_BOOK_AUTHOR2,
                SavedBooksEntry.COLUMN_BOOK_THUMBNAIL,
                SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING
        };

        // Filter results. Make these null if you want to query all rows
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;	// Ascending or Descending ...

		/*
		Cursor cursor = database.query(NationEntry.TABLE_NAME,		// The table name
				projection,                 // The columns to return
				selection,                  // Selection: WHERE clause OR the condition
				selectionArgs,              // Selection Arguments for the WHERE clause
				null,                       // don't group the rows
				null,                       // don't filter by row groups
				sortOrder);					// The sort order


		 */

        Uri uri=SavedBooksEntry.CONTENT_URI;
        Cursor cursor=getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);

        Log.d(TAG, "From Content Provider >>>> CURSOR OBJECT YEAH " );

        if (cursor != null) {

            int indexID=cursor.getColumnIndex(SavedBooksEntry._ID);
            int index_Book_id=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_ID);
            int index_Book_isbn=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_ISBN);
            int index_Book_title=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_TITLE);
            int index_Book_subtitle=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_SUBTITLE);
            int index_Book_publisher=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_PUBLISHER);
            int index_Book_publishedDate=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE);
            int index_Book_description=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_DESCRIPTION);
            int index_Book_thumbnail=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_THUMBNAIL);
            int index_Book_averageRating=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING);

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


                Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@_book_retrivial");
                Log.i(TAG, "id : " +mNewBook.getId());
                Log.i(TAG, "isbn : " +mNewBook.getIsbn());
                Log.i(TAG, "title : " +mNewBook.getTitle());
                Log.i(TAG, "publisher : " +mNewBook.getPublisher());
                Log.i(TAG, "publisher_date : " +mNewBook.getPublishedDate());
                Log.i(TAG, "description : " +mNewBook.getDescription());
                Log.i(TAG, "thumbnail : " +mNewBook.getThumbnail());
                Log.i(TAG, "averageRating : " +mNewBook.getAverageRating());
                likedBooks.add(mNewBook);
            }

            cursor.close();
            Log.i(TAG, "Im DOne yeah");
        }
        return likedBooks;
    }

    /**********************************************************************************************/
    /******************************CLICKED BOOKS*********************************/
    /*********************************************************************************************/
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
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_ID, book_id);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_ISBN, book_isbn);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_TITLE, book_title);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_SUBTITLE, book_subtitle);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_PUBLISHER, book_publisher);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE, book_publishedDate);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_DESCRIPTION, book_description);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_THUMBNAIL, book_thumbnail);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING, book_averageRating);


        Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@_clicked_book_insert_yeah");
        Uri uri= BookContract.ClickedBooksEntry.CONTENT_URI;
        Uri uriRowInserted=getContentResolver().insert(uri,contentValues);
        Log.i(TAG, "From Content Provider yeah**Insert_Clicked_Book : " + uriRowInserted);
    }
    private ArrayList<Book> getClickedBooks(){
        ArrayList<Book> likedBooks=new ArrayList<>();

        String[] projection = {
                SavedBooksEntry._ID,
                SavedBooksEntry.COLUMN_BOOK_ID,
                SavedBooksEntry.COLUMN_BOOK_ISBN,
                SavedBooksEntry.COLUMN_BOOK_TITLE,
                SavedBooksEntry.COLUMN_BOOK_SUBTITLE,
                SavedBooksEntry.COLUMN_BOOK_PUBLISHER,
                SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE,
                SavedBooksEntry.COLUMN_BOOK_DESCRIPTION,
                SavedBooksEntry.COLUMN_BOOK_AUTHOR,
                SavedBooksEntry.COLUMN_BOOK_AUTHOR2,
                SavedBooksEntry.COLUMN_BOOK_THUMBNAIL,
                SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING
        };

        // Filter results. Make these null if you want to query all rows
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;	// Ascending or Descending ...


        Uri uri= BookContract.ClickedBooksEntry.CONTENT_URI;
        Cursor cursor=getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);

        Log.d(TAG, "From Content Provider >>>> CURSOR OBJECT YEAH " );

        if (cursor != null) {

            int indexID=cursor.getColumnIndex(SavedBooksEntry._ID);
            int index_Book_id=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_ID);
            int index_Book_isbn=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_ISBN);
            int index_Book_title=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_TITLE);
            int index_Book_subtitle=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_SUBTITLE);
            int index_Book_publisher=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_PUBLISHER);
            int index_Book_publishedDate=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE);
            int index_Book_description=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_DESCRIPTION);
            int index_Book_thumbnail=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_THUMBNAIL);
            int index_Book_averageRating=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING);

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
    /**********************************************************************************************/
    /****************************************RECOMMENDED BOOKS****************************************************/
    /*********************************************************************************************/
    private void insertRecommendedBook(Book mBook){
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
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_ID, book_id);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_ISBN, book_isbn);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_TITLE, book_title);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_SUBTITLE, book_subtitle);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_PUBLISHER, book_publisher);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE, book_publishedDate);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_DESCRIPTION, book_description);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_THUMBNAIL, book_thumbnail);
        contentValues.put(SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING, book_averageRating);


        Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@_recommended_insert_yeah");
        Uri uri= BookContract.RecommendedBooksEntry.CONTENT_URI;
        Uri uriRowInserted=getContentResolver().insert(uri,contentValues);
        Log.i(TAG, "From Content Provider yeah ** Insert_Recommended____ Books : " + uriRowInserted);
    }
    private ArrayList<Book> getRecommendedBooks(){
        ArrayList<Book> likedBooks=new ArrayList<>();

        String[] projection = {
                SavedBooksEntry._ID,
                SavedBooksEntry.COLUMN_BOOK_ID,
                SavedBooksEntry.COLUMN_BOOK_ISBN,
                SavedBooksEntry.COLUMN_BOOK_TITLE,
                SavedBooksEntry.COLUMN_BOOK_SUBTITLE,
                SavedBooksEntry.COLUMN_BOOK_PUBLISHER,
                SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE,
                SavedBooksEntry.COLUMN_BOOK_DESCRIPTION,
                SavedBooksEntry.COLUMN_BOOK_AUTHOR,
                SavedBooksEntry.COLUMN_BOOK_AUTHOR2,
                SavedBooksEntry.COLUMN_BOOK_THUMBNAIL,
                SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING
        };

        // Filter results. Make these null if you want to query all rows
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;	// Ascending or Descending ...

        Uri uri= BookContract.RecommendedBooksEntry.CONTENT_URI;
        Cursor cursor=getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);

        Log.d(TAG, "From Content Provider >>>> CURSOR OBJECT YEAH " );

        if (cursor != null) {

            int indexID=cursor.getColumnIndex(SavedBooksEntry._ID);
            int index_Book_id=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_ID);
            int index_Book_isbn=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_ISBN);
            int index_Book_title=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_TITLE);
            int index_Book_subtitle=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_SUBTITLE);
            int index_Book_publisher=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_PUBLISHER);
            int index_Book_publishedDate=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE);
            int index_Book_description=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_DESCRIPTION);
            int index_Book_thumbnail=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_THUMBNAIL);
            int index_Book_averageRating=cursor.getColumnIndex(SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING);

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

                Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@_from_Recommended_Books");
                Log.i(TAG,"Recommended_books");
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
            Log.i(TAG, "Im DOne yeah__Recommended_Books");
        }
        return likedBooks;
    }



}
