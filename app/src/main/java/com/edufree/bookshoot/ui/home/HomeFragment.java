package com.edufree.bookshoot.ui.home;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.edufree.bookshoot.database.BookContract;
import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Search;
import com.edufree.bookshoot.utils.networkUtils;
import com.edufree.bookshoot.utils.pConstants;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;
import static com.theartofdev.edmodo.cropper.CropImage.*;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>{
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
    private static final int googleBookLoader_id=1;
    private static final String TAG="HomeFragment";
    FloatingActionButton fab;
    private View mRoot;
    private Toolbar mHomeToolBar;

    ///from intents
    private Search userSearch=null;
    private String userAuthorSearch=null;
    private String photo_text=null;

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
        photo_text=mIntent.getStringExtra(pConstants.PHOTO_SEARCH_TEXT_KEY);

        //check for internet connection yeah
        if(isInternetConnected()){
            thereIsNetwork();
            if(userAuthorSearch!=null){
                makeQueryToGoogleApi(false,true,userAuthorSearch);
            }else if(photo_text!=null){
                makeQueryToGoogleApi(false,false,photo_text);
            }else{
                ArrayList<Book> recommendedList=new ArrayList<>();
                recommendedList=getRecommendedBooks();
                if(recommendedList.size()>=1){
                    //Collections.shuffle(recommendedList);
                    BooksAdapter adapter=new BooksAdapter(getActivity(),recommendedList);
                    rvBooks_recyler.setAdapter(adapter);
                }else{
                    makeQueryToGoogleApi(false,false,"tolkien");
                }
            }
        }else{
            noNetworkError();
        }

        if(getActivity().getSupportLoaderManager().getLoader(googleBookLoader_id)!=null){
            getActivity().getSupportLoaderManager().initLoader(googleBookLoader_id,null,this);
        }
        //do permission shit
        //camera permission

        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
                saveRecommendedBooks(books);
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
        mPreviewIv=(ImageView)mRoot.findViewById(R.id.preview_id);
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
                showImageImportDialog();
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
        closeKeyBoard();
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

            ActivityResult result= getActivityResult(data);
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
                        makeQueryToGoogleApi(false,false,results_text);
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

    /*********************************************************************************************************************/
    /******************************************************************************************************************/
    /*************************************   SQL DATABASE YEAH ****************************************/
    /*******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/
    private void saveRecommendedBooks(ArrayList<Book> resultList){
        ArrayList<Book> newRecos=new ArrayList<>();
        ArrayList<Book> recommendedBooks=getRecommendedBooks();

        if(true){
            for(Book result_book:resultList){
                Boolean isAvailable=false;
                for(Book reco_book:recommendedBooks){
                    if(result_book.getId().matches(reco_book.getId())){
                        isAvailable=true;
                    }
                }

                if(!isAvailable){
                    newRecos.add(result_book);
                }

            }
        }

        ///Save the Books into the SQL_lite Database yeah........
        for(Book book:newRecos){
            insertRecommendedBook(book);
        }

    }

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

    private void insertRecommendedBook(Book mBook){
        String book_author=null;
        String book_author2=null;

        String []auths=mBook.getAuthors();
        if(auths!=null){
            int auth_size=auths.length;

            if(auth_size==1){
                book_author=auths[0];
            }else if(auth_size==2){
                book_author2=auths[1];
            }else{
                if(auth_size>0 && auth_size>3){
                    book_author=auths[0];
                    book_author2=auths[1];
                }
            }

        }

        String book_id=mBook.getId();
        String book_isbn=mBook.getIsbn();
        String book_title=mBook.getTitle();
        String book_subtitle=mBook.getSubtitle();
        String book_publisher=mBook.getPublisher();
        String book_publishedDate=mBook.getPublishedDate();
        String book_description=mBook.getDescription();
        String book_thumbnail=mBook.getThumbnail();
        String book_averageRating=mBook.getAverageRating();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_ID, book_id);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_ISBN, book_isbn);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_TITLE, book_title);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_SUBTITLE, book_subtitle);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHER, book_publisher);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE, book_publishedDate);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_DESCRIPTION, book_description);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_THUMBNAIL, book_thumbnail);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING, book_averageRating);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_AUTHOR,book_author);
        contentValues.put(BookContract.SavedBooksEntry.COLUMN_BOOK_AUTHOR2,book_author2);

        Log.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@_recommended_insert_yeah");
        Uri uri= BookContract.RecommendedBooksEntry.CONTENT_URI;
        Uri uriRowInserted=getActivity().getContentResolver().insert(uri,contentValues);
        Log.i(TAG, "From Content Provider yeah ** Insert_Recommended____ Books : " + uriRowInserted);
    }

}//end of MainActivity....