package com.edufree.bookshoot;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.edufree.bookshoot.Pagers.bookDetailFragmentPager;
import com.edufree.bookshoot.database.BookContract;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Comment;
import com.edufree.bookshoot.models.Stats;
import com.edufree.bookshoot.utils.pConstants;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class BookDetailActivity extends AppCompatActivity {
    private static final String TAG="BookDetailActivity";
    private TextView mTitle,mSubtitle,mAuthors;
    private RatingBar mRating;
    private ImageView imgCover,imgLike;
    private Book mBook=null;
    private ArrayList<Comment> mBookCommentsList=new ArrayList<>();
    private boolean hasChildCommentNodes=false;
    private Stats mBookStats=new Stats();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail2);

        mBook = getIntent().getParcelableExtra(pConstants.BOOK_KEY);
        if(mBook!=null){
            initializeData();
            checkForReviews();
            checkForBookStats();
            checkIfBookIsLiked(mBook);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        bookDetailFragmentPager adapter=new bookDetailFragmentPager(getSupportFragmentManager(),getApplicationContext(),mBook,hasChildCommentNodes,mBookStats);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }//end of onCreate...

    private void initializeData() {
        mTitle=(TextView)findViewById(R.id.tvTitle_detail);
        mAuthors=(TextView)findViewById(R.id.tvAuthors_detail);
        mRating=(RatingBar) findViewById(R.id.mm_rating_of_bookDetails);
        imgCover=(ImageView)findViewById(R.id.tvImage_detail);
        imgLike=(ImageView)findViewById(R.id.like_book_id);

        mTitle.setText(mBook.getTitle());

        if(mBook.getAverageRating().isEmpty() || mBook.getAverageRating()==null){

        }else{
            float rating= Float.parseFloat(mBook.getAverageRating());
            mRating.setRating(rating);
        }

        String []auths=mBook.getAuthors();

        if(auths!=null){
            //String []auths=mBook.getAuthors();
            int auth_size=auths.length;

            if(auth_size==1){
                mAuthors.setText(auths[0]);
            }else if(auth_size==2){
                mAuthors.setText(auths[0]+", "+auths[1]);
            }else if(auth_size==3){
                mAuthors.setText(auths[0]+", "+auths[1] + ", "+auths[2]);
            }else{
                if(auth_size>0 && auth_size>3){
                    mAuthors.setText(auths[0]);
                }
            }

        }

        //setting the image
        if(!mBook.getThumbnail().isEmpty() && mBook.getThumbnail()!=null){
            Picasso.get().load(mBook.getThumbnail()).placeholder(R.drawable.ic_book).into(imgCover);
        }else{
            imgCover.setImageResource(R.drawable.ic_book);
        }

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:Save Book yeah....
                saveLikedBook(mBook);
                imgLike.setImageResource(R.drawable.ic_like_pink);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.sample_id){
            Toast.makeText(getApplicationContext(),"working nicely yeah",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*****************************************************************************************************************/
    /*****************************************************************************************************************/
    private void checkForReviews(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("books").child(mBook.getIsbn());
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    // The child doesn't exist
                    Log.d(TAG,"create new values here yeah ");
                    hasChildCommentNodes=false;
                    RIP_ALL_COMMENTS();
                }else{
                    Log.d(TAG,"child already exists ************");
                    hasChildCommentNodes=true;
                    getBookComments(mBook);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkForBookStats(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("stats").child(mBook.getIsbn());
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    // The child doesn't exist
                    Log.d(TAG,"No values dude ");
                    RIP_ALL_STATS();
                }else{
                    Log.d(TAG,"child already exists ************");
                    hasChildCommentNodes=true;
                    getBookStats(mBook);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RIP_ALL_STATS() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("stats").child(mBook.getIsbn());

        Stats newBookStat=new Stats();
        newBookStat.setNum_reading(getReading());
        newBookStat.setNum_likes(getLikes());
        newBookStat.setNum_dislikes(getDislikes());

        String id=myRef.push().getKey();
        newBookStat.setId(id);
        myRef.child(id).setValue(newBookStat);
        getBookStats(mBook);

    }

    private void getBookStats(Book book) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("stats").child(book.getIsbn());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mBookStats=snapshot.getValue(Stats.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getDislikes() {
        int max=400;
        int min=50;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return String.valueOf(randomNum);
    }

    private String getLikes() {
        int max=10000;
        int min=99;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return String.valueOf(randomNum);
    }

    private String getReading() {
        int max=36000;
        int min=5000;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return String.valueOf(randomNum);
    }


    private void RIP_ALL_COMMENTS() {
        final ArrayList<Comment> mCommentList=new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("comments");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCommentList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment=snapshot.getValue(Comment.class);
                    if(comment!=null){
                        mCommentList.add(comment);
                    }
                }
                //call JOHN ..its his time now
                jOhN_thE_RiPpeR(mCommentList,mBook);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void jOhN_thE_RiPpeR(ArrayList<Comment> mList,Book book){
        ArrayList<Comment> trimmedList=new ArrayList<>();
        ArrayList<Comment> playingList=mList;

        for(int a=0;a<20;a++){
            Comment chosenComment=playingList.get(new Random().nextInt(playingList.size()));
            trimmedList.add(chosenComment);
            playingList.remove(chosenComment);
        }

        Log.d(TAG,"Finished L getting the new 20 Books yeah");

        for(Comment comment:trimmedList){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("books").child(book.getIsbn()).child(comment.getId());
            myRef.setValue(comment);
        }
        getBookComments(book);
    }

    private void getBookComments(Book book){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("books").child(book.getIsbn());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBookCommentsList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment=snapshot.getValue(Comment.class);
                    if(comment!=null){
                        mBookCommentsList.add(comment);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**************************************************************************************************************/
    /****************************************************************************************************************/
    /****************************************SQL DATABASE SHIT YEAH....*******************************************/
    /*******************************************************************************************************************/
    /******************************************************************************************************************/
    private void saveLikedBook(Book book){
        ArrayList<Book> newRecos=new ArrayList<>();
        ArrayList<Book> likedBooks=getLikedBooks();

        if(true){
            Boolean isAvailable=false;
            for(Book books:likedBooks){
                if(books.getId()==book.getId()){
                    isAvailable=true;
                }
            }

            if(!isAvailable){
                newRecos.add(book);
            }

        }

        ///Save the Books into the SQL_lite Database yeah........
        for(Book c_book:newRecos){
            insertLikedBook(c_book);
        }

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
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_ID, book_id);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_ISBN, book_isbn);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_TITLE, book_title);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_SUBTITLE, book_subtitle);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHER, book_publisher);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE, book_publishedDate);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_DESCRIPTION, book_description);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_THUMBNAIL, book_thumbnail);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING, book_averageRating);


        Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Uri uri= BookContract.SavedBooksEntry.CONTENT_URI;
        Uri uriRowInserted=getContentResolver().insert(uri,contentValues);
        Log.i(TAG, "From Content Provider yeah *** : " + uriRowInserted);
    }
    private ArrayList<Book> getLikedBooks(){
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

		/*
		Cursor cursor = database.query(NationEntry.TABLE_NAME,		// The table name
				projection,                 // The columns to return
				selection,                  // Selection: WHERE clause OR the condition
				selectionArgs,              // Selection Arguments for the WHERE clause
				null,                       // don't group the rows
				null,                       // don't filter by row groups
				sortOrder);					// The sort order


		 */

        Uri uri= BookContract.SavedBooksEntry.CONTENT_URI;
        Cursor cursor=getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);

        Log.d(TAG, "From Content Provider >>>> CURSOR OBJECT YEAH " );

        if (cursor != null) {

            int indexID=cursor.getColumnIndex(BookContract.SavedBooksEntry._ID);
            int index_Book_id=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_ID);
            int index_Book_isbn=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_ISBN);
            int index_Book_title=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_TITLE);
            int index_Book_subtitle=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_SUBTITLE);
            int index_Book_publisher=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHER);
            int index_Book_publishedDate=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE);
            int index_Book_description=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_DESCRIPTION);
            int index_Book_thumbnail=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_THUMBNAIL);
            int index_Book_averageRating=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING);

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

    private void checkIfBookIsLiked(Book book){
        ArrayList<Book> likedBooks=getLikedBooks();
        Boolean isLiked=false;

        for(Book l_book:likedBooks){
            if(l_book.getId().matches(mBook.getId())){
                isLiked=true;
            }
        }

        if(isLiked){
            imgLike.setImageResource(R.drawable.ic_like_pink);
        }

    }


}
