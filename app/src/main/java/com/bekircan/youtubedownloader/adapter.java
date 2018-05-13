package com.bekircan.youtubedownloader;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class adapter extends RecyclerView.Adapter<adapter.viewHolder>{

    private List<pureDownload> downList = new ArrayList<>();

    public adapter(List<pureDownload> list) {
        this.downList.addAll(list);
    }

    @NonNull
    @Override
    public adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_row, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter.viewHolder holder, int position) {

        pureDownload download = downList.get(position);

        holder.fileMame.setText(download.getFileName());
        holder.fileSize.setText(download.getFileSize());
        //holder.fileQuality.setText(download.get);
        holder.downProgress.setProgress(download.getStatus());

        /*
        holder.fileMame.setText(downloadItems.get(position).getFileName());
        holder.fileSize.setText(downloadItems.get(position).getFileSize());
        holder.fileQuality.setText(downloadItems.get(position).getFileQuality());
        holder.fileType.setText(downloadItems.get(position).getFileType());
        holder.fileStatus.setText(String.valueOf(downloadItems.get(position).getDownStatus()));
        holder.downProgress.setProgress((int) downloadItems.get(position).getDownStatus());
        */
    }

    public void updateList(List<pureDownload> pureDownloads){

        diifCallBack diifCallBack = new diifCallBack(this.downList, pureDownloads);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diifCallBack);

        this.downList.clear();
        this.downList.addAll(pureDownloads);

        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return downList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private TextView fileMame;
        private TextView fileSize;
        private TextView fileQuality;
        private TextView fileType;
        private TextView fileStatus;
        private ProgressBar downProgress;
        private ImageView fileImg;

        public viewHolder(View itemView) {
            super(itemView);

            fileMame = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            fileQuality = itemView.findViewById(R.id.fileQuality);
            fileType = itemView.findViewById(R.id.fileType);
            fileStatus = itemView.findViewById(R.id.downloadProgress);
            downProgress = itemView.findViewById(R.id.progressBar);
            fileImg = itemView.findViewById(R.id.imageView);

        }
    }
}
