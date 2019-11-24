package com.edufree.bookshoot.Loaders;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.edufree.bookshoot.utils.networkUtils;

import java.net.URL;

public class googleApiBookLoader extends AsyncTaskLoader<String> {
    private static final String TAG="googleApiBookLoader";
    private URL address;

    public googleApiBookLoader(@NonNull Context context, URL address) {
        super(context);
        this.address = address;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        URL searchUrl=address;
        String jsonResult=null;

        try {
            jsonResult= networkUtils.getJson(searchUrl);
        }catch (Exception io){
            Log.d(TAG,io.toString());
        }

        return jsonResult;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

}
