package com.bekircan.youtubedownloader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class pathDialog extends AppCompatDialogFragment {

    private EditText editText;
    private getPathListener getPathListener;
    private static String downPath;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.path_dialog, null);

        builder.setView(view)
                .setTitle("Download Path")
                .setMessage("Enter a folder name without '/' for save into it .")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        downPath = editText.getText().toString();
                        getPathListener.downPath(downPath);
                    }
                });

        editText = view.findViewById(R.id.path_dialog);

        if (!downPath.equals("")){
            editText.setText(downPath);
        }

       return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            getPathListener = (pathDialog.getPathListener) getTargetFragment();
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + "must implement Listener ");
        }
    }

    public  void setDownpath(String path){

        downPath = path;
    }


    public interface getPathListener{
        void downPath(String path);
    }

}
