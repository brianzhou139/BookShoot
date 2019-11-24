package com.edufree.bookshoot.pagerFragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edufree.bookshoot.R;
import com.edufree.bookshoot.adapters.commentAdapter;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Comment;
import com.edufree.bookshoot.utils.pConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewsFragment extends Fragment {
    private View mViewReview;
    private Book currentBook=null;
    private ArrayList<Comment> mCommentList=new ArrayList<>();
    private boolean hasChildCommentNodes=false;

    private RecyclerView recyler;
    private commentAdapter adapter;

    private TextView review_numReading,review_numLikes,review_numDisLikes,seeAllReviews;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        // Inflate the layout for this fragment
        mViewReview = inflater.inflate(R.layout.fragment_reviews_paged, container, false);

        recyler=(RecyclerView)mViewReview.findViewById(R.id.rv_frag);
        recyler.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getArguments();
        currentBook = args.getParcelable(pConstants.BOOK_FRAGMENT_KEY);
        hasChildCommentNodes=args.getBoolean(pConstants.COMMENTS_KEY);

        if(currentBook!=null){

        }

        getBookComments(currentBook);

        return mViewReview;
    }


    private void initViews(){
        review_numReading=(TextView)mViewReview.findViewById(R.id.review_numReading);
        review_numLikes=(TextView)mViewReview.findViewById(R.id.review_numLikes);
        review_numDisLikes=(TextView)mViewReview.findViewById(R.id.review_numDisLikes);
        seeAllReviews=(TextView)mViewReview.findViewById(R.id.seeReviews_text);

        seeAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : showAllReviews yeah........

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
}
