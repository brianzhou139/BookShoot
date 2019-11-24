package com.edufree.bookshoot.utils;
import android.net.Uri;
import android.util.Log;

import com.edufree.bookshoot.models.Book;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class networkUtils {
    /*********************************************************************************************************************/
    /******************************   GOOGLE API CONSTANTS YEAH        ****************************************************/
    /*********************************************************************************************************************/
    public static final String TAG="ApiUtil";
    public static final String BASE_API_URL="https://www.googleapis.com/books/v1/volumes";
    private static final String QUERY_PARAMETER_KEY ="q";
    private static final String KEY="key";
    private static final String API_KEY="AIzaSyC1L5KH0baDylHl9d05bpsq7x8Cpy0s-I0";
    private static String VOLUMEINFOR="volumeInfo";
    ///for seahc functionalty
    private static String TITLE="intitle:";
    private static String AUTHOR="inauthor:";
    private static String PUBLISHER="inpublisher:";
    private static String ISBN="isbn:";
    /*********************************************************************************************************************/
    /*********************************************************************************************************************/

    private networkUtils(){}

    public static ArrayList<Book> getBooksFromJson(String json){
        final String ID="id";
        final String TITLE="title";
        final String SUBTITLE="subtitle";
        final String AUTHORS="authors";
        final String PUBLISHER="publisher";
        final String PUBLISHED_DATE="publishedDate";
        final String DESCRIPTION="description";
        final String IMAGELINKS="imageLinks";
        final String THUMBNAIL="thumbnail";
        final String AVERAGERATING="averageRating";
        final String INDEUSTRYIDENTFIER="industryIdentifiers";
        final String IDENTIFIER="identifier";

        final String ITEMS="items";
        ArrayList<Book> books=new ArrayList<>();

        try {
            //get s json object from the data retrieved on net
            JSONObject jsonBooks=new JSONObject(json);
            JSONArray arrayBooks=jsonBooks.getJSONArray(ITEMS);

            int numberOfBooks=arrayBooks.length();

            for(int i=0;i<numberOfBooks;i++){
                JSONObject bookJson=arrayBooks.getJSONObject(i);
                JSONObject volumeInforJson=bookJson.getJSONObject(VOLUMEINFOR);
                JSONObject imageLinksJSON=null;

                if(volumeInforJson.has(IMAGELINKS)){
                    imageLinksJSON=volumeInforJson.getJSONObject(IMAGELINKS);
                }

                int authorNum=0;

                try{
                    authorNum=volumeInforJson.getJSONArray(AUTHORS).length();
                }catch (Exception no_num){
                    authorNum=0;
                }
                //int authorNum=volumeInforJson.getJSONArray(AUTHORS).length();
                JSONArray auths=volumeInforJson.getJSONArray(AUTHORS);
                String[] authors_temp=new String[authorNum];

                for(int j=0;j<authorNum;j++){
                    authors_temp[j]=auths.get(j).toString();
                }

                String isbn_temp=null;
                JSONArray arrayIdentifier=volumeInforJson.getJSONArray(INDEUSTRYIDENTFIER);
                int ind_len=arrayIdentifier.length();

                if(ind_len>0){
                    JSONObject Object_identifier=arrayIdentifier.getJSONObject(0);
                    isbn_temp=Object_identifier.getString(IDENTIFIER);
                }

                Book book=new Book(
                        bookJson.getString(ID),
                        (volumeInforJson.isNull(TITLE)?"":volumeInforJson.getString(TITLE)),
                        (volumeInforJson.isNull(SUBTITLE)?"":volumeInforJson.getString(SUBTITLE)),
                        authors_temp,
                        (volumeInforJson.isNull(PUBLISHER)?"":volumeInforJson.getString(PUBLISHER)),
                        (volumeInforJson.isNull(PUBLISHED_DATE)?"":volumeInforJson.getString(PUBLISHED_DATE)),
                        (volumeInforJson.isNull(DESCRIPTION)?"":volumeInforJson.getString(DESCRIPTION)),
                        (imageLinksJSON==null?"":imageLinksJSON.getString(THUMBNAIL)),
                        (volumeInforJson.isNull(AVERAGERATING)?"":volumeInforJson.getString(AVERAGERATING))
                );

                book.setIsbn(isbn_temp);
                books.add(book);
            }

        }catch (Exception ie){
            Log.d(TAG,ie.toString());
            Log.d(TAG,"Here dude fix me pleasessss");
        }

        return books;
    }

    public static URL buildURL(String title){
        URL url=null;

        Uri uri=Uri.parse(BASE_API_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,title)
                .appendQueryParameter(KEY,API_KEY)
                .build();
        try {
            url=new URL(uri.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildURL(String author,int type){
        if(type==2){
            URL url=null;

            StringBuilder sb=new StringBuilder();

            if(!author.isEmpty()){sb.append(AUTHOR+author); }
            String query=sb.toString();

            Uri uri=Uri.parse(BASE_API_URL)
                    .buildUpon()
                    .appendQueryParameter(QUERY_PARAMETER_KEY,query)
                    .appendQueryParameter(KEY,API_KEY)
                    .build();
            try {
                url=new URL(uri.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return url;
        }else{
            return null;
        }

    }

    public static URL buildURL(String title,String author,String publisher,String isbn)
    {
        URL url=null;


        //https://www.googleapis.com/books/v1/volumes?q=flowers+inauthor:keyes&key=yourAPIKey
        //https://www.googleapis.com/books/v1/volumes?q=flowers&key=AIzaSyC1L5KH0baDylHl9d05bpsq7x8Cpy0s-I0
        StringBuilder sb=new StringBuilder();

        if(!title.isEmpty()){sb.append(TITLE+title+"+"); }
        if(!author.isEmpty()){sb.append(AUTHOR+author+"+"); }
        if(!publisher.isEmpty()){sb.append(PUBLISHER+publisher+"+"); }
        if(!isbn.isEmpty()){sb.append(ISBN+isbn+"+"); }
        sb.setLength(sb.length()-1);

        String query=sb.toString();

        Uri uri=Uri.parse(BASE_API_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,query)
                .appendQueryParameter(KEY,API_KEY)
                .build();
        try {
            url=new URL(uri.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getJson(URL url){
        HttpURLConnection connection=null;
        InputStream stream=null;

        try{
            connection=(HttpURLConnection)url.openConnection();
            stream=connection.getInputStream();

            Scanner scanner=new Scanner(stream);
            scanner.useDelimiter("\\A");

            boolean hasData=scanner.hasNext();
            if(hasData){
                return scanner.next();
            }else {
                return null;
            }

        }catch (Exception io){
            Log.d(TAG,io.toString());
            return null;
        }finally {
            connection.disconnect();
        }

    }


}
