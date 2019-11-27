package com.edufree.bookshoot.pagerFragments;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edufree.bookshoot.Loaders.googleApiBookLoader;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.adapters.BooksAdapter;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.utils.networkUtils;
import com.edufree.bookshoot.utils.pConstants;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import dmax.dialog.SpotsDialog;

public class RelatedFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>{
    private static final int googleBookLoader_id=5;
    private static final String TAG="RelatedFragment";
    private View mRoot;
    private Book currentBook=null;
    private RecyclerView rvRelatedBooks_recyler;
    private RelativeLayout rvRelatedBooks_error;
    private Button btnRefresh;
    private AlertDialog dialog;

    private TextView seeAllRelated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRoot = inflater.inflate(R.layout.fragment_related_paged, container, false);

        Bundle args = getArguments();
        currentBook = args.getParcelable(pConstants.BOOK_FRAGMENT_KEY);

        initCustomToolandClicks();

        //check for internet connection yeah
        if(isInternetConnected()){
            thereIsNetwork();
            makeQueryToGoogleApi(false,false,currentBook.getTitle());
        }else{
            noNetworkError();
        }

        if(getActivity().getSupportLoaderManager().getLoader(googleBookLoader_id)!=null){
            getActivity().getSupportLoaderManager().initLoader(googleBookLoader_id,null,this);
        }

        return mRoot;
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        if(id==googleBookLoader_id){
            dialog.show();
            Boolean isRefresh=null;
            Boolean isUsingAuthor=false;
            String queryString = "";
            URL bookURl=null;

            if (args != null) {
                queryString = args.getString("qString");
                isRefresh=args.getBoolean("refresh");
                isUsingAuthor=args.getBoolean("useAuthor");
            }

            try {
                //check to see if the user did an advance search if no...do default search
                bookURl= networkUtils.buildURL(queryString);
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
            dialog.dismiss();
            //mLoadingProgress.setVisibility(View.INVISIBLE);
            if(data==null){
                rvRelatedBooks_recyler.setVisibility(View.INVISIBLE);
                //tvError.setVisibility(View.VISIBLE);
            }else {
                rvRelatedBooks_recyler.setVisibility(View.VISIBLE);
                //tvError.setVisibility(View.INVISIBLE);
                ArrayList<Book> books=networkUtils.getBooksFromJson(data);
                ArrayList<Book> newBooks=books;
                int index = 0;

                for(Book book:books){
                    if(book.getId().matches(currentBook.getId())){
                        index=books.indexOf(book);
                    }
                }

                newBooks.remove(index);
                BooksAdapter adapter=new BooksAdapter(getActivity(),newBooks);
                rvRelatedBooks_recyler.setAdapter(adapter);

            }

        }//end of LOADER 1/**********************************************************************/

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private void thereIsNetwork(){
        rvRelatedBooks_recyler.setVisibility(View.VISIBLE);
        rvRelatedBooks_error.setVisibility(View.GONE);
    }

    private void noNetworkError() {
        Toast.makeText(getActivity(),"there is no network",Toast.LENGTH_SHORT).show();
        rvRelatedBooks_recyler.setVisibility(View.GONE);
        rvRelatedBooks_error.setVisibility(View.VISIBLE);
    }

    private void makeQueryToGoogleApi(Boolean isRefresh,Boolean isAuthorSearch,String query){
        if(!isRefresh){
            Bundle queryBundle1 = new Bundle();
            String queryString=query;

            queryBundle1.putBoolean("refresh",isRefresh);
            queryBundle1.putString("qString", queryString);
            queryBundle1.putBoolean("useAuthor",isAuthorSearch);
            getActivity().getSupportLoaderManager().restartLoader(googleBookLoader_id, queryBundle1, this);
        }else{
            Bundle queryBundle2 = new Bundle();
            String queryString="";
            String classyString="1";

            queryBundle2.putString("qString", queryString);
            queryBundle2.putString("cString",classyString);
            //queryBundle2.putBoolean("useAuthor",isAuthorSearch);
            getActivity().getSupportLoaderManager().restartLoader(googleBookLoader_id, queryBundle2, this);
        }

    }

    private void initCustomToolandClicks() {

        //setting recycler and manager
        btnRefresh=(Button)mRoot.findViewById(R.id.rv_books_refresh);
        rvRelatedBooks_error=(RelativeLayout)mRoot.findViewById(R.id.rv_Relatedbooks_no_network);
        rvRelatedBooks_recyler=(RecyclerView)mRoot.findViewById(R.id.rv_books_related);
        rvRelatedBooks_recyler.setLayoutManager(new LinearLayoutManager(getActivity()));
        dialog=new SpotsDialog.Builder().setContext(getActivity()).build();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: do some when refresh is pressed yeah...
                //getActivity().finish();
                //startActivity(getActivity().getIntent());
                Toast.makeText(getActivity(),"Refresh selected",Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void handleSearch(String query){
        try {
            if(isInternetConnected()){
                makeQueryToGoogleApi(false,false,query);
            }else{
                noNetworkError();
            }

        }catch (Exception io){
            Log.d(TAG,io.toString());
        }
    }

    private void afterSearch(EditText user_input){
        user_input.setText("");
        user_input.setCursorVisible(false);
        user_input.clearFocus();
        hideKeyboardFrom(getActivity());
    }

    public static void hideKeyboardFrom(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void closeKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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




}
