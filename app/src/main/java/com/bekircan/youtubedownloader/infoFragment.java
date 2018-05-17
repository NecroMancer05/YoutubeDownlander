package com.bekircan.youtubedownloader;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ACTIVITY_SERVICE;

//import static com.bekircan.youtubedownlander.downloaderService.downloadItems;
//import static com.bekircan.youtubedownlander.downloaderService.indexCounter;


public class infoFragment extends android.support.v4.app.Fragment implements pathDialog.getPathListener, PopupMenu.OnMenuItemClickListener{

    private static final String LINK_HEAD = "&index=";
    private static final String LINK_HEAD2 = "&list=";
    private static final String YOUTUBE_HEAD = "youtube.com/watch?v=";
    private static final String YOUTUBE_HEAD2 = "youtu.be/";
    private static final String YOUTUBE_TIME_HEAD = "?t=";
    private static final String BODY_TEXT = "<body>";
    private static final String DIV_TEXT = "</div></div></a>";
    private static final String HEAD = "//youtubemp3api.com";
    private static final String END_TEXT_MP3 = ".mp3\">";
    private static final String BIT_RATE_TEXT = "<div class=\"btn-bitrate\">";
    private static final String BIT_RATE = " kbps</div>";
    private static final String SIZE_TEXT = "=\"btn-filesize\">";
    private static final String SIZE_TEXT_END = "B</div>";
    private static final String SCRIPT_TEXT = "<script>";
    private static final String KBPS = " Kbps";
    private static final String BYTE = "B";
    private static final String MP3 = ".mp3";
    private static final String MP4 = ".mp4";
    private static final String HTTPS_LINK = "https:";
    private static final String MP3_EXTENSION = "mp3/";
    private static final String VIDEO_EXTENSION = "videos/";
    private static final String DOWNLOAD_LINK = "https://youtubemp3api.com/@api/button/";
    private static final String FILE_TYPE_HEAD = "\"btn-filetype\">";
    private static final String FILE_QUALITY_HEAD = "\"btn-quality\">";
    private static final String END_TEXT_VIDEOS = "\" download=\"\">";
    private static final String VIDEO_FORMAT_EXTENSIONS = ".mp4";
    private static final String VIDEO_FORMAT_EXTENSIONS2 = ".webm";
    private static final String VIDEO_FORMAT_EXTENSIONS3 = ".3gp";
    private static final String NO_STREAM = "\"Error: No Streams found!\"";
    private static final String SCRIPT_BEGIN = "<script data-cfasync=\"false\" type=\"text/javascript\">";
    private static final String SCRIPT_END = "</script><script src=\"//go.oclasrv.com/apu.php?zoneid=1464911\" data-cfasync=\"false\" async onerror=\"_sqtxoxvz()\" onload=\"_pgqho()\"></script>";
    private static final String DOWNLOADED_TEXT = "\t\tDonwloaded : ";
    private static final String DOWNLOADING_TEXT = "\t\t\tDownloading : ";

    private static final int SERVICE_DELAY = 10000;


    private static int indexCounter = 0;
    private static int downloadedCounter = 0;
    private static int downloadingCounter = 0;
    private static int menuCounter = 0;

    private WebView webView;

    private EditText linkEditText;
    private TextView statsText;
    private FloatingActionButton downloadButton;

    private String youtubeID = null;
    private String sharedYtLink = null;
    private String htmlData;
    private String downloadLink;
    private String old = null;
    private String stat = "";
    private String downloadPath = "";


    private boolean dataOK = false;
    private boolean downloadType;       //true for mp3 false for videos
    private static boolean dialogOpen = false;

    private AlertDialog.Builder builder;
    private AlertDialog.Builder listBuilder;
    private ProgressDialog progressDialog;

    private ArrayList<downloadLinks> downloadLinks = new ArrayList<>();

    //TODO service with import static com.bekircan.youtubedownlander.infoFragment.downloadItems; lul that look good idea but idk its bad
    private static ArrayList<downloadItem> downloadItems = new ArrayList<>();
    private static ArrayList<notifyDownload> notifyDownloads = new ArrayList<>();

    private static ArrayList<Downloader> downloaders = new ArrayList<>();
    private ArrayAdapter<String> dialogAdapter;

