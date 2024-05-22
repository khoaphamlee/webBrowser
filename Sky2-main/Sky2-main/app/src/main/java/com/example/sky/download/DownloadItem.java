package com.example.sky.download;

public class DownloadItem {
    private String fileName;
    private long fileSize;
    private String fileDate;
    private String fileUri;

    public DownloadItem(String fileName, long fileSize, String fileDate, String fileUri) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileDate = fileDate;
        this.fileUri = fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileDate() {
        return fileDate;
    }

    public String getFileUri() {
        return fileUri;
    }
}
