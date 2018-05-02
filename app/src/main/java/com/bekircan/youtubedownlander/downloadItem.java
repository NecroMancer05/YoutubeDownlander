package com.bekircan.youtubedownlander;


import com.google.gson.annotations.Expose;

import java.io.File;

public class downloadItem {

    @Expose
    private int id;
    @Expose
    private long downStatus;
    @Expose
    private String FileUrl;
    @Expose
    private String fileSize;
    @Expose
    private String fileType;
    @Expose
    private String fileName;
    @Expose
    private String fileQuality;
    @Expose
    private String filePath;
    @Expose
    private downloadListener downloadListener;
    private String downPath;

    //TODO downloader causes overflow is there any loop ?
    @Expose
    private Downloader downloader;

    public downloadItem(int id, long downStatus, String fileUrl, String fileSize, String fileType, String fileQuality, String downPath, downloadListener downloadListener) {
        this.id = id;
        this.downStatus = downStatus;
        this.FileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileQuality = fileQuality;
        this.downPath = downPath;
        this.downloadListener = downloadListener;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePatth) {
        this.filePath = filePatth;
    }

    public long getDownStatus() {
        return downStatus;
    }

    public void setDownStatus(long downStatus) {
        this.downStatus = downStatus;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileQuality() {
        return fileQuality;
    }

    public void setFileQuality(String fileQuality) {
        this.fileQuality = fileQuality;
    }

    public String getDownPath() {
        return downPath;
    }

    public void setDownPath(String downPath) {
        this.downPath = downPath;
    }

    public boolean deleteFile() {
        File file = new File(filePath);

        return file.delete();
    }

    public void updateID(int id){
        this.id = id;
        downloader.updateID(id);
    }

    public void downStart(){
        downloader = new Downloader(this, id, downloadListener);
        downloader.start();
    }

    public void update(downloadListener newListenr){
        downloader.updatePref(newListenr);
    }

    public void stop(){
        downloader.stopDownload();
    }

    public void pause(){
        downloader.pauseResume();
    }

}
