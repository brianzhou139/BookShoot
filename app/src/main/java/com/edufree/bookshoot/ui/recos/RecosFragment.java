package com.edufree.bookshoot.ui.recos;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edufree.bookshoot.Loaders.googleApiBookLoader;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.adapters.RecoursAdapter;
import com.edufree.bookshoot.adapters.authorAdapter;
import com.edufree.bookshoot.database.BookContract;
import com.edufree.bookshoot.models.Author;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Search;
import com.edufree.bookshoot.utils.networkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class RecosFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>{
    private static final int googleBookLoader_id=2;
    private static final String TAG="RecosFragment";
    private ArrayList<Book> mRecommendedBooks=new ArrayList<>();
    private ArrayList<Author> mAuthorsList=new ArrayList<>();

    private Search userSearch=null;
    private RecyclerView rvBooks_recyler_recos,rvBooks_recycler_recos_authors;
    private AlertDialog dialog_recos;
    private View mRoot;
    private TextView category1_name,category2_name,category3_name;
    private TextView category1_num,category2_num,category3_num;
    private LinearLayout nType1, nType2, nType3;

    private LinearLayout categories_linear, author_linear,reading_linear;
    private LinearLayout forYou_selections,categories_selections,status_selections;

    private TextView readMore_books,readMoreAuthors;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_recos, container, false);
        initViews();

        //check for internet connection yeah
        rvBooks_recyler_recos=(RecyclerView)mRoot.findViewById(R.id.rv_books_recos);
        rvBooks_recyler_recos.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        rvBooks_recycler_recos_authors=(RecyclerView)mRoot.findViewById(R.id.rv_books_recos_authors);
        rvBooks_recycler_recos_authors.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        dialog_recos=new SpotsDialog.Builder().setContext(getActivity()).build();

        readMore_books=(TextView)mRoot.findViewById(R.id.showMore_books);
        readMoreAuthors=(TextView)mRoot.findViewById(R.id.showMore_authors_id);


        //getBooks from the sql_database
        mRecommendedBooks=getRecommendedBooks();

        if(mRecommendedBooks.size()>=1){
            RecoursAdapter adapter=new RecoursAdapter(getActivity(),mRecommendedBooks);
            rvBooks_recyler_recos.setAdapter(adapter);
            setBookScroll_1(adapter);
            if(isInternetConnected()){
                readAuthors();
            }
        }else {
            if(isInternetConnected()){
                makeQueryToGoogleApi(false,"programming");
                readAuthors();
            }else{
                noNetworkError();
            }
        }

        if(getActivity().getSupportLoaderManager().getLoader(googleBookLoader_id)!=null){
            getActivity().getSupportLoaderManager().initLoader(googleBookLoader_id,null,this);
        }

        return mRoot;

    }

    private void setBookScroll_1(final RecoursAdapter adapter) {
        readMore_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvBooks_recyler_recos.post(new Runnable() {
                    @Override
                    public void run() {
                        rvBooks_recyler_recos.smoothScrollToPosition(adapter.getItemCount()- 1);
                    }
                });
            }
        });
    }

    private void setAuthorScroll_1(final authorAdapter adapter) {
        readMoreAuthors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvBooks_recycler_recos_authors.post(new Runnable() {
                    @Override
                    public void run() {
                        rvBooks_recycler_recos_authors.smoothScrollToPosition(adapter.getItemCount()- 1);
                    }
                });
            }
        });
    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        if(id==googleBookLoader_id){
            dialog_recos.show();
            Boolean isRefresh=null;
            String queryString = "";
            URL bookURl=null;

            if (args != null) {
                queryString = args.getString("qString");
                isRefresh=args.getBoolean("refresh");
            }

            try {
                //check to see if the user did an advance search if no...do default search
                if(userSearch==null){
                    bookURl= networkUtils.buildURL(queryString);
                }else{
                    bookURl=networkUtils.buildURL(userSearch.getTitle(),userSearch.getAuthor(),userSearch.getPublisher(),userSearch.getPublisher());
                }
                return new googleApiBookLoader(getActivity(),bookURl);
            }catch(Exception o){
                Log.d(TAG,o.toString());
            }
            //TODO: setup the loader if its refresh
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        int id = loader.getId();

        if(id==googleBookLoader_id){
            dialog_recos.dismiss();
            //mLoadingProgress.setVisibility(View.INVISIBLE);
            if(data==null){
                rvBooks_recyler_recos.setVisibility(View.INVISIBLE);
                //tvError.setVisibility(View.VISIBLE);
            }else {
                rvBooks_recyler_recos.setVisibility(View.VISIBLE);
                //tvError.setVisibility(View.INVISIBLE);
                ArrayList<Book> books= networkUtils.getBooksFromJson(data);
                int num=books.size();
                RecoursAdapter adapter=new RecoursAdapter(getActivity(),books);
                rvBooks_recyler_recos.setAdapter(adapter);
                setBookScroll_1(adapter);
                Log.d(TAG,"************** : "+num);
            }

        }//end of LOADER 1/**********************************************************************/
    }


    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private void initViews(){
        category1_name =(TextView)mRoot.findViewById(R.id.category1_name);
        category1_num =(TextView)mRoot.findViewById(R.id.category1_num);

        category2_name =(TextView)mRoot.findViewById(R.id.category2_name);
        category2_num =(TextView)mRoot.findViewById(R.id.category2_num);

        category3_name =(TextView)mRoot.findViewById(R.id.category3_name);
        category3_num =(TextView)mRoot.findViewById(R.id.category3_num);

        nType1 =(LinearLayout)mRoot.findViewById(R.id.type1_id);
        nType2 =(LinearLayout)mRoot.findViewById(R.id.type2_id);
        nType3 =(LinearLayout)mRoot.findViewById(R.id.type3_id);

        //categories linear
        categories_linear=(LinearLayout)mRoot.findViewById(R.id.linear_categories);
        author_linear =(LinearLayout)mRoot.findViewById(R.id.linear_authors);
        reading_linear=(LinearLayout)mRoot.findViewById(R.id.linear_readingStats);

        ///selctions
        forYou_selections=(LinearLayout)mRoot.findViewById(R.id.foryou_id);
        categories_selections=(LinearLayout)mRoot.findViewById(R.id.categories_selections);
        status_selections=(LinearLayout)mRoot.findViewById(R.id.status_selections);

        forYou_selections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                author_linear.setVisibility(View.VISIBLE);
                categories_linear.setVisibility(View.GONE);
                reading_linear.setVisibility(View.GONE);
            }
        });

        categories_selections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categories_linear.setVisibility(View.VISIBLE);
                author_linear.setVisibility(View.GONE);
                reading_linear.setVisibility(View.GONE);
            }
        });

        status_selections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reading_linear.setVisibility(View.VISIBLE);
                categories_linear.setVisibility(View.GONE);
                author_linear.setVisibility(View.GONE);
            }
        });


        //TODO : implemt actions after any list now reading,read later,finished is clicked
        nType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : show books being read by user ...
            }
        });

        nType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : show books to be read later
            }
        });

        nType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : show finished books yeah
            }
        });

    }
    private void whileLoadingView(){
        author_linear.setVisibility(View.GONE);
        categories_linear.setVisibility(View.VISIBLE);
        reading_linear.setVisibility(View.GONE);
    }

    private void afterLoadingView(){
        author_linear.setVisibility(View.VISIBLE);
        categories_linear.setVisibility(View.GONE);
        reading_linear.setVisibility(View.GONE);
    }

    private void makeQueryToGoogleApi(Boolean isRefresh,String query){
        if(!isRefresh){
            Bundle queryBundle1 = new Bundle();
            String queryString=query;
            queryBundle1.putBoolean("refresh",isRefresh);
            queryBundle1.putString("qString", queryString);
            getActivity().getSupportLoaderManager().restartLoader(googleBookLoader_id, queryBundle1, this);
        }else{
            Bundle queryBundle2 = new Bundle();
            String queryString="";
            String classyString="1";

            queryBundle2.putString("qString", queryString);
            queryBundle2.putString("cString",classyString);
            getActivity().getSupportLoaderManager().restartLoader(googleBookLoader_id, queryBundle2, this);
        }

    }

    private boolean isInternetConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void noNetworkError() {
        Toast.makeText(getActivity(),"there is no network",Toast.LENGTH_SHORT).show();
    }

    private void readAuthors() {
        whileLoadingView();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("authors");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAuthorsList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Author author=snapshot.getValue(Author.class);
                    if(author!=null){
                        mAuthorsList.add(author);
                    }
                }
                afterLoadingView();
                authorAdapter adapter=new authorAdapter(getActivity(),mAuthorsList);
                rvBooks_recycler_recos_authors.setAdapter(adapter);
                setAuthorScroll_1(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }//end of readUsers...

    private ArrayList<Book> getRecommendedBooks(){

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

        Uri uri= BookContract.RecommendedBooksEntry.CONTENT_URI;
        Cursor cursor=getActivity().getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);

        Log.d(TAG, "From Content Provider >>>> CURSOR OBJECT YEAH " );

        if (cursor != null) {
            int indexID=cursor.getColumnIndex(BookContract.RecommendedBooksEntry._ID);
            int index_Book_id=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_ID);
            int index_Book_isbn=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_ISBN);
            int index_Book_title=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_TITLE);
            int index_Book_subtitle=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_SUBTITLE);
            int index_Book_publisher=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_PUBLISHER);
            int index_Book_publishedDate=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE);
            int index_Book_description=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_DESCRIPTION);
            int index_Book_thumbnail=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_THUMBNAIL);
            int index_Book_averageRating=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_AVERAGE_RATING);
            int index_Book_author=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_AUTHOR);
            int index_Book_author2=cursor.getColumnIndex(BookContract.RecommendedBooksEntry.COLUMN_BOOK_AUTHOR2);

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
                mNewBook.setAuthors(authors);
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