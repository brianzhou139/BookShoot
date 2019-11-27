package com.edufree.bookshoot;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.edufree.bookshoot.adapters.commentAdapter;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Comment;
import com.edufree.bookshoot.utils.pConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.edufree.bookshoot.utils.pConstants.*;

public class ClickedReviewsActivity extends AppCompatActivity {

    /***************************************************************************************************************/
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.edufree.bookshoot";
    /***************************************************************************************************************/
    /***************************************************************************************************************/

    private View mViewReview;
    private Book currentBook=null;
    private ArrayList<Comment> mCommentList=new ArrayList<>();
    private boolean hasChildCommentNodes=false;

    private RecyclerView recyler;
    private commentAdapter adapter;
    private String mUsername=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_reviews);

        recyler=(RecyclerView)findViewById(R.id.rv_clicked_reviews_id);
        recyler.setLayoutManager(new LinearLayoutManager(this));

        currentBook =getIntent().getParcelableExtra(BOOK_REVIEWS_KEY);
        //hasChildCommentNodes=getIntent().getBooleanExtra(COMMENTS_KEY);
        //initialize the shared Preferances
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        // Restore preferences
        mUsername=mPreferences.getString(SHARED_PREF_USERNAME,null);

        if(currentBook!=null){
            getBookComments(currentBook);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReview();
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(SHARED_PREF_USERNAME,mUsername);
        preferencesEditor.apply();
        // ...
    }

    private void getBookComments(Book book){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("books").child(book.getIsbn());

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

                adapter=new commentAdapter(getApplicationContext(),mCommentList);
                recyler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addReview(){
        final AlertDialog.Builder myDialog=new AlertDialog.Builder(this);
        LayoutInflater mInflater=LayoutInflater.from(this);
        View myView=mInflater.inflate(R.layout.add_review,null);
        myDialog.setView(myView);

        final EditText user_name=myView.findViewById(R.id.add_username_id);
        final EditText user_review=myView.findViewById(R.id.add_user_review_id);
        final EditText user_rating=myView.findViewById(R.id.add_rating_id);
        Button btnSave_ref=myView.findViewById(R.id.mySave_create_id);

        if(mUsername!=null){
            user_name.setText(mUsername);
            user_name.clearFocus();
            user_name.setEnabled(false);
        }

        final AlertDialog dialog=myDialog.create();

        btnSave_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsername = user_name.getText().toString().trim();
                String review=user_review.getText().toString().trim();
                String rating=user_rating.getText().toString().trim();

                if(TextUtils.isEmpty(mUsername)){
                    user_name.setError("required field");
                    return;
                }

                if(TextUtils.isEmpty(review)){
                    user_review.setError("required field");
                    return;
                }

                if(TextUtils.isEmpty(rating)){
                    user_rating.setError("required field");
                    return;
                }

                Comment newReview=new Comment(true);
                newReview.setContent(review);
                newReview.setRating(rating);
                newReview.setEmail("unknown");
                newReview.setUsername(mUsername);


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("books").child(currentBook.getIsbn());


                String id=myRef.push().getKey();
                newReview.setId(id);

                myRef.child(id).setValue(newReview)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //check to see if task is successful
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Reviews added",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"failed to add Comment",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });///end of Save Button Click by user

        dialog.show();

    }//end of addData method yeah...

}
