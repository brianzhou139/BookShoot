package com.edufree.bookshoot.ui.recos;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_recos, container, false);
        initViews();

        //check for internet connection yeah
        rvBooks_recyler_recos=(RecyclerView)mRoot.findViewById(R.id.rv_books_recos);
        rvBooks_recyler_recos.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        rvBooks_recycler_recos_authors=(RecyclerView)mRoot.findViewById(R.id.rv_books_recos_authors);
        rvBooks_recycler_recos_authors.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        dialog_recos=new SpotsDialog.Builder().setContext(getActivity()).build();

        if(isInternetConnected()){
            makeQueryToGoogleApi(false,"cooking");
            readAuthors();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }//end of readUsers...


}