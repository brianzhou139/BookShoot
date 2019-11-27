package com.edufree.bookshoot.database;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {
    //public static final String CONTENT_AUTHORITY="com.sriyank.cpdemo.data.NationProvider";
    public static final String CONTENT_AUTHORITY="com.edufree.bookshoot.database.BookProvider";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class SavedBooksEntry implements BaseColumns {
        // Table Name
        public static final String PATH_SAVED_BOOKS= "savedbooks";
        public static final String TABLE_NAME = "savedbooks";
        public static final Uri CONTENT_URI= Uri.withAppendedPath(BASE_CONTENT_URI,TABLE_NAME);

        // Columns ..............
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_ISBN= "isbn";
        public static final String COLUMN_BOOK_ID = "book_id";
        public static final String COLUMN_BOOK_TITLE= "title";
        public static final String COLUMN_BOOK_SUBTITLE= "subtitle";
        public static final String COLUMN_BOOK_AUTHOR= "author";
        public static final String COLUMN_BOOK_AUTHOR2= "author2";
        public static final String COLUMN_BOOK_PUBLISHER= "publisher";
        public static final String COLUMN_BOOK_PUBLISHED_DATE= "publisherDate";
        public static final String COLUMN_BOOK_DESCRIPTION= "description";
        public static final String COLUMN_BOOK_THUMBNAIL= "thumbnail";
        public static final String COLUMN_BOOK_AVERAGE_RATING= "averageRating";

    }

    public static final class ClickedBooksEntry implements BaseColumns {
        // Table Name
        public static final String PATH_CLICKED_BOOKS= "clickedbooks";
        public static final String TABLE_NAME = "clickedbooks";
        public static final Uri CONTENT_URI= Uri.withAppendedPath(BASE_CONTENT_URI,TABLE_NAME);
        // Columns ..............
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_ISBN= "isbn";
        public static final String COLUMN_BOOK_ID = "book_id";
        public static final String COLUMN_BOOK_TITLE= "title";
        public static final String COLUMN_BOOK_SUBTITLE= "subtitle";
        public static final String COLUMN_BOOK_AUTHOR= "author";
        public static final String COLUMN_BOOK_AUTHOR2= "author2";
        public static final String COLUMN_BOOK_PUBLISHER= "publisher";
        public static final String COLUMN_BOOK_PUBLISHED_DATE= "publisherDate";
        public static final String COLUMN_BOOK_DESCRIPTION= "description";
        public static final String COLUMN_BOOK_THUMBNAIL= "thumbnail";
        public static final String COLUMN_BOOK_AVERAGE_RATING= "averageRating";

    }


    public static final class RecommendedBooksEntry implements BaseColumns {
        // Table Name
        public static final String PATH_RECOMMENDED_BOOKS= "recommendedbooks";
        public static final String TABLE_NAME = "recommendedbooks";
        public static final Uri CONTENT_URI= Uri.withAppendedPath(BASE_CONTENT_URI,TABLE_NAME);
        // Columns ..............
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_ISBN= "isbn";
        public static final String COLUMN_BOOK_ID = "book_id";
        public static final String COLUMN_BOOK_TITLE= "title";
        public static final String COLUMN_BOOK_SUBTITLE= "subtitle";
        public static final String COLUMN_BOOK_AUTHOR= "author";
        public static final String COLUMN_BOOK_AUTHOR2= "author2";
        public static final String COLUMN_BOOK_PUBLISHER= "publisher";
        public static final String COLUMN_BOOK_PUBLISHED_DATE= "publisherDate";
        public static final String COLUMN_BOOK_DESCRIPTION= "description";
        public static final String COLUMN_BOOK_THUMBNAIL= "thumbnail";
        public static final String COLUMN_BOOK_AVERAGE_RATING= "averageRating";
    }


    /*
    private String isbn;
    private String id;
    private String title;
    private String subtitle;
    private String[] authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private String thumbnail;
    private String averageRating;
    */


}
