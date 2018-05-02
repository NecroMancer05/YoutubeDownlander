package com.bekircan.youtubedownlander;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class downloadAdapter extends RecyclerView.Adapter<downloadAdapter.viewHolder> {

    private ArrayList<downloadItem> downloadItems;
    private boolean paused = false;
    private itemDeleted itemDeleted;

    public downloadAdapter(ArrayList<downloadItem> downloadItems, itemDeleted itemDeleted) {
        this.downloadItems = downloadItems;
        this.itemDeleted = itemDeleted;
    }

    public void updateDelteListener(itemDeleted itemDeleted){
        this.itemDeleted = itemDeleted;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_row, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {

        holder.fileMame.setText(downloadItems.get(position).getFileName());
        holder.fileSize.setText(downloadItems.get(position).getFileSize());
        holder.fileQuality.setText(downloadItems.get(position).getFileQuality());
        holder.fileType.setText(downloadItems.get(position).getFileType());
        holder.fileStatus.setText(String.valueOf(downloadItems.get(position).getDownStatus()));
        holder.downProgress.setProgress((int) downloadItems.get(position).getDownStatus());

        holder.setItemClickListener(new onItemClickListener() {
            @Override
            public void onDelete(int position) {

                if (downloadItems.get(position).deleteFile()){
                    itemDeleted.deleted(position,(int) downloadItems.get(position).getDownStatus());
                    downloadItems.get(position).stop();
                    onRemove(position);
                }
            }

            @Override
            public void onPause(int position) {

                if (paused){
                    paused = false;
                    //notifyDataSetChanged();

                }else {
                    paused = true;
                    notifyDataSetChanged();
                }
                downloadItems.get(position).pause();
            }

            @Override
            public void onRemove(int position) {
                itemDeleted.deleted(position,(int) downloadItems.get(position).getDownStatus());
                if (downloadItems.get(position).getDownStatus() != 100){
                    downloadItems.get(position).stop();
                }
                downloadItems.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onStop(int position) {

                downloadItems.get(position).stop();
                onDelete(position);
            }

        });

        //TODO https://stackoverflow.com/questions/8049620/how-to-set-layout-gravity-programmatically
        if (downloadItems.get(position).getDownStatus() >= 100){
            holder.downProgress.setVisibility(View.GONE);
            holder.fileStatus.setText("Donwload Finished");
        }else{
            holder.downProgress.setVisibility(View.VISIBLE);
        }

        if(downloadItems.get(position).getDownStatus() == -1){
            holder.fileStatus.setText("Pending...");
        }

        if (paused){
            holder.fileStatus.setText("Paused");
        }

    }

    @Override
    public int getItemCount() {
        return downloadItems.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder implements MenuItem.OnMenuItemClickListener, View.OnCreateContextMenuListener{


        private onItemClickListener mListener;

        private TextView fileMame;
        private TextView fileSize;
        private TextView fileQuality;
        private TextView fileType;
        private TextView fileStatus;
        private ProgressBar downProgress;
        //TODO put img view
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


            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener !=null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            mListener.onPause(position);
                            return true;

                        case 2:
                            mListener.onStop(position);
                            return true;

                        case 3:
                            mListener.onRemove(position);
                            return  true;

                        case 4:
                            mListener.onDelete(position);
                            return true;
                    }
                }
            }
            return false;
        }

        /*
        @Override
        public void onClick(View v) {

            if(mListener !=null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onDelete(position);
                }
            }
        }
        */

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select Action");

            MenuItem pause = menu.add(Menu.NONE, 1, 1, "Pause&Resume");
            MenuItem stop = menu.add(Menu.NONE, 2, 2, "Stop");
            MenuItem remove = menu.add(Menu.NONE, 3 , 3, "Remove from list");
            MenuItem delete = menu.add(Menu.NONE, 4, 4, "Delete");

            pause.setOnMenuItemClickListener(this);
            stop.setOnMenuItemClickListener(this);
            remove.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }


        private void setItemClickListener(onItemClickListener itemClickListener){

            mListener = itemClickListener;
        }

    }

    private interface onItemClickListener {

        void onDelete(int position);

        void onPause(int position);

        void onRemove(int position);

        void onStop(int position);

    }
}
