package com.mposyandu.mposyandu.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.Database;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class scanQRPeserta extends AppCompatActivity {
    CodeScannerView scannerView;
    private CodeScanner mCodeScanner;
    String ayah,ibu,ttl,jk,alamat,posyandu,usia;
    TextView tvNama,tvAyah,tvIbu,tvTTL,tvJk,tvAlamat,tvPosyandu,tvUsia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        tvNama=findViewById(R.id.tv_nama_bayi);
        tvAlamat=findViewById(R.id.tv_det_alamat_bayi);
        tvPosyandu=findViewById(R.id.tv_det_posyandu);
        tvUsia=findViewById(R.id.tv_usia_bayi);
        tvAyah=findViewById(R.id.tv_det_ayah_bayi);
        tvIbu=findViewById(R.id.tv_det_mama_bayi);
        tvTTL=findViewById(R.id.tv_det_tanggal_lahir);
        tvJk=findViewById(R.id.tv_det_gender_bayi);
        findViewById(R.id.detailLayout).setVisibility(View.GONE);
        findViewById(R.id.loading_scan).setVisibility(View.GONE);
        findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               scanQRPeserta.super.onBackPressed();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.detailLayout).setVisibility(View.GONE);
            }
        });

//        findViewById(R.id.tesButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                scanData("");
//            }
//        });

        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.startPreview();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                scanQRPeserta.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scanData(result.toString());
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    private void scanData(String result){
        findViewById(R.id.loading_scan).setVisibility(View.VISIBLE);
        AndroidNetworking.post(Database.scanQRBalita)
                .addBodyParameter("id_qr",result)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        findViewById(R.id.loading_scan).setVisibility(View.GONE);
                        try {
                            tvNama.setText( response.getJSONArray("result").getJSONObject(0).getString("nama"));
                            tvUsia.setText( response.getJSONArray("result").getJSONObject(0).getString("usia")+" Bulan");
                            tvTTL.setText( response.getJSONArray("result").getJSONObject(0).getString("tanggal_lahir"));
                            tvIbu.setText( response.getJSONArray("result").getJSONObject(0).getString("ibu"));
                            tvAyah.setText( response.getJSONArray("result").getJSONObject(0).getString("ayah"));
                            tvAlamat.setText( response.getJSONArray("result").getJSONObject(0).getString("alamat"));
                            tvPosyandu.setText( response.getJSONArray("result").getJSONObject(0).getString("posyandu"));
                            tvJk.setText( response.getJSONArray("result").getJSONObject(0).getString("gender"));
                            Toast.makeText(scanQRPeserta.this, "Berhasil Menampilkan Data", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.detailLayout).setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            Toast.makeText(scanQRPeserta.this, "Gagal Menampilkan Data", Toast.LENGTH_SHORT).show();
                            Toast.makeText(scanQRPeserta.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ExceptionTag", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        findViewById(R.id.loading_scan).setVisibility(View.GONE);
                        Toast.makeText(scanQRPeserta.this, "Terjadi Kesalahan, Periksa Koneksi Internet Anda dan Coba Kembali ", Toast.LENGTH_LONG).show();
                        Toast.makeText(scanQRPeserta.this, anError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}