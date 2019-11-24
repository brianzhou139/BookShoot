package com.edufree.bookshoot;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.edufree.bookshoot.Pagers.bookDetailFragmentPager;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Comment;
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
    private ImageView imgCover;
    private Book mBook=null;
    private ArrayList<Comment> mBookCommentsList=new ArrayList<>();
    private boolean hasChildCommentNodes=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail2);

        mBook = getIntent().getParcelableExtra(pConstants.BOOK_KEY);
        if(mBook!=null){
            initializeData();
            checkForReviews();
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        bookDetailFragmentPager adapter=new bookDetailFragmentPager(getSupportFragmentManager(),getApplicationContext(),mBook,hasChildCommentNodes);

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

    }//end of readUsers...

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

}
