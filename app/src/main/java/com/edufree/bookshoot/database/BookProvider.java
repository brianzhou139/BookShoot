package com.edufree.bookshoot.database;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import static com.edufree.bookshoot.database.BookContract.CONTENT_AUTHORITY;
import com.edufree.bookshoot.database.BookContract.SavedBooksEntry;
import com.edufree.bookshoot.database.BookContract.ClickedBooksEntry;
import com.edufree.bookshoot.database.BookContract.RecommendedBooksEntry;

public class BookProvider extends ContentProvider {
    private static final String TAG="BookProvider";
    private BookDbHelper databaseHelper;
    //constants
    private static final int BOOKS_SAVED=1;
    private static final int BOOKS_CLICKED=2;
    private static final int BOOKS_RECOMMENDED=3;
    private static final int BOOKS_BOOK_ISBN=10;
    private static final int BOOKS_ID=11;
    private static final UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY,SavedBooksEntry.PATH_SAVED_BOOKS,BOOKS_SAVED);
        uriMatcher.addURI(CONTENT_AUTHORITY,ClickedBooksEntry.PATH_CLICKED_BOOKS,BOOKS_CLICKED);
        uriMatcher.addURI(CONTENT_AUTHORITY,RecommendedBooksEntry.PATH_RECOMMENDED_BOOKS,BOOKS_RECOMMENDED);
        uriMatcher.addURI(CONTENT_AUTHORITY,SavedBooksEntry.PATH_SAVED_BOOKS + "/#",BOOKS_BOOK_ISBN);
        uriMatcher.addURI(CONTENT_AUTHORITY,SavedBooksEntry.PATH_SAVED_BOOKS + "/*",BOOKS_ID);
    }

    @Override
    public boolean onCreate() {
        databaseHelper=new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        SQLiteDatabase database=databaseHelper.getWritableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)){
            case  BOOKS_RECOMMENDED:
                cursor = database.query(RecommendedBooksEntry.TABLE_NAME, strings,null,null, null, null, s1);
                break;
            case BOOKS_CLICKED:
                cursor = database.query(ClickedBooksEntry.TABLE_NAME, strings,null,null, null, null, s1);
                break;
            case BOOKS_BOOK_ISBN:
                cursor = database.query(SavedBooksEntry.TABLE_NAME, strings,s,strings1, null, null, s1);
                break;
            case BOOKS_SAVED:
                cursor = database.query(SavedBooksEntry.TABLE_NAME, strings,null,null, null, null, s1);
                break;
            default:
                throw new IllegalArgumentException(TAG + " Cursor unknown URI : " + uri);

        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        switch (uriMatcher.match(uri)){
            case BOOKS_RECOMMENDED:
                return insertRecord(uri,contentValues, RecommendedBooksEntry.TABLE_NAME);
            case BOOKS_SAVED:
                return insertRecord(uri,contentValues, SavedBooksEntry.TABLE_NAME);
            case BOOKS_CLICKED:
                return insertRecord(uri,contentValues,ClickedBooksEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException(TAG + " Insert unknown URI : " + uri);

        }

    }

    private Uri insertRecord(Uri uri, ContentValues contentValues, String tableName) {
        SQLiteDatabase database=databaseHelper.getWritableDatabase();
        long rowId = database.insert(tableName, null, contentValues);
        Log.i(TAG, "Items inserted in table with row id: " + rowId);

        if(rowId == -1){
            Log.e(TAG," Insert error for URI " + uri);
            return  null;
        }

        return ContentUris.withAppendedId(uri,rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {


        switch (uriMatcher.match(uri)){
            case BOOKS_BOOK_ISBN:
                return deleteRecord(s,strings,SavedBooksEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException(TAG + " Cursor unknown URI : " + uri);
        }

    }

    private int deleteRecord(String s, String[] strings, String tableName) {
        SQLiteDatabase database=databaseHelper.getWritableDatabase();
        return database.delete(tableName, s,strings);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        switch (uriMatcher.match(uri)){
            case BOOKS_SAVED:
                return updateRecord(contentValues,s,strings, SavedBooksEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException(TAG + " Cursor unknown URI : " + uri);
        }

    }

    private int updateRecord(ContentValues contentValues, String s, String[] strings, String tableName) {
        SQLiteDatabase database=databaseHelper.getWritableDatabase();
        return database.update(tableName, contentValues, s, strings);
    }

}
