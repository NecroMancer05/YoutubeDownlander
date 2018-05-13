package com.bekircan.youtubedownloader;

public interface downloadListener {

    void updateProgress(int id, long percent);

    void isFinish(int id);

}