    private static ArrayList<pureDownload> pureDownloads = new ArrayList<>();
    private List<pureDownload> pureDownloadList = new ArrayList<>();

    private static downloadListener downloadListener;
    private itemDeleted itemDeleted;

    private downloadAdapter downloadAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    //private adapter adapter;

    private myWebClient myWebClient;

    private ImageView imageView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment, container, false);

        loadData();


        if (!isServiceRunning()) {

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                   startService();
                }
            },SERVICE_DELAY);
        }



        //setHasOptionsMenu(true);

        //TODO https://stackoverflow.com/questions/13401632/android-app-crashed-on-screen-rotation-with-dialog-open
        //setRetainInstance(true);

        //TODO add notification
        //TODO do activities is stacking ! this causes the dont get shared link !
        //TODO Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK

        linkEditText = view.findViewById(R.id.editText);
        downloadButton = view.findViewById(R.id.button_download);
        statsText = view.findViewById(R.id.queueLine);
        imageView = view.findViewById(R.id.button_menu);


        /*
        if(getArguments() != null) {

            sharedYtLink = getArguments().getString("sharedLink");
            linkEditText.setText(sharedYtLink);
        }
        */


        recyclerView = view.findViewById(R.id.recyc);
        recyclerView.hasFixedSize();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        downloadAdapter = new downloadAdapter(downloadItems, itemDeleted, getView());
        recyclerView.setAdapter(downloadAdapter);

        linkEditText.setOnEditorActionListener(getEditorListener);

        //keyboard never opens auto
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        checkPermission();

        barStats();

        /*
        if (indexCounter != 0){

            barStats();
            //updateDownStats();
        }
        */


        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        while (true){
                            if (pureDownloadList != null && adapter != null){
                                adapter.updateList(pureDownloadList);
                            }
                        }

                    }
                });

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        */

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        //linkEditText = getView().findViewById(R.id.editText);
        //linkEditText.setOnEditorActionListener(getEditorListener);

        if(getArguments() != null) {

            sharedYtLink = getArguments().getString("sharedLink");
            linkEditText.setText(sharedYtLink);
        }
    }


    private void startService() {

        if (getActivity() != null) {

            Intent serviceIntent = new Intent(getContext(), downloaderService.class);
            getActivity().startService(serviceIntent);
        }
    }


    private void stopService() {

       if (getActivity() != null) {
           Intent serviceIntent = new Intent(getContext(), downloaderService.class);
           getActivity().stopService(serviceIntent);

           //getActivity().finish();
           getActivity().finishAffinity();
       }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        saveData();
    }

    private boolean isServiceRunning() {

        if (getActivity() != null) {
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
                if("com.bekircan.youtubedownloader.downloaderService".equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveData() {

        /*
        if (getActivity() != null){
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

            String downItems = gson.toJson(downloadItems);
            //String downlands = gson.toJson(downloaders);
            editor.putString("items", downItems);
            //editor.putString("downlands", downlands);
            editor.apply();
        }
        */

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("download location", downloadPath);
        editor.apply();

    }

    private void loadData() {

        /*
        if (getActivity() != null){
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String downItems = sharedPreferences.getString("items", null);
            //String downlands = sharedPreferences.getString("downlands", null);

            Type item = new TypeToken<ArrayList<downloadItem>>() {}.getType();
            //Type downland = new TypeToken<ArrayList<Downloader>>() {}.getType();

            downloadItems = gson.fromJson(downItems, item);
            //downloaders = gson.fromJson(downlands, downland);

            if (downloadItems == null){
                downloadItems = new ArrayList<>();
            }

            if (downloaders == null){
                downloaders = new ArrayList<>();
            }

            downloadAdapter = new downloadAdapter(downloadItems, itemDeleted);
            downloadAdapter.notifyDataSetChanged();
        }
        */

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        downloadPath = sharedPreferences.getString("download location", "");

    }



    private void updateDownStats(){

        stat = DOWNLOADED_TEXT + downloadedCounter + DOWNLOADING_TEXT + downloadingCounter;
        statsText.setText(stat);
    }

    private void setDownloadItems() {

        int count = 0;
        for (downloadItem ıtem : downloadItems){
            downloaders.get(count).updatePref(downloadListener);
            count++;
        }

    }

    private void updatePrefs(){

        for (downloadItem downloadItem : downloadItems){
            downloadItem.update(downloadListener);
        }
    }

    private void closeKeyboard(){

        View view = getView().findFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextView.OnEditorActionListener getEditorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId){
                case EditorInfo.IME_ACTION_SEARCH:

                    closeKeyboard();

                    sharedYtLink = linkEditText.getText().toString();

                    if(getYoutubeID(sharedYtLink)){

                        builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Download As : ");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Mp3", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadLink = DOWNLOAD_LINK + MP3_EXTENSION + youtubeID;

                                webView = new WebView(getContext());
                                downloadType = true;
                                getHtml(downloadLink);

                                getLinksFromHtml getLinks = new getLinksFromHtml();
                                getLinks.execute();

                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadLink = DOWNLOAD_LINK + VIDEO_EXTENSION + youtubeID;

                                webView = new WebView(getContext());
                                downloadType = false;
                                getHtml(downloadLink);

                                getLinksFromHtml getLinks = new getLinksFromHtml();
                                getLinks.execute();

                                dialog.dismiss();

                            }
                        });

                        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();

                    }else {
                        Toast.makeText(getContext(), "Invalid youtube link ..", Toast.LENGTH_SHORT).show();
                    }
            }

            return false;
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMenu(v);
                //v.showContextMenu();
                /*
                menuCounter++;
                if (menuCounter % 2 == 0 ){
                    Toast.makeText(getContext(), "Long press for open menu", Toast.LENGTH_SHORT).show();
                }
                */

                //registerForContextMenu(v.findViewById(R.id.button_menu));

                /*
                if (getActivity() != null){
                    //getActivity().openOptionsMenu();
                }
                */
            }
        });


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedYtLink = linkEditText.getText().toString();

                if(getYoutubeID(sharedYtLink)){

                    closeKeyboard();

                    builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Download As : ");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Mp3", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadLink = DOWNLOAD_LINK + MP3_EXTENSION + youtubeID;

                            webView = new WebView(getContext());
                            downloadType = true;
                            getHtml(downloadLink);

                            getLinksFromHtml getLinks = new getLinksFromHtml();
                            getLinks.execute();

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadLink = DOWNLOAD_LINK + VIDEO_EXTENSION + youtubeID;

                            webView = new WebView(getContext());
                            downloadType = false;
                            getHtml(downloadLink);

                            getLinksFromHtml getLinks = new getLinksFromHtml();
                            getLinks.execute();

                            dialog.dismiss();

                        }
                    });

                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();

                }else if (sharedYtLink.isEmpty()){
                    Toast.makeText(getContext(), "Enter a link ..", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Invalid youtube link ..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });


        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (downloadItem ıtem : downloadItems){
                    Log.d("data",ıtem.getFileName() + " % " + ıtem.getDownStatus());
                    downloadAdapter.notifyDataSetChanged();
                }
            }
        });
        */


        /*
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedYtLink = linkEditText.getText().toString();

                if(getYoutubeID(sharedYtLink)){

                    closeKeyboard();

                    builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Download As : ");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Mp3", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadLink = DOWNLOAD_LINK + MP3_EXTENSION + youtubeID;

                            webView = new WebView(getContext());
                            downloadType = true;
                            getHtml(downloadLink);

                            getLinksFromHtml getLinks = new getLinksFromHtml();
                            getLinks.execute();

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadLink = DOWNLOAD_LINK + VIDEO_EXTENSION + youtubeID;

                            webView = new WebView(getContext());
                            downloadType = false;
                            getHtml(downloadLink);

                            getLinksFromHtml getLinks = new getLinksFromHtml();
                            getLinks.execute();

                            dialog.dismiss();

                        }
                    });

                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();

                }else {
                    Toast.makeText(getContext(), "Invalid youtube link ..", Toast.LENGTH_SHORT).show();
                }
            }
        });

         */

        downloadListener = new downloadListener() {

            @Override
            public void updateProgress(final int id, final long percent) {

                if (getActivity() != null){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("updateProgress","id: " + id + percent);
                            //notifyDownloads.get(id).updateNotify();

                            /*
                            adapter.updateList(pureDownloadList);
                            adapter.notifyDataSetChanged();
                            */

                           if (downloadItems.size() > id){
                               //downloadItems.get(id).setDownStatus(percent);
                               downloadAdapter.notifyItemChanged(id);
                           }

                        }
                    });
                }

            }

            @Override
            public void isFinish(final int id) {

                //TODO barStats not working correctly
                downloadedCounter++;
                downloadingCounter--;
                barStats();
                notifyDownloads.get(id).finishNotify();


                /*
                if (getView() != null) {

                    Snackbar.make(getView(),downloadItems.get(id).getFileName() + "Finished", Snackbar.LENGTH_LONG).show();
                }
                */

                if (getActivity() != null){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //updateDownStats();
                            Log.d("isFinish", "finish");
                        }
                    });
                }
            }
        };


        itemDeleted = new itemDeleted() {
            @Override
            public void deleted(final int position, final int downStat) {

                indexCounter--;

                if (downStat == 100) {
                    downloadedCounter--;
                }else {
                    downloadingCounter--;
                }

                if (getActivity() != null){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (indexCounter == 0){
                                statsText.setText("");
                            }

                            //updateIDs();
                            barStats();
                            //updateDownStats();
                            downloadAdapter.updateDeleteListener(itemDeleted);

                            //downloadAdapter = new downloadAdapter(downloadItems, itemDeleted);
                            //downloadAdapter.notifyDataSetChanged();
                        }
                    });

                }

            }
        };


        //Update prefs after rotate or re share
        updatePrefs();
        downloadAdapter.updateDeleteListener(itemDeleted);




    }

    //TODO need rework too :(
    private void barStats() {


        if (getActivity() != null) {
           getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   if (downloadedCounter != 0 || downloadingCounter != 0) {

                       stat = DOWNLOADED_TEXT + downloadedCounter + DOWNLOADING_TEXT + downloadingCounter;
                       statsText.setText(stat);
                   }else {
                       statsText.setText("");
                   }
               }
           });
        }
    }



    /*
    //TODO context menu is useless do this https://stackoverflow.com/questions/30417223/how-to-add-menu-button-without-action-bar
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.download_path:
                downloadLocationDialog();
                return true;

            case R.id.downloads:
                //TODO show downloaded items
                Log.d("down", "menu");
                return true;

            case R.id.info:
                openAboutPage();
                return true;

            case R.id.exit:
                stopService();
                getActivity().finish();
        }

        return super.onContextItemSelected(item);
    }
    */

    private void showMenu(View v){

        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.setOnMenuItemClickListener(this);
        //MenuInflater menuInflater = popupMenu.getMenuInflater();
        //menuInflater.inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){

            case R.id.download_path:
                downloadLocationDialog();
                return true;

            case R.id.downloads:
                //TODO show downloaded items
                Log.d("down", "menu");
                return true;

            case R.id.info:
                openAboutPage();
                return true;

            case R.id.exit:
                stopService();
        }

        return false;
    }


    private void openAboutPage() {

        Intent aboutIntent = new Intent(getActivity(), aboutActivity.class);
        startActivity(aboutIntent);
    }

    private void downloadLocationDialog() {

        pathDialog pathDialog = new pathDialog();
        pathDialog.setCancelable(false);
        pathDialog.setTargetFragment(infoFragment.this, 1);
        pathDialog.show(getActivity().getSupportFragmentManager().beginTransaction(), "path dialog");
        pathDialog.setDownpath(downloadPath);
    }

    @Override
    public void onStop() {
        super.onStop();
        //saveData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        saveData();
    }

    private void updateIDs(){

        int i = 0;

        for (downloadItem item : downloadItems){
            item.updateID(i);
            i++;
        }
    }

    private void checkPermission(){
        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = getContext().checkCallingOrSelfPermission(perm);

        if (res == PackageManager.PERMISSION_DENIED){

            builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Permission Need");
            builder.setMessage("This app not work without permission");
            builder.setCancelable(false);
            builder.setPositiveButton("Go app settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    //getContext().startActivity(intent);
                    getActivity().startActivity(intent);
                }
            });

            /*
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "This app not work without permissions", Toast.LENGTH_SHORT).show();
                }
            });
            */
            builder.show();
            /*
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            getContext().startActivity(intent);
            */
        }

    }

    private boolean getYoutubeID(String url) {

        if (url != null) {
            if (url.contains(YOUTUBE_HEAD2)) {
                if (url.contains(YOUTUBE_TIME_HEAD)) {
                    youtubeID = url.substring(url.indexOf(YOUTUBE_HEAD2), url.indexOf(YOUTUBE_TIME_HEAD)).replace(YOUTUBE_TIME_HEAD, "").replace(YOUTUBE_HEAD2, "");
                } else {
                    youtubeID = url.substring(url.indexOf(YOUTUBE_HEAD2)).replace(YOUTUBE_HEAD2, "");
                }
            } else if (url.contains(YOUTUBE_HEAD)) {
                if (url.contains(LINK_HEAD) || url.contains(LINK_HEAD2)) {
                    youtubeID = url.substring(url.indexOf(YOUTUBE_HEAD), url.indexOf("&")).replace(YOUTUBE_HEAD, "");
                } else {
                    youtubeID = url.substring(url.indexOf(YOUTUBE_HEAD)).replace(YOUTUBE_HEAD, "");
                }
            }else {
                return false;
            }
            return true;
        }
        return false;
    }


    public void getHtml(String html){

        if (getView() != null){
            webView = getView().findViewById(R.id.webview);
        }

        //webView = new WebView(getContext());
        webView.clearHistory();
        webView.clearCache(true);
        webView.setClickable(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webView.loadUrl(html);

        webView.setWebViewClient(new myWebClient());

        /*
        webView.setWebViewClient(new WebViewClient() {
            //this is working look every load resource
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                view.loadUrl("javascript:window.HTMLOUT.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.HTMLOUT.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

        });
        */
    }

    @Override
    public void downPath(String path) {

        downloadPath = path;
    }



    public class myWebClient extends WebViewClient{

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            view.loadUrl("javascript:window.HTMLOUT.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:window.HTMLOUT.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
        }

    }



    public class MyJavaScriptInterface{

        @JavascriptInterface
        public void showHTML(String html_data) {

            String scriptText= new Script().getScriptText;
            if (html_data.contains(scriptText)){
                html_data = html_data.replace(scriptText, "");
            }

            if (!html_data.equals(old) && html_data.length() > 4500){

                Log.i("showhtml", " ======>  HTML Data OK : " + html_data.length());
                htmlData = html_data;
                dataOK = true;
                old = html_data;

            } else {
                Log.i("showhtml","======> NO HTML Data"+html_data.length());
            }
        }
    }


    public class getLinksFromHtml extends AsyncTask<Void,Integer,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            //30 sec timeout
            for (int i = 1; i <= 30; i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (dataOK){
                    break;
                }else {
                    publishProgress(i);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialogOpen = true;
            //TODO handle rotate while progress dialog open
            openDialog();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dialogOpen = false;

            if (dataOK){
                progressDialog.dismiss();
                webView .stopLoading();
                webView.clearHistory();
                //webView.destroy();    //don t destroy a view
                //webView = null;
                myWebClient = null;
                dataOK = false;

                if(htmlData.contains(NO_STREAM)){
                    Toast.makeText(getContext(), "Invalid Youtube link !", Toast.LENGTH_SHORT).show();
                }else{
                    parseLinks(htmlData);
                }

                htmlData = null;
            }else {
                progressDialog.dismiss();
                //webView = null;
                Toast.makeText(getActivity(), "An error occurred try again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... integers) {
            super.onProgressUpdate(integers);

            if(integers[0] %6 == 0){
                if (!dataOK){
                    getHtml(downloadLink);
                }
            }
        }
    }

    private void openDialog() {

        //TODO Window manager error when view destroyed
        if (dialogOpen) {
            progressDialog = new ProgressDialog(getContext());
            //dialog.setTitle("Getting Links");
            progressDialog.setMessage("Wait !!");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }


    public void parseLinks(String getHtml){

        //true for mp3 false for videos;


        dialogAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);


        String newLinks = getHtml.substring(getHtml.indexOf(BODY_TEXT),getHtml.lastIndexOf(SCRIPT_TEXT));
        String splits[] = newLinks.split("href");


        if (downloadType){      //mp3

            for (int i = 1; i < splits.length ; i++){
                downloadLinks.add(new downloadLinks("mp3"
                        ,HTTPS_LINK + splits[i].substring(splits[i].indexOf(HEAD),splits[i].indexOf(END_TEXT_MP3)).concat(MP3)
                        ,splits[i].substring(splits[i].indexOf(BIT_RATE_TEXT),splits[i].indexOf(BIT_RATE)).replace(BIT_RATE_TEXT,"").concat(KBPS)
                        ,splits[i].substring(splits[i].indexOf(SIZE_TEXT),splits[i].indexOf(SIZE_TEXT_END)).replace(SIZE_TEXT ,"").concat(BYTE)));
            }

        }else {         //videos

            for (int i = 1; i < splits.length ; i++){
                downloadLinks.add(new downloadLinks(splits[i].substring(splits[i].indexOf(FILE_TYPE_HEAD), splits[i].indexOf(FILE_TYPE_HEAD) + 4 + FILE_TYPE_HEAD.length()).replace(FILE_TYPE_HEAD, "").replace("<", "")
                        ,HTTPS_LINK + splits[i].substring(splits[i].indexOf(HEAD),splits[i].indexOf(END_TEXT_VIDEOS))
                        ,splits[i].substring(splits[i].indexOf(FILE_QUALITY_HEAD), splits[i].indexOf(FILE_QUALITY_HEAD) + 4 + FILE_QUALITY_HEAD.length()).replace("<", "").replace(FILE_QUALITY_HEAD, "")
                        ,splits[i].substring(splits[i].indexOf(SIZE_TEXT),splits[i].indexOf(SIZE_TEXT_END)).replace(SIZE_TEXT ,"").concat(BYTE)));
            }

        }

        //download select dialog

        for (com.bekircan.youtubedownloader.downloadLinks links : downloadLinks){

            if (downloadType){
                dialogAdapter.add("As :\t\t" + links.getQuality() + "\t\t" + links.getSize());
            }else {
                dialogAdapter.add("As :\t\t" + links.getType() + "\t\t" + links.getQuality() + "\t\t" + links.getSize());
            }
        }


        listBuilder = new AlertDialog.Builder(getActivity());
        listBuilder.setTitle("Download :  ");
        listBuilder.setCancelable(false);

        listBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                downloadLinks.clear();
            }
        });

        listBuilder.setAdapter(dialogAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                //download item
                downloadItems.add(new downloadItem(indexCounter, -1, downloadLinks.get(which).getUrl(), downloadLinks.get(which).getSize()
                        ,downloadLinks.get(which).getType(), downloadLinks.get(which).getQuality(), downloadPath, false, downloadListener));

                downloadItems.get(indexCounter).downStart();

                //notify
                notifyDownloads.add(new notifyDownload(downloadItems.get(indexCounter), getContext(), true));
                notifyDownloads.get(indexCounter).createNotify();
                //notifyDownloads.get(indexCounter).updateNotify();




                /*
                downloaders.add(new Downloader(downloadItems.get(indexCounter), indexCounter ,downloadListener));
                downloaders.get(indexCounter).start();
                */


                /*
                pureDownloadList.add(new pureDownload(indexCounter, downloadLinks.get(which).getUrl(), downloadLinks.get(which).getType(),
                        downloadLinks.get(which).getSize(), downloadPath, downloadLinks.get(which).getQuality(), downloadListener));

                pureDownloadList.get(indexCounter).start();
                */

                /*
                pureDownloads.add(new pureDownload(indexCounter, downloadLinks.get(which).getUrl(), downloadLinks.get(which).getType(),
                        downloadLinks.get(which).getSize(), downloadPath, downloadLinks.get(which).getQuality(), downloadListener));

                pureDownloads.get(indexCounter).start();
                */

                indexCounter += 1;
                downloadLinks.clear();
                dialog.dismiss();


                downloadingCounter++;
                //updateDownStats();
                barStats();
                linkEditText.setText("");

                if (!isServiceRunning()) {

                    startService();
                }



            }
        });


        listBuilder.show();


        /*
        adapter = new adapter(pureDownloadList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        */


        downloadAdapter = new downloadAdapter(downloadItems, itemDeleted, getView());
        recyclerView.setAdapter(downloadAdapter);
        downloadAdapter.notifyDataSetChanged();

        //downloaders.add(new Downloader(downloadItems,indexCounter, downloadListener));

    }





}