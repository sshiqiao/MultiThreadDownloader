package com.start.download;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qiaoshi on 2018/1/28.
 */

public class DownloadInfoAsyncTask extends AsyncTask<String, Integer, Object> {

    private OnCompleteListener listener;

    @Override
    protected Object doInBackground(String... params) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(20000);
            if (connection.getResponseCode() == 200) {
                if( listener!=null ) {
                    listener.onCompleteListener(connection.getContentLength());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public void setOnCompleteListener(OnCompleteListener listener){
        this.listener = listener;
    }

    public interface OnCompleteListener {
        void onCompleteListener(int contentLength);
    }
}
