package com.start.download;

/**
 * Created by qiaoshi on 2018/1/28.
 */

public class DownloadTaskInfo {
    public final static int MAX_RETRY_NUM = 3;
    public enum TaskStatus {
        WAITING,
        READY,
        DOWNLOADING,
        CANCELED,
        FAILED,
        COMPLETED
    }

    private long totalContentLength;
    private long blockSize;
    private int blockPosition;

    private long startPosition;
    private long endPosition;

    private float progress = 0;
    private int retryCount = 0;
    private TaskStatus status = TaskStatus.WAITING;

    public DownloadTaskInfo(long totalContentLength, long blockSize, int blockPosition) {

        this.totalContentLength = totalContentLength;
        this.blockSize = blockSize;
        this.blockPosition = blockPosition;

        this.startPosition = blockSize * blockPosition;
        this.endPosition = blockSize * blockPosition + blockSize;

    }

    public void addRetryCount() {
        if (retryCount < MAX_RETRY_NUM) {
            retryCount ++;
        }
    }

    public boolean shouldRetry() {
        return retryCount < MAX_RETRY_NUM ? true : false;
    }

    public long getTotalContentLength() {
        return totalContentLength;
    }

    public void setTotalContentLength(long totalContentLength) {
        this.totalContentLength = totalContentLength;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public int getBlockPosition() {
        return blockPosition;
    }

    public void setBlockPosition(int blockPosition) {
        this.blockPosition = blockPosition;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(long endPosition) {
        this.endPosition = endPosition;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
