package com.edufree.bookshoot.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.edufree.bookshoot.database.BookContract.SavedBooksEntry;
import com.edufree.bookshoot.database.BookContract.ClickedBooksEntry;
import com.edufree.bookshoot.database.BookContract.RecommendedBooksEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    private  static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    /*
    private final String SQL_CREATE_COUNTRY_TABLE
            = "CREATE TABLE " + SavedBooksEntry.TABLE_NAME
            + " (" + SavedBooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SavedBooksEntry.COLUMN_BOOK_ISBN + " TEXT NOT NULL, "
            + NationEntry.COLUMN_CONTINENT + " TEXT"
            + ");";
     */

    private final String SQL_CREATE_SAVEDBOOKS_TABLE
            = "CREATE TABLE " + SavedBooksEntry.TABLE_NAME
            + " (" + SavedBooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SavedBooksEntry.COLUMN_BOOK_ID + " TEXT NOT NULL, "
            + SavedBooksEntry.COLUMN_BOOK_ISBN + " TEXT NOT NULL, "
            + SavedBooksEntry.COLUMN_BOOK_TITLE + " TEXT,"
            + SavedBooksEntry.COLUMN_BOOK_SUBTITLE + " TEXT,"
            + SavedBooksEntry.COLUMN_BOOK_AUTHOR + " TEXT,"
            + SavedBooksEntry.COLUMN_BOOK_AUTHOR2 + " TEXT,"
            + SavedBooksEntry.COLUMN_BOOK_PUBLISHER + " TEXT,"
            + SavedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE + " TEXT,"
            + SavedBooksEntry.COLUMN_BOOK_DESCRIPTION + " TEXT,"
            + SavedBooksEntry.COLUMN_BOOK_THUMBNAIL + " TEXT,"
            + SavedBooksEntry.COLUMN_BOOK_AVERAGE_RATING + " TEXT"
            + ");";


    private final String SQL_CREATE_CLICKEDBOOKS_TABLE
            = "CREATE TABLE " + ClickedBooksEntry.TABLE_NAME
            + " (" + ClickedBooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ClickedBooksEntry.COLUMN_BOOK_ID + " TEXT NOT NULL, "
            + ClickedBooksEntry.COLUMN_BOOK_ISBN + " TEXT NOT NULL, "
            + ClickedBooksEntry.COLUMN_BOOK_TITLE + " TEXT,"
            + ClickedBooksEntry.COLUMN_BOOK_SUBTITLE + " TEXT,"
            + ClickedBooksEntry.COLUMN_BOOK_AUTHOR + " TEXT,"
            + ClickedBooksEntry.COLUMN_BOOK_AUTHOR2 + " TEXT,"
            + ClickedBooksEntry.COLUMN_BOOK_PUBLISHER + " TEXT,"
            + ClickedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE + " TEXT,"
            + ClickedBooksEntry.COLUMN_BOOK_DESCRIPTION + " TEXT,"
            + ClickedBooksEntry.COLUMN_BOOK_THUMBNAIL + " TEXT,"
            + ClickedBooksEntry.COLUMN_BOOK_AVERAGE_RATING + " TEXT"
            + ");";

    private final String SQL_CREATE_RECOMMENDEDBOOKS_TABLE
            = "CREATE TABLE " + RecommendedBooksEntry.TABLE_NAME
            + " (" + RecommendedBooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RecommendedBooksEntry.COLUMN_BOOK_ID + " TEXT NOT NULL, "
            + RecommendedBooksEntry.COLUMN_BOOK_ISBN + " TEXT NOT NULL, "
            + RecommendedBooksEntry.COLUMN_BOOK_TITLE + " TEXT,"
            + RecommendedBooksEntry.COLUMN_BOOK_SUBTITLE + " TEXT,"
            + RecommendedBooksEntry.COLUMN_BOOK_AUTHOR + " TEXT,"
            + RecommendedBooksEntry.COLUMN_BOOK_AUTHOR2 + " TEXT,"
            + RecommendedBooksEntry.COLUMN_BOOK_PUBLISHER + " TEXT,"
            + RecommendedBooksEntry.COLUMN_BOOK_PUBLISHED_DATE + " TEXT,"
            + RecommendedBooksEntry.COLUMN_BOOK_DESCRIPTION + " TEXT,"
            + RecommendedBooksEntry.COLUMN_BOOK_THUMBNAIL + " TEXT,"
            + RecommendedBooksEntry.COLUMN_BOOK_AVERAGE_RATING + " TEXT"
            + ");";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SAVEDBOOKS_TABLE);
        db.execSQL(SQL_CREATE_CLICKEDBOOKS_TABLE);
        db.execSQL(SQL_CREATE_RECOMMENDEDBOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Ideally we wouldn't want to delete all of our entries!
        db.execSQL("DROP TABLE IF EXISTS " + SavedBooksEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ClickedBooksEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecommendedBooksEntry.TABLE_NAME);
        onCreate(db);	// Call to create a new db with upgraded schema and version
    }

}

