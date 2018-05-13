package com.bekircan.youtubedownloader;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class diifCallBack extends DiffUtil.Callback {

    private List<pureDownload> oldDownList;
    private List<pureDownload> newDownList;

    public diifCallBack(List<pureDownload> oldDownList, List<pureDownload> newDownList) {
        this.oldDownList = oldDownList;
        this.newDownList = newDownList;
    }

    @Override
    public int getOldListSize() {
        return oldDownList.size();
    }

    @Override
    public int getNewListSize() {
        return newDownList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldDownList.get(oldItemPosition).getId() == newDownList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        pureDownload oldItem = oldDownList.get(oldItemPosition);
        pureDownload newItem = newDownList.get(newItemPosition);

        return oldItem.getName().equals(newItem.getName());
    }


}
