package com.edufree.bookshoot.ui.favourites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edufree.bookshoot.BookDetailActivity;
import com.edufree.bookshoot.Loaders.googleApiBookLoader;
import com.edufree.bookshoot.NewMenuActivity;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.SearchActivity;
import com.edufree.bookshoot.adapters.BooksAdapter;
import com.edufree.bookshoot.adapters.FavouritesAdapter;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Search;
import com.edufree.bookshoot.shitData.fakeData;
import com.edufree.bookshoot.utils.networkUtils;
import com.edufree.bookshoot.utils.pConstants;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class FavouritesFragment extends Fragment{

    private View mRoot;
    private AlertDialog dialog;


    private ArrayList<Book> mSavedBooks=new ArrayList<>();
    private ArrayList<Book> mOftenSearchedBooks=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_favourites, container, false);

        getSavedBooksFromDatabase();
        getOftenSearchedBooksFromDatabase();
        initMajor();
        initFirstViewAndSetUP(mSavedBooks);
        initSecondViewAndSetUP(mOftenSearchedBooks);
        return mRoot;
    }



    private void getSavedBooksFromDatabase() {
        mSavedBooks=fakeData.getShitBooksSamples();
    }

    private void getOftenSearchedBooksFromDatabase() {
        mOftenSearchedBooks=fakeData.getShitBooksSamples();
    }

    private void initMajor(){

        CardView app_icon=(CardView)mRoot.findViewById(R.id.app_icon);
        CardView usePhoto=(CardView)mRoot.findViewById(R.id.usePhoto_id);

        ImageView search_image=(ImageView)mRoot.findViewById(R.id.search_2_id);
        final EditText user_input=(EditText)mRoot.findViewById(R.id.user_input_id);
        user_input.setCursorVisible(false);

        app_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: do something here titus
                Toast.makeText(getActivity(),"you clicked the app icon",Toast.LENGTH_SHORT).show();
            }
        });

        usePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///TODO : do something here titus yeah
                Toast.makeText(getActivity(),"use Photo",Toast.LENGTH_SHORT).show();
            }
        });

        user_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_input.setCursorVisible(true);
            }
        });

        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=user_input.getText().toString().trim();
                if(!input.isEmpty() && input!=null){
                    ///TODO: handle userSearch yeah  ...............
                    afterSearch(user_input);
                }
            }
        });

        user_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //do stuff
                    // Handle pressing "Enter" key here
                    String input=user_input.getText().toString().trim();
                    if(!input.isEmpty() && input!=null){
                        ///TODO: handle userSearch yeah
                        afterSearch(user_input);
                        user_input.clearFocus();
                    }
                    return true;
                }
                return false;
            }
        });

    }

    private void afterSearch(EditText user_input){
        user_input.setText("");
        user_input.setCursorVisible(false);
        user_input.clearFocus();
        hideKeyboardFrom(getActivity());
    }

    private void initFirstViewAndSetUP(final ArrayList<Book> books){
        final int tot=books.size();
        final int targ = (int )(Math.random() * (tot-3) + 1);
        /////FIRST LINE VIEW
        TextView numBooks;
        TextView book1_name,book2_name,book3_name;
        ImageView book1_image,book2_image,book3_image,ShowMore;
        RatingBar book1_rating,book2_rating,book3_rating;
        LinearLayout book1_view,book2_view,book3_view;
        LinearLayout book1_details,book2_details,book3_details;

        numBooks=(TextView)mRoot.findViewById(R.id.mm_numbooks);
        book1_name=(TextView)mRoot.findViewById(R.id.mm_name_of_book1);
        book2_name=(TextView)mRoot.findViewById(R.id.mm_name_of_book2);
        book3_name=(TextView)mRoot.findViewById(R.id.mm_name_of_book3);

        book1_image=(ImageView)mRoot.findViewById(R.id.mm_image_of_book1);
        book2_image=(ImageView)mRoot.findViewById(R.id.mm_image_of_book2);
        book3_image=(ImageView)mRoot.findViewById(R.id.mm_image_of_book3);
        ShowMore=(ImageView)mRoot.findViewById(R.id.showMoreAmazingBooks);

        book1_rating=(RatingBar)mRoot.findViewById(R.id.mm_rating_of_book1);
        book2_rating=(RatingBar)mRoot.findViewById(R.id.mm_rating_of_book2);
        book3_rating=(RatingBar)mRoot.findViewById(R.id.mm_rating_of_book3);

        book1_view=(LinearLayout)mRoot.findViewById(R.id.mm_linearView_of_book1);
        book2_view=(LinearLayout)mRoot.findViewById(R.id.mm_linearView_of_book2);
        book3_view=(LinearLayout)mRoot.findViewById(R.id.mm_linearView_of_book3);

        book1_details=(LinearLayout)mRoot.findViewById(R.id.mm_details_of_book1);
        book2_details=(LinearLayout)mRoot.findViewById(R.id.mm_details_of_book2);
        book3_details=(LinearLayout)mRoot.findViewById(R.id.mm_details_of_book3);

        if(tot>3){
            numBooks.setText(""+tot);

            book1_name.setText(books.get(targ).getTitle());
            book2_name.setText(books.get(targ+1).getTitle());
            book3_name.setText(books.get(targ+2).getTitle());

            Picasso.get().load(books.get(targ).getThumbnail()).placeholder(R.drawable.ic_book).into(book1_image);
            Picasso.get().load(books.get(targ+1).getThumbnail()).placeholder(R.drawable.ic_book).into(book2_image);
            Picasso.get().load(books.get(targ+2).getThumbnail()).placeholder(R.drawable.ic_book).into(book3_image);

            float rating1= Float.parseFloat(books.get(targ).getAverageRating());
            float rating2= Float.parseFloat(books.get(targ+1).getAverageRating());
            float rating3= Float.parseFloat(books.get(targ+2).getAverageRating());

            book1_rating.setRating(rating1);
            book2_rating.setRating(rating2);
            book3_rating.setRating(rating3);

            ///setUptheOnClick listeners yeah
            book1_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_detail=new Intent(getActivity(), BookDetailActivity.class);
                    intent_detail.putExtra(pConstants.BOOK_KEY,books.get(targ));
                    startActivity(intent_detail);
                }
            });

            book2_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_detail=new Intent(getActivity(), BookDetailActivity.class);
                    intent_detail.putExtra(pConstants.BOOK_KEY,books.get(targ+1));
                    startActivity(intent_detail);
                }
            });

            book2_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_detail=new Intent(getActivity(), BookDetailActivity.class);
                    intent_detail.putExtra(pConstants.BOOK_KEY,books.get(targ+2));
                    startActivity(intent_detail);
                }
            });

            ShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),"Show More Books",Toast.LENGTH_SHORT).show();
                }
            });

        }



    }

    private void initSecondViewAndSetUP(final ArrayList<Book> Books){
        final int tot=Books.size();
        final int targ = (int )(Math.random() * (tot-3) + 1);
        /////FIRST LINE VIEW
        TextView book1_name2,book2_name2,book3_name2;
        ImageView book1_image2,book2_image2,book3_image2,ShowMore;
        RatingBar book1_rating2,book2_rating2,book3_rating2;
        LinearLayout book1_view2,book2_view2,book3_view2;
        LinearLayout book1_details2,book2_details2,book3_details2;


        book1_name2=(TextView)mRoot.findViewById(R.id.mm_name_of_book1_2);
        book2_name2=(TextView)mRoot.findViewById(R.id.mm_name_of_book2_2);
        book3_name2=(TextView)mRoot.findViewById(R.id.mm_name_of_book3_2);

        book1_image2=(ImageView)mRoot.findViewById(R.id.mm_image_of_book1_2);
        book2_image2=(ImageView)mRoot.findViewById(R.id.mm_image_of_book2_2);
        book3_image2=(ImageView)mRoot.findViewById(R.id.mm_image_of_book3_2);
        ShowMore=(ImageView)mRoot.findViewById(R.id.showMoreMostReadBooks);

        book1_rating2=(RatingBar)mRoot.findViewById(R.id.mm_rating_of_book1_2);
        book2_rating2=(RatingBar)mRoot.findViewById(R.id.mm_rating_of_book2_2);
        book3_rating2=(RatingBar)mRoot.findViewById(R.id.mm_rating_of_book3_2);

        book1_view2=(LinearLayout)mRoot.findViewById(R.id.mm_linearView_of_book1_2);
        book2_view2=(LinearLayout)mRoot.findViewById(R.id.mm_linearView_of_book2_2);
        book3_view2=(LinearLayout)mRoot.findViewById(R.id.mm_linearView_of_book3_2);

        book1_details2=(LinearLayout)mRoot.findViewById(R.id.mm_details_of_book1_2);
        book2_details2=(LinearLayout)mRoot.findViewById(R.id.mm_details_of_book2_2);
        book3_details2=(LinearLayout)mRoot.findViewById(R.id.mm_details_of_book3_2);

        if(tot>3){

            book1_name2.setText(Books.get(targ).getTitle());
            book2_name2.setText(Books.get(targ+1).getTitle());
            book3_name2.setText(Books.get(targ+2).getTitle());

            Picasso.get().load(Books.get(targ).getThumbnail()).placeholder(R.drawable.ic_book).into(book1_image2);
            Picasso.get().load(Books.get(targ+1).getThumbnail()).placeholder(R.drawable.ic_book).into(book2_image2);
            Picasso.get().load(Books.get(targ+2).getThumbnail()).placeholder(R.drawable.ic_book).into(book3_image2);

            float rating1= Float.parseFloat(Books.get(targ).getAverageRating());
            float rating2= Float.parseFloat(Books.get(targ+1).getAverageRating());
            float rating3= Float.parseFloat(Books.get(targ+2).getAverageRating());

            book1_rating2.setRating(rating1);
            book2_rating2.setRating(rating2);
            book3_rating2.setRating(rating3);

            ///setUptheOnClick listeners yeah
            book1_view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_detail=new Intent(getActivity(), BookDetailActivity.class);
                    intent_detail.putExtra(pConstants.BOOK_KEY,Books.get(targ));
                    startActivity(intent_detail);
                }
            });

            book2_view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_detail=new Intent(getActivity(), BookDetailActivity.class);
                    intent_detail.putExtra(pConstants.BOOK_KEY,Books.get(targ+1));
                    startActivity(intent_detail);
                }
            });

            book2_view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_detail=new Intent(getActivity(), BookDetailActivity.class);
                    intent_detail.putExtra(pConstants.BOOK_KEY,Books.get(targ+2));
                    startActivity(intent_detail);
                }
            });

            ShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),"show More Amazing Books",Toast.LENGTH_SHORT).show();
                }
            });


        }



    }

    public static void hideKeyboardFrom(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}

