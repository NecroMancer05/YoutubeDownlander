package com.bekircan.youtubedownloader;

import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class pureDownload extends Thread{

    private int fileID;
    private String fileUrl;
    private String fileExtension;
    private String fileSize;
    private String fileQuality;
    private String customDownPath;
    private downloadListener downloadListener;
    private String fileName;
    private String filePath;
    private int status;

    private boolean running = true;
    private boolean paused = false;
    private boolean idUpdate = false;

    private FileOutputStream fileOutputStream;

    public pureDownload(int fileID, String fileUrl, String fileExtension, String fileSize, String customDownPath, String fileQuality, com.bekircan.youtubedownloader.downloadListener downloadListener) {
        this.fileID = fileID;
        this.fileUrl = fileUrl;
        this.fileQuality = fileQuality;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.customDownPath = customDownPath;
        this.downloadListener = downloadListener;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFileID() {
        return fileID;
    }


    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getCustomDownPath() {
        return customDownPath;
    }

    public void setCustomDownPath(String customDownPath) {
        this.customDownPath = customDownPath;
    }

    public com.bekircan.youtubedownloader.downloadListener getDownloadListener() {
        return downloadListener;
    }

    public void setDownloadListener(com.bekircan.youtubedownloader.downloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }


    //downloader

    public void stopDownload(){
        running = false;
    }

    public void pauseResume(){

        if (!paused){
            paused = true;
        }else {
            synchronized (fileOutputStream){
                paused = false;
                fileOutputStream.notify();
            }
        }
    }


    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();

        String fileName;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            /*
            String fileName = URLUtil.guessFileName(getURL,null,null);
            Log.d("Downloader","File name: " + fileName);
            */

            int length = httpURLConnection.getContentLength();

            String contentName = httpURLConnection.getHeaderField("Content-Disposition");

            if (contentName != null){
                fileName = contentName.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
            }else{
                fileName = URLUtil.guessFileName(fileUrl,null,null);
            }

            //downloadItem.setFileName(fileName);

            this.fileName = fileName;

            //downloadItems.get(index).setFileName(fileName);
            //downloadItems.get(index).setFileSize(String.valueOf(length));
            //downloadItems.get(index).setDownStatus(-1);

            //set down directory
            String downloadDirectory = Environment.getExternalStorageDirectory().getPath();
            if (customDownPath.equals("")){
                filePath = downloadDirectory + "/" + fileName;
            }else {
                filePath = downloadDirectory + "/" + customDownPath + "/" +fileName;
            }

            Log.d("downpath", "custom down path" + filePath);


            File file = new File(filePath.replace(fileName, "/"));
            if (!file.exists()){
                file.mkdirs();
            }

            File outputFile = new File(file, fileName);

            //File output = new File(file, downloadItem.getFilePath());
            //FileOutputStream fileOutputStream = new FileOutputStream(output);

            /*
                File file = new File(PATH);
                file.mkdirs();
                String fileName = mp3;

                File outputFile = new File(file , fileName);
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();
             */

            //File outputFile = new File(getURL.substring(getURL.indexOf("@download/") + 11, getURL.indexOf("@download/") + 20));
            //String name = getURL.substring(getURL.indexOf("@download/") + 11, getURL.indexOf("@download/") + 20) + ".mp3";

            //FileOutputStream fileOutputStream = new FileOutputStream(downloadDirectory + "/" + fileName);
            fileOutputStream = new FileOutputStream(outputFile);

            InputStream inputStream = httpURLConnection.getInputStream();

            byte[] buffer = new byte[4096];

            int len = 0;
            long total = 0;


            while ((len = inputStream.read(buffer)) != -1 && running){

                synchronized (fileOutputStream) {

                    if (!running){
                        break;
                    }
                    if (paused){
                        fileOutputStream.wait();
                        if (!running){
                            break;
                        }
                    }else{

                        /*
                        if (idUpdate){
                            index = downloadItem.getId();
                            idUpdate = false;
                        }
                        */

                        fileOutputStream.write(buffer,0,len);
                        total += len;

                        setStatus((int) (total*100)/length);
                        //this.status = (int) (total*100)/length;
                        downloadListener.updateProgress(fileID, (total*100)/length);

                        if ((total * 100)/length == 100){
                            downloadListener.isFinish(fileID);
                        }

                    }

                }

            }

            fileOutputStream.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }





}
