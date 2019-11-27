package com.edufree.bookshoot.pagerFragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edufree.bookshoot.ClickedReviewsActivity;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.adapters.commentAdapter;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Comment;
import com.edufree.bookshoot.models.Stats;
import com.edufree.bookshoot.utils.pConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.edufree.bookshoot.utils.pConstants.SHARED_PREF_USERNAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewsFragment extends Fragment {
    /***************************************************************************************************************/
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.edufree.bookshoot";
    /***************************************************************************************************************/
    /***************************************************************************************************************/

    private View mViewReview;
    private Book currentBook=null;
    private Stats bookStats=null;
    private ArrayList<Comment> mCommentList=new ArrayList<>();
    private boolean hasChildCommentNodes=false;

    private RecyclerView recyler;
    private commentAdapter adapter;

    private TextView review_numReading,review_numLikes,review_numDisLikes,seeAllReviews;
    private LinearLayout addReviews;
    private String mUsername=null;
    private ImageView btnLike,btnDislike;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        // Inflate the layout for this fragment
        mViewReview = inflater.inflate(R.layout.fragment_reviews_paged, container, false);
        initViews();
        //hasChildCommentNodes=getIntent().getBooleanExtra(COMMENTS_KEY);
        //initialize the shared Preferances
        mPreferences = getActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        // Restore preferences
        mUsername=mPreferences.getString(SHARED_PREF_USERNAME,null);

        recyler=(RecyclerView)mViewReview.findViewById(R.id.rv_frag);
        recyler.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getArguments();
        currentBook = args.getParcelable(pConstants.BOOK_FRAGMENT_KEY);

        hasChildCommentNodes=args.getBoolean(pConstants.COMMENTS_KEY);

        if(currentBook!=null){
            getBookStats(currentBook);
        }

        getBookComments(currentBook);

        return mViewReview;
    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(SHARED_PREF_USERNAME,mUsername);
        preferencesEditor.apply();
        // ...
    }

    private void initViews(){
        review_numReading=(TextView)mViewReview.findViewById(R.id.review_numReading);
        review_numLikes=(TextView)mViewReview.findViewById(R.id.review_numLikes);
        review_numDisLikes=(TextView)mViewReview.findViewById(R.id.review_numDisLikes);
        seeAllReviews=(TextView)mViewReview.findViewById(R.id.seeReviews_text);
        addReviews=(LinearLayout)mViewReview.findViewById(R.id.add_review_linear_id);

        btnLike=(ImageView)mViewReview.findViewById(R.id.like_id);
        btnDislike=(ImageView)mViewReview.findViewById(R.id.dislike_id);


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_value= Integer.parseInt(bookStats.getNum_dislikes());
                current_value++;
                bookStats.setNum_likes(String.valueOf(current_value));
                SaveTick(bookStats);
            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_value= Integer.parseInt(bookStats.getNum_dislikes());
                current_value++;
                bookStats.setNum_dislikes(String.valueOf(current_value));
                SaveTick(bookStats);
            }
        });

        seeAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : showAllReviews yeah........
                Intent i=new Intent(getActivity(), ClickedReviewsActivity.class);
                i.putExtra(pConstants.BOOK_REVIEWS_KEY,currentBook);
                getActivity().startActivity(i);

            }
        });

        addReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReview();
            }
        });
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

                adapter=new commentAdapter(getContext(),mCommentList);
                recyler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addReview(){
        final AlertDialog.Builder myDialog=new AlertDialog.Builder(getActivity());
        LayoutInflater mInflater=LayoutInflater.from(getActivity());
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
                                    Toast.makeText(getActivity(),"Reviews added",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getActivity(),"failed to add Comment",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });///end of Save Button Click by user

        dialog.show();

    }//end of addData method yeah...


    private void getBookStats(Book book) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("stats").child(book.getIsbn());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    bookStats=snapshot.getValue(Stats.class);
                    review_numReading.setText(bookStats.getNum_reading());
                    review_numLikes.setText(bookStats.getNum_likes());
                    review_numDisLikes.setText(bookStats.getNum_dislikes());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SaveTick(Stats stat) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("stats").child(currentBook.getIsbn());
        myRef.child(stat.getId()).setValue(stat);
    }

}




