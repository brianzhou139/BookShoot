package com.edufree.bookshoot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.edufree.bookshoot.Loaders.googleApiBookLoader;
import com.edufree.bookshoot.adapters.FavouritesAdapter;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Search;
import com.edufree.bookshoot.utils.networkUtils;

import java.net.URL;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class BookListAfterClickActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<String>{

    private static final String TAG="FavouritesFragment";
    private static final int googleBookLoader_id=3;
    private RecyclerView recycler;
    private View mRoot;
    private Search userSearch=null;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list_after_click);
        initViews();
        //check for internet connection yeah
        if(isInternetConnected()){
            makeQueryToGoogleApi(false,"algorithms");
        }else{
            noNetworkError();
        }

        if(getSupportLoaderManager().getLoader(googleBookLoader_id)!=null){
            getSupportLoaderManager().initLoader(googleBookLoader_id,null,this);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        if(id==googleBookLoader_id){
            dialog.show();
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
                return new googleApiBookLoader(this,bookURl);
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
                recycler.setVisibility(View.INVISIBLE);
                //tvError.setVisibility(View.VISIBLE);
            }else {
                recycler.setVisibility(View.VISIBLE);
                //tvError.setVisibility(View.INVISIBLE);
                ArrayList<Book> books=networkUtils.getBooksFromJson(data);
                int num=books.size();
                FavouritesAdapter adapter=new FavouritesAdapter(this,books);
                recycler.setAdapter(adapter);
            }

        }//end of LOADER 1/**********************************************************************/
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }

    private void noNetworkError() {
        Toast.makeText(this,"there is no network",Toast.LENGTH_SHORT).show();
    }

    private void initViews(){
        recycler=(RecyclerView)findViewById(R.id.rv_books_saved);
        recycler.setLayoutManager(new GridLayoutManager(this,2));
        dialog=new SpotsDialog.Builder().setContext(this).build();
    }


    private boolean isInternetConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void makeQueryToGoogleApi(Boolean isRefresh,String query){
        if(!isRefresh){
            Bundle queryBundle1 = new Bundle();
            String queryString=query;

            queryBundle1.putBoolean("refresh",isRefresh);
            queryBundle1.putString("qString", queryString);
            getSupportLoaderManager().restartLoader(googleBookLoader_id, queryBundle1, this);
        }else{
            Bundle queryBundle2 = new Bundle();
            String queryString="";
            String classyString="1";

            queryBundle2.putString("qString", queryString);
            queryBundle2.putString("cString",classyString);
            getSupportLoaderManager().restartLoader(googleBookLoader_id, queryBundle2, this);
        }

    }

}


