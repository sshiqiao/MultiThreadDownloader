package com.start.download;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qiaoshi on 2018/1/28.
 */

public class MutiThreadDownloadTask {

    public final static int threadCount = 5;
    public String urlPath;
    public String filePath;
    public List<DowloadAsyncTask> fileDowloaderAsyncTasks = new ArrayList<>();
    public OnProgressUpdateListener onProgressUpdateListener;

    public MutiThreadDownloadTask(String urlPath, String filePath) {
        this.urlPath = urlPath;
        this.filePath = filePath;
    }

    public void startDownload() {
        DownloadInfoAsyncTask fileInfoAsyncTask = new DownloadInfoAsyncTask();
        fileInfoAsyncTask.setOnCompleteListener(new DownloadInfoAsyncTask.OnCompleteListener() {
            @Override
            public void onCompleteListener(int contentLength) {
                long totalContentLength = contentLength;
                long blockSize = (int) Math.ceil((float)totalContentLength/threadCount);

                for(int position =0; position<threadCount; position++) {
                    DowloadAsyncTask fileDowloaderAsyncTask = new DowloadAsyncTask(urlPath, filePath, new DownloadTaskInfo(totalContentLength, blockSize, position));
                    fileDowloaderAsyncTask.setOnProgressUpdateListener(new DowloadAsyncTask.OnProgressUpdateListener() {
                        @Override
                        public void onProgressUpdate(int position, float subProgress, float totalProgress) {
                            fileDowloaderAsyncTasks.get(position).getDownloadTaskInfo().setProgress(totalProgress);
                            if(onProgressUpdateListener!=null) {
                                onProgressUpdateListener.onProgressUpdate(getDownloadTaskProgress(), isDownloadTaskCompleted());
                            }
                        }
                    });
                    fileDowloaderAsyncTask.setOnCompletedListener(new DowloadAsyncTask.OnCompletedListener() {
                        @Override
                        public void onCompleted() {
                            if(onProgressUpdateListener!=null) {
                                onProgressUpdateListener.onProgressUpdate(getDownloadTaskProgress(), isDownloadTaskCompleted());
                            }
                        }
                    });
                    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
                    fileDowloaderAsyncTask.executeOnExecutor(executorService);
                    fileDowloaderAsyncTasks.add(fileDowloaderAsyncTask);
                }
            }
        });
        fileInfoAsyncTask.execute(urlPath);
    }

    public float getDownloadTaskProgress() {
        float progress = 0;
        for(DowloadAsyncTask task : fileDowloaderAsyncTasks) {
            progress+=task.getDownloadTaskInfo().getProgress();
        }
        return progress;
    }

    public boolean isDownloadTaskCompleted() {
        boolean isCompleted = true;
        for(DowloadAsyncTask task : fileDowloaderAsyncTasks) {
            if(task.getDownloadTaskInfo().getStatus() != DownloadTaskInfo.TaskStatus.COMPLETED) {
                isCompleted = false;
                break;
            }
        }
        return isCompleted;
    }

    public void setOnProgressUpdateListener(OnProgressUpdateListener listener) {
        this.onProgressUpdateListener = listener;
    }

    interface OnProgressUpdateListener {
        void onProgressUpdate(float progress, boolean isCompleted);
    }
}
