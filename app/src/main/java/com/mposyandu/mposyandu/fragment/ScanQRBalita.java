package com.mposyandu.mposyandu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.activity.scanQRPeserta;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScanQRBalita extends Fragment {
    View isi;
    CodeScannerView scannerView;
    CodeScanner mCodeScanner;
    UserModel user;



    public ScanQRBalita() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        isi=inflater.inflate(R.layout.fragment_scan_qr_balita, container, false);
        isi.findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        scannerView = isi.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        mCodeScanner.startPreview();

        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                getActivity().runOnUiThread(new Runnable() {
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
        return isi;
    }

    private void scanData(String result){
        isi.findViewById(R.id.loading_scan).setVisibility(View.VISIBLE);
        String Url = Database.getUrl() + "/balita/qr/"+result;
        Log.d("SCAN_QR_REQUEST", "request: "+Url);
        JsonObjectRequest RequestBalita = new JsonObjectRequest(Request.Method.POST, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0 :
                            JSONObject obj = response.getJSONObject("data");
                            BalitaModel balitaList = new BalitaModel();
                            balitaList.setId(obj.getInt("id"));
                            balitaList.setNama(obj.getString("nama"));
                            balitaList.setGender(obj.getString("gender"));
                            balitaList.setTanggal_lahir(obj.getString("tanggal_lahir"));
                            balitaList.setPosyandu(obj.getString("posyandu"));
                            balitaList.setPosyandu_id(obj.getInt("posyandu_id"));
                            balitaList.setAyah(obj.getString("ayah"));
                            balitaList.setPhoto(obj.getString("photo"));
                            balitaList.setIbu(obj.getString("ibu"));
                            balitaList.setIbu_id(obj.getInt("ibu_id"));
                            balitaList.setActive(obj.getInt("active"));
                            balitaList.setPetugas(obj.getString("petugas"));
                            balitaList.setCreator_id(obj.getInt("creator_id"));
                            balitaList.setAlamat(obj.getString("alamat"));
                            balitaList.setEmail(obj.getString("email"));
                            balitaList.setId_qrcode(obj.getString("id_qrcode"));
                            isi.findViewById(R.id.loading_scan).setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("balita", balitaList);
                            bundle.putParcelable("user", user);
                            Fragment fragment = new DetailBalita();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragment.setArguments(bundle);
                            fragmentManager.beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .replace(R.id.content_ketua, fragment)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case 1 :
                            isi.findViewById(R.id.loading_scan).setVisibility(View.GONE);
                            AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Isi Data",
                                    "Error", getActivity());
                            builder.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    isi.findViewById(R.id.loading_scan).setVisibility(View.GONE);
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing Data",
                            "Error", getActivity());
                    builder.show();
                    Log.e("SCAN_QR_REQUEST", "JSONException: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isi.findViewById(R.id.loading_scan).setVisibility(View.GONE);
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
                Log.e("SCAN_QR_REQUEST", "onResponse: error", error);
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestBalita);
    }
}