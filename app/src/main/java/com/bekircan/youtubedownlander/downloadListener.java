package com.bekircan.youtubedownlander;

public interface downloadListener {
    void updateProgress(int id, long percent);

    void isFinish(int id);

}
