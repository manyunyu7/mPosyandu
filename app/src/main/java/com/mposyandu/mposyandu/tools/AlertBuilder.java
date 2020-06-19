package com.mposyandu.mposyandu.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertBuilder{
    private String Message;
    private String Title;
    private android.content.Context Context;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    public AlertBuilder(String Message, String Title, Context Context) {
        this.Message = Message;
        this.Title = Title;
        this.Context = Context;
    }

    public void show() {
        builder = new AlertDialog.Builder(Context);
        builder.setMessage(Message);
        builder.setTitle(Title);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
