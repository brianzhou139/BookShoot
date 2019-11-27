package com.edufree.bookshoot.ui.favourites;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edufree.bookshoot.BookDetailActivity;
import com.edufree.bookshoot.Loaders.googleApiBookLoader;
import com.edufree.bookshoot.MainActivity;
import com.edufree.bookshoot.NewMenuActivity;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.SearchActivity;
import com.edufree.bookshoot.adapters.BooksAdapter;
import com.edufree.bookshoot.adapters.FavouritesAdapter;
import com.edufree.bookshoot.database.BookContract;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Search;
import com.edufree.bookshoot.shitData.fakeData;
import com.edufree.bookshoot.utils.networkUtils;
import com.edufree.bookshoot.utils.pConstants;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URL;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.getActivityResult;

public class FavouritesFragment extends Fragment{

    /****************************************************************************************************/
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=400;
    private static final int IMAGE_PICK_GALLERY_CODE=1000;
    private static final int IMAGE_PICK_CAMERA_CODE=1001;
    String cameraPermission[];
    String storagePermission[];
    Uri image_uri;
    ImageView mPreviewIv;
    /****************************************************************************************************/


    private String TAG="FavouritesFragment";
    private View mRoot;
    private AlertDialog dialog;
    private ArrayList<Book> likedBooks=new ArrayList<>();
    private ArrayList<Book> clickedBooks=new ArrayList<>();
    private ArrayList<Book> mOftenSearchedBooks=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_favourites, container, false);
        //get books from sql
        likedBooks=getLikedBooksFromDB();
        clickedBooks=getClickedBooksFromDB();
        initMajor();
        initFirstViewAndSetUP(likedBooks);
        initSecondViewAndSetUP(clickedBooks);

        //do permission shit
        //camera permission
        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        return mRoot;
    }


    private void getOftenSearchedBooksFromDatabase() {
        mOftenSearchedBooks=fakeData.getShitBooksSamples();
    }

    private void initMajor(){

        CardView app_icon=(CardView)mRoot.findViewById(R.id.app_icon);
        CardView usePhoto=(CardView)mRoot.findViewById(R.id.usePhoto_id);

        ImageView search_image=(ImageView)mRoot.findViewById(R.id.search_2_id);
        mPreviewIv=(ImageView)mRoot.findViewById(R.id.prev_id);

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
                ///TODO : do something here titus yeah
                showImageImportDialog();
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

    private void initFirstViewAndSetUP(final ArrayList<Book> likedBooksDB){
        ArrayList<Book> mAutoSavedBooks=new ArrayList<>();
        mAutoSavedBooks=fakeData.getShitBooksSamples();

        ArrayList<Book> books_temp=new ArrayList<>();

        if(likedBooksDB.size()<=3){
            books_temp=mAutoSavedBooks;
        }else{
            books_temp=likedBooksDB;
        }

        final int tot=books_temp.size();
        final int targ =0;


        final ArrayList<Book> books=books_temp;


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

            float rating1=0;
            float rating2=0;
            float rating3=0;

            if(!books.get(targ).getAverageRating().isEmpty()){
                rating1= Float.parseFloat(books.get(targ).getAverageRating());
            }else{
                rating1=1;
            }

            if(!books.get(targ+1).getAverageRating().isEmpty()){
                rating2= Float.parseFloat(books.get(targ+1).getAverageRating());
            }else{
                rating2=1;
            }

            if(!books.get(targ+2).getAverageRating().isEmpty()){
                rating3= Float.parseFloat(books.get(targ+2).getAverageRating());
            }else{
                rating3=1;
            }

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

            book3_view.setOnClickListener(new View.OnClickListener() {
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

    private void initSecondViewAndSetUP(final ArrayList<Book> searchedBooks){

        ArrayList<Book> mAutoSavedBooks=new ArrayList<>();
        mAutoSavedBooks=fakeData.getShitBooksSamples();

        ArrayList<Book> books_temp=new ArrayList<>();

        if(searchedBooks.size()<=3){
            books_temp=mAutoSavedBooks;
        }else{
            books_temp=searchedBooks;
        }


        final int tot=books_temp.size();
        final int targ = 0;


        final ArrayList<Book> Books=books_temp;

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


            float rating1=0;
            float rating2=0;
            float rating3=0;

            if(!Books.get(targ).getAverageRating().isEmpty()){
                rating1= Float.parseFloat(Books.get(targ).getAverageRating());
            }else{
                rating1=1;
            }

            if(!Books.get(targ+1).getAverageRating().isEmpty()){
                rating2= Float.parseFloat(Books.get(targ+1).getAverageRating());
            }else{
                rating2=1;
            }

            if(!Books.get(targ+2).getAverageRating().isEmpty()){
                rating3= Float.parseFloat(Books.get(targ+2).getAverageRating());
            }else{
                rating3=1;
            }

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

            book3_view2.setOnClickListener(new View.OnClickListener() {
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

    private ArrayList<Book> getLikedBooksFromDB(){
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

        Uri uri= BookContract.SavedBooksEntry.CONTENT_URI;
        Cursor cursor=getActivity().getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);

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
            int index_Book_author=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_AUTHOR);
            int index_Book_author2=cursor.getColumnIndex(BookContract.SavedBooksEntry.COLUMN_BOOK_AUTHOR2);


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

    private ArrayList<Book> getClickedBooksFromDB(){
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
        Cursor cursor=getActivity().getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);

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
            int index_Book_author2=cursor.getColumnIndex(BookContract.ClickedBooksEntry.COLUMN_BOOK_AUTHOR2);

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

    /********************************************************************************************************************/
    /********************************************************************************************************************/
    /********************************************************************************************************************/
    /************************************************GAMERA/GALLERY CODE YEAH*******************************************/
    /********************************************************************************************************************/
    /********************************************************************************************************************/
    /********************************************************************************************************************/
    /********************************************************************************************************************/
    /********************************************************************************************************************/

    private void showImageImportDialog() {
        //items to display in the dialog ........
        String [] items={" Camera "," Gallery "};
        androidx.appcompat.app.AlertDialog.Builder dialog=new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        //set title ...
        dialog.setTitle("Select Image");

        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    ///camera options here ....   ......
                    if(!checkCameraPermission()){
                        //camera permission not allowed ....
                        requestCameraPermission();
                    }else{
                        //camera allowed yeah , take the picture yeah
                        pickCamera();
                    }
                }

                if(i==1){
                    //gallery optiom goes here yeah...   .....
                    if(!checkStoragePermission()){
                        //Storage permission not allowed..ask for it
                        requestStoragePermission();
                    }else{
                        ////
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }//end of showImageImport

    private void pickGallery() {
        ///intent to pick image from gallery
        Intent intent=new Intent(Intent.ACTION_PICK);
        //set intent type to image ...
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //content values to take image from camera, it will also be to save the high quality image
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic"); /// title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");  //description
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }//end of pickCamera

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(),storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(),cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //In order to get high qulaity image we gotta save to external storage first yeah ........................
        boolean result= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorage){
                        pickCamera();
                    }else{
                        Toast.makeText(getActivity(),"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean writeStorage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(writeStorage){
                        pickGallery();
                    }else{
                        Toast.makeText(getActivity(),"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }//end of switch statement.,........
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG,"******************************************************************");

        if(resultCode==RESULT_OK){

            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                //got image from gallery now crop it yeah
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getContext(),this);

                Log.d(TAG,"Gallery image : cropping galleryImage");
            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE){
                //got imagge from camera ..nnow do it
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getContext(),this);
                Log.d(TAG,"Gallery image : cropping Camera Image now... ");
            }

        }

        //get croppedImageYEah
        if(requestCode==CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result= getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri=result.getUri();  //get image uri ...
                //set image to image view ...
                mPreviewIv.setImageURI(resultUri);
                Log.d(TAG,"Photo is here..uri  : " + resultUri);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)mPreviewIv.getDrawable();
                Bitmap bipmap=bitmapDrawable.getBitmap();

                TextRecognizer recognizer=new TextRecognizer.Builder(getActivity()).build();

                if(!recognizer.isOperational()){
                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                }else{
                    Frame frame=new Frame.Builder().setBitmap(bipmap).build();
                    SparseArray<TextBlock> items=recognizer.detect(frame);
                    StringBuilder sb=new StringBuilder();
                    //get text tilltheres is no text
                    String results_text=null;
                    for(int a=0;a<items.size();a++){
                        TextBlock myItem=items.valueAt(a);
                        String temp=myItem.getValue().toString().replace("\n", "").replace("\r", "");
                        sb.append(temp+" ");
                    }

                    //set the text to edit text yeah ...
                    results_text=sb.toString();


                    if(results_text.isEmpty() || results_text==null){
                        //TODO:Do something here titus yeah .........................
                        Toast.makeText(getActivity(), "Couldnt Recognize text...sorry", Toast.LENGTH_LONG).show();
                    }else{
                        //TODO:DO search of the Book__ yeah...........
                        Log.d(TAG,"hala tito : " + results_text);
                        Intent i=new Intent(getActivity(), MainActivity.class);
                        i.putExtra(pConstants.PHOTO_SEARCH_TEXT_KEY,results_text);
                        getActivity().startActivity(i);
                    }

                }

            }else if (resultCode== CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                //if there is an error show it ..please ..
                Exception error=result.getError();
                Toast.makeText(getActivity(),""+error,Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG,"DDDDDDDDDDDDDDDDDDDDDDDDD");
            Toast.makeText(getActivity(),"DDDDDDDDDDDDDDD",Toast.LENGTH_SHORT);
        }

    }


}

