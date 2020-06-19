package com.mposyandu.mposyandu.tools;

import android.app.ProgressDialog;
import android.content.Context;


public class DialogBuilder {
    private String Message;
    private String Title;
    private Context Context;
    private ProgressDialog progressDialog;

    public DialogBuilder (String Message, String Title, Context Context) {
        this.Message = Message;
        this.Title = Title;
        this.Context = Context;
    }

    public void show() {
        progressDialog = new ProgressDialog(Context);
        progressDialog.setTitle(Title);
        progressDialog.setMessage(Message);
        progressDialog.setCancelable(true);
        progressDialog.show();

    }

    public void setMessage(final String Message) {

        progressDialog.setMessage(Message);
    }

    public void dismis() {
        progressDialog.dismiss();

    }
}
