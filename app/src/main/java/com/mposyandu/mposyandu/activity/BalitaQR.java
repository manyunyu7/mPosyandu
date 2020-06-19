package com.mposyandu.mposyandu.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.Database;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class BalitaQR extends AppCompatActivity {

    String nama,posyandu,id;
    String idQR;
    String setterQR="";

    ImageView showQR;
    TextView replacement,idMentee,idMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balita_q_r);
        nama=getIntent().getStringExtra("nama");
        posyandu=getIntent().getStringExtra("posyandu");
        id=getIntent().getStringExtra("id");
        showQR=findViewById(R.id.show_qr);
        replacement=findViewById(R.id.reptext);
        idMentee=findViewById(R.id.id_mentee);
        idMain=findViewById(R.id.id_mentee_main);

        updateData();



        idMain.setText(nama);
        idMentee.setText("Posyandu :\n"+posyandu);


    }
    private void updateData(){
        AndroidNetworking.post(Database.getQRBalita)
                .addBodyParameter("id",id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        findViewById(R.id.loading_scan).setVisibility(View.GONE);
                        try {
                            setterQR=response.getJSONArray("data").getJSONObject(0).getString("qr_code");
                            QRGEncoder qre = new QRGEncoder(setterQR, null, QRGContents.Type.TEXT, 500);
                            Bitmap qrbm = qre.getBitmap();
                            replacement.setVisibility(View.INVISIBLE);
                            showQR.setImageBitmap(qrbm);
                        } catch (JSONException e) {
                            Toast.makeText(BalitaQR.this, "Terjadi Kesalahan, Periksa Koneksi Internet Anda dan Ketuk Tombol Refresh ", Toast.LENGTH_LONG).show();
                            Toast.makeText(BalitaQR.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ExceptionTag", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        findViewById(R.id.loading_scan).setVisibility(View.GONE);
                        Toast.makeText(BalitaQR.this, anError.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(BalitaQR.this, "Terjadi Kesalahan, Periksa Koneksi Internet Anda dan Ketuk Tombol Refresh ", Toast.LENGTH_LONG).show();
                    }
                });
    }

}