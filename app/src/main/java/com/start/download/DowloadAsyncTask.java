package com.start.download;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qiaoshi on 2018/1/28.
 */

public class DowloadAsyncTask extends AsyncTask<String, Float, ByteArrayOutputStream> {

    private String urlPath;
    private String filePath;
    private DownloadTaskInfo downloadTaskInfo;
    private OnProgressUpdateListener onProgressUpdateListener;
    private OnCompletedListener onCompletedListener;


    public DowloadAsyncTask(String urlPath, String filePath, DownloadTaskInfo downloadTaskInfo) {
        this.urlPath = urlPath;
        this.filePath = filePath;
        this.downloadTaskInfo = downloadTaskInfo;
    }

    @Override
    protected ByteArrayOutputStream doInBackground(String... params) {
        startDownloadTask();
        return null;
    }

    public void startDownloadTask() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestProperty("Range", "bytes=" + downloadTaskInfo.getStartPosition() + "-" + downloadTaskInfo.getEndPosition());
            connection.setConnectTimeout(15000);

            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 206) {
                onTaskStatusChange(DownloadTaskInfo.TaskStatus.DOWNLOADING);
                RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rwd");
                randomAccessFile.seek(downloadTaskInfo.getStartPosition());

                InputStream inputStream = connection.getInputStream();
                int length;
                int currentLength = 0;
                int contentLength = connection.getContentLength();

                byte[] buffer = new byte[1024*1000];
                while ((length = inputStream.read(buffer)) != -1) {
                    currentLength += length;
                    if (contentLength > 0) {
                        publishProgress(((float)currentLength / contentLength * 100), ((float) currentLength / downloadTaskInfo.getTotalContentLength() * 100));
                    }
                    randomAccessFile.write(buffer, 0, length);
                }
            }else {
                onTaskStatusChange(DownloadTaskInfo.TaskStatus.FAILED);
            }
        } catch (Exception e) {
            onTaskStatusChange(DownloadTaskInfo.TaskStatus.FAILED);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
        if (onProgressUpdateListener!=null) {
            onProgressUpdateListener.onProgressUpdate(downloadTaskInfo.getBlockPosition(), values[0], values[1]);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        onTaskStatusChange(DownloadTaskInfo.TaskStatus.READY);
    }

    @Override
    protected void onPostExecute(ByteArrayOutputStream outputStream) {
        super.onPostExecute(outputStream);
        if (downloadTaskInfo.getStatus() != DownloadTaskInfo.TaskStatus.CANCELED && downloadTaskInfo.getStatus() != DownloadTaskInfo.TaskStatus.FAILED) {
            onTaskStatusChange(DownloadTaskInfo.TaskStatus.COMPLETED);
            if (onCompletedListener!=null) {
                onCompletedListener.onCompleted();
            }
        }

    }

    @Override
    protected void onCancelled(ByteArrayOutputStream outputStream) {
        super.onCancelled(outputStream);
        onTaskStatusChange(DownloadTaskInfo.TaskStatus.CANCELED);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        onTaskStatusChange(DownloadTaskInfo.TaskStatus.CANCELED);
    }

    public DownloadTaskInfo getDownloadTaskInfo() {
        return downloadTaskInfo;
    }

    public void onTaskStatusChange(DownloadTaskInfo.TaskStatus taskStatus) {
        downloadTaskInfo.setStatus(taskStatus);
        switch (taskStatus) {
            case CANCELED:
            case FAILED:
                downloadTaskInfo.addRetryCount();
                if (downloadTaskInfo.shouldRetry()) {
                    startDownloadTask();
                }
                break;
            default:
                break;
        }
    }

    public void setOnProgressUpdateListener(OnProgressUpdateListener listener) {
        this.onProgressUpdateListener = listener;
    }

    interface OnProgressUpdateListener {
        void onProgressUpdate(int position, float subProgress, float totalProgress);
    }

    public void setOnCompletedListener(OnCompletedListener listener) {
        this.onCompletedListener = listener;
    }

    interface OnCompletedListener {
        void onCompleted();
    }

}
