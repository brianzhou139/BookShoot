package com.edufree.bookshoot.ui.home;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edufree.bookshoot.Loaders.googleApiBookLoader;
import com.edufree.bookshoot.MainActivity;
import com.edufree.bookshoot.R;
import com.edufree.bookshoot.SearchActivity;
import com.edufree.bookshoot.adapters.BooksAdapter;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Search;
import com.edufree.bookshoot.utils.networkUtils;
import com.edufree.bookshoot.utils.pConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URL;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>{
    private static final int googleBookLoader_id=1;
    private static final String TAG="HomeFragment";
    FloatingActionButton fab;
    private View mRoot;
    private Toolbar mHomeToolBar;

    ///from intents
    private Search userSearch=null;
    private String userAuthorSearch=null;

    private RecyclerView rvBooks_recyler;
    private RelativeLayout rvBooks_error;
    private Button btnRefresh;

    private AlertDialog dialog;
    private CardView mApp_icon;
    private CardView mUsePhoto;
    private ImageView mSearch_image;
    private EditText mUser_input;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_home, container, false);
        initCustomToolandClicks();
        Intent mIntent=getActivity().getIntent();
        userSearch=mIntent.getParcelableExtra("Query");
        userAuthorSearch=mIntent.getStringExtra(pConstants.CLICKED_AUTHOR__KEY);

        //check for internet connection yeah
        if(isInternetConnected()){
            thereIsNetwork();
            if(userAuthorSearch!=null){
                makeQueryToGoogleApi(false,true,userAuthorSearch);
            }else{
                makeQueryToGoogleApi(false,false,"algorithms");
            }
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
                if(userSearch==null){
                    bookURl= networkUtils.buildURL(queryString);
                }else{
                    bookURl=networkUtils.buildURL(userSearch.getTitle(),userSearch.getAuthor(),userSearch.getPublisher(),userSearch.getPublisher());
                }

                if(userSearch==null && isUsingAuthor){
                    bookURl= networkUtils.buildURL(queryString,2);
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
            dialog.dismiss();
            //mLoadingProgress.setVisibility(View.INVISIBLE);
            if(data==null){
                rvBooks_recyler.setVisibility(View.INVISIBLE);
                //tvError.setVisibility(View.VISIBLE);
            }else {
                rvBooks_recyler.setVisibility(View.VISIBLE);
                //tvError.setVisibility(View.INVISIBLE);
                ArrayList<Book> books=networkUtils.getBooksFromJson(data);
                int num=books.size();
                BooksAdapter adapter=new BooksAdapter(getActivity(),books);
                rvBooks_recyler.setAdapter(adapter);
            }

        }//end of LOADER 1/**********************************************************************/

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private void thereIsNetwork(){
        rvBooks_recyler.setVisibility(View.VISIBLE);
        rvBooks_error.setVisibility(View.GONE);
    }

    private void noNetworkError() {
        Toast.makeText(getActivity(),"there is no network",Toast.LENGTH_SHORT).show();
        rvBooks_recyler.setVisibility(View.GONE);
        rvBooks_error.setVisibility(View.VISIBLE);
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

        mApp_icon = (CardView)mRoot.findViewById(R.id.app_icon);
        mUsePhoto = (CardView)mRoot.findViewById(R.id.usePhoto_id);

        mSearch_image = (ImageView)mRoot.findViewById(R.id.search_2_id);
        mUser_input = (EditText)mRoot.findViewById(R.id.user_input_id);
        mUser_input.setCursorVisible(false);

        //setting recycler and manager
        btnRefresh=(Button)mRoot.findViewById(R.id.rv_books_refresh);
        rvBooks_error=(RelativeLayout)mRoot.findViewById(R.id.rv_books_no_network);
        rvBooks_recyler=(RecyclerView)mRoot.findViewById(R.id.rv_books);
        rvBooks_recyler.setLayoutManager(new LinearLayoutManager(getActivity()));
        dialog=new SpotsDialog.Builder().setContext(getActivity()).build();


        mApp_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: do something here titus
                Toast.makeText(getActivity(),"you clicked the app icon",Toast.LENGTH_SHORT).show();
            }
        });

        mUsePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///TODO : do something here titus yeah
                Toast.makeText(getActivity(),"use Photo",Toast.LENGTH_SHORT).show();
            }
        });

        mUser_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser_input.setCursorVisible(true);
            }
        });

        mSearch_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=mUser_input.getText().toString().trim();
                if(!input.isEmpty() && input!=null){
                    ///TODO: handle userSearch yeah  ...............
                    handleSearch(input);
                    afterSearch(mUser_input);
                }
            }
        });

        mUser_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //do stuff
                    // Handle pressing "Enter" key here
                    String input=mUser_input.getText().toString().trim();
                    if(!input.isEmpty() && input!=null){
                        ///TODO: handle userSearch yeah
                        handleSearch(input);
                        afterSearch(mUser_input);
                    }
                    return true;
                }
                return false;
            }
        });


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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu,menu);
        final MenuItem searchItem=menu.findItem(R.id.app_bar_search);
        final SearchView searchView= (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    if(isInternetConnected()){
                        makeQueryToGoogleApi(false,false,query);
                    }else{
                        noNetworkError();
                    }
                }catch (Exception io){
                    Log.d(TAG,io.toString());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.action_advanced_search){
            Intent goToSearchIntent=new Intent(getActivity(), SearchActivity.class);
            startActivity(goToSearchIntent);
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}//end of MainActivity....