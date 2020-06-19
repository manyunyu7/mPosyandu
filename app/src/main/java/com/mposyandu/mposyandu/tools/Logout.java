package com.mposyandu.mposyandu.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mposyandu.mposyandu.activity.LoginActivity;

public class Logout {
    private Context context;
    private Intent intent;
    public Logout(Context context) {
        this.context = context;
    }
    public Intent out() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        if(editor.commit()) {
            intent = new Intent(context, LoginActivity.class);
        }
        return intent;
    }
}
