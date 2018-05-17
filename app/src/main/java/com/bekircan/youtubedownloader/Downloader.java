package com.bekircan.youtubedownloader;

import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;

public class Downloader extends Thread implements Runnable{

    @Expose
    private int index;
    @Expose
    private downloadListener downloadListener;
    private downloadItem downloadItem;

    private boolean running = true;
    private boolean paused ;
    private boolean idUpdate = false;

    public Downloader(downloadItem downloadItem, int index, downloadListener downloadListener, boolean paused) {
        this.downloadItem = downloadItem;
        this.paused = paused;
        this.index = index;
        this.downloadListener = downloadListener;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    public void stopDownload(){
        running = false;
    }

    public void pauseResume(){

        if(downloadItem.isPaused()){

            synchronized (downloadItem){

                paused = false;
                downloadItem.notify();
            }

        }else {
            paused = true;
        }
        /*
        if (!paused){
            paused = true;
        }else {
            synchronized (downloadItem){
                paused = false;
                downloadItem.notify();
            }
        }
        */
    }

    public void updateID(int id){

        this.index = id;
        idUpdate = true;

    }

    public void updatePref(downloadListener downloadListener){
        this.downloadListener = downloadListener;
    }

    @Override
    public void run() {
        super.run();

        String fileName;
        try {
            URL url = new URL(downloadItem.getFileUrl());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            Log.d("link", downloadItem.getFileUrl());
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
                fileName = URLUtil.guessFileName(downloadItem.getFileUrl(),null,null);
            }

            downloadItem.setFileName(fileName);
            //downloadItems.get(index).setFileName(fileName);
            //downloadItems.get(index).setFileSize(String.valueOf(length));
            //downloadItems.get(index).setDownStatus(-1);

            //set down directory
            String downloadDirectory = Environment.getExternalStorageDirectory().getPath();
            if (downloadItem.getDownPath().equals("")){
                downloadItem.setFilePath(downloadDirectory + "/" + fileName);
            }else {
                downloadItem.setFilePath(downloadDirectory + "/" + downloadItem.getDownPath() + "/" +fileName);
            }

            Log.d("downPath", "custom down path" + downloadItem.getFilePath());




            File file = new File(downloadItem.getFilePath().replace(downloadItem.getFileName(), "/"));
            if (!file.exists()){
                file.mkdirs();
            }

            File outputFile = new File(file, downloadItem.getFileName());

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
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

            InputStream inputStream = httpURLConnection.getInputStream();

            byte[] buffer = new byte[4096];

            int len = 0;
            long total = 0;


            while ((len = inputStream.read(buffer)) != -1 && running){

                synchronized (downloadItem) {

                    if (!running){
                        break;
                    }
                    if (paused){
                        downloadItem.wait();
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

                        /*
                        fileOutputStream.write(buffer,0,len);
                        total += len;
                        downloadListener.updateProgress(downloadItem.getId(), (total*100)/length);
                        downloadItem.setDownStatus((total*100)/length);

                        //downloadItems.get(index).setDownStatus((total*100)/length);

                        if ((total * 100)/length == 100){
                            downloadListener.isFinish(downloadItem.getId());
                        }
                        */
                    }


                    fileOutputStream.write(buffer,0,len);
                    total += len;
                    downloadListener.updateProgress(downloadItem.getId(), (total*100)/length);
                    downloadItem.setDownStatus((total*100)/length);

                    //downloadItems.get(index).setDownStatus((total*100)/length);

                    if ((total * 100)/length == 100){
                        downloadListener.isFinish(downloadItem.getId());
                    }

                }

            }

            //Close streams
            fileOutputStream.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("Exception", "FileNotFound");
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.d("Exception", "Protocol");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("Exception", "UrlException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Exception", "IO");
            downloadItem.setError(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Exception", "Interrupt");
        }

    }
}
