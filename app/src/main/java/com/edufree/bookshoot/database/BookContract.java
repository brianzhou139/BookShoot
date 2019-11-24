package com.edufree.bookshoot.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    //public static final String CONTENT_AUTHORITY="com.sriyank.cpdemo.data.NationProvider";
    public static final String CONTENT_AUTHORITY="com.edufree.bookshoot.database.BookProvider";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://" + CONTENT_AUTHORITY);
    //public static final String PATH_BOOKS= "savedbooks";

    public static final class SavedBooksEntry implements BaseColumns {

        // Table Name
        public static final String TABLE_NAME = "savedbooks";
        public static final Uri CONTENT_URI= Uri.withAppendedPath(BASE_CONTENT_URI,TABLE_NAME);
        // Columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_CONTINENT = "continent";
    }



}
