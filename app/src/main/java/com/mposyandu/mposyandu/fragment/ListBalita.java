package com.mposyandu.mposyandu.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.adapter.ListBalitaAdapter;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.data.MonthModel;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.data.UserModel;
import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>

 */
public class ListBalita extends Fragment {
    private List<BalitaModel> balita = new ArrayList<>();
    private RecyclerView rv;
    private UserModel user;
    private ListBalitaAdapter adapter;
    private FloatingActionButton fab;
    private SwipeRefreshLayout sw;
    private LinearLayout empty;
    private LinearLayout load;
    private ProgressDialog progressDialog;
    private List<MonthModel> dms = new ArrayList<>();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_export) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPermission();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Export Berdasarkan");
            String[] choose =  {"SIP"};
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Menyiapkan Data...");
            builder.setSingleChoiceItems(choose, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            dialog.dismiss();
                            semuabulan();
                            break;
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void semuabulan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pencarian");
        final LayoutInflater inflate = getLayoutInflater();
        final View alertView = inflate.inflate(R.layout.export_bulan, null);
        builder.setView(alertView);
        final TextInputLayout bulan = alertView.findViewById(R.id.export_bulan);
        bulan.setVisibility(View.GONE);
        final TextInputLayout tahun = alertView.findViewById(R.id.export_tahun);

        builder.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final String tahun_val = tahun.getEditText().getText().toString();
                boolean flags = false;
                if(tahun_val.length() > 4)
                    flags = true;
                tahun.setError("Invalid Tahun");
                progressDialog.dismiss();
                if(!flags) {
                    dialog.dismiss();
                    tahun.setErrorEnabled(false);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("tahun", tahun_val);
                    params.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
                    progressDialog.show();
                    String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/0"
                            +"/condition/getAllMonth";
                    JsonObjectRequest exportall = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Integer error = response.getInt("msg");
                                switch (error) {
                                    case 0 :
                                        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Posyandu "+user.getPosyandu()+"_"+tahun_val+".csv";
                                        try {
                                            CSVWriter writer = new CSVWriter(new FileWriter(path));
                                            List<String[]> data = new ArrayList<>();
                                            data.add(new String[] {"No", "Nama", "Tanggal Lahir", "Ayah", "Ibu",
                                                    "", "", "",
                                                    "", "", "",
                                                    "", "", "",
                                                    "", "", "",
                                                    "", "", "",
                                                    "", "Bulan", "",
                                                    "", "", "",
                                                    "", "", "",
                                                    "", "", "",
                                                    "", "", "",
                                                    "", "", "",
                                                    "", "", "",});
                                            data.add(new String[]{ "", "", "", "", "",
                                                    "", "Januari","",
                                                    "", "Februari","",
                                                    "", "Maret","",
                                                    "", "April","",
                                                    "", "Mei","",
                                                    "", "Juni","",
                                                    "", "Juli","",
                                                    "", "Agustus","",
                                                    "", "September","",
                                                    "", "Oktober","",
                                                    "", "November","",
                                                    "", "Desember",""});
                                            data.add(new String[] {"", "", "", "", "",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala",
                                                    "Berat", "Tinggi","Lingkar Kepala"
                                            });

                                            JSONObject json = response.getJSONObject("data");
                                            Iterator<?> keys = json.keys();
                                            int j = 0;
                                            while (keys.hasNext()) {
                                                String key = (String)keys.next();
                                                JSONArray arrr = json.getJSONArray(key);
                                                JSONObject element;
                                                MonthModel m = new MonthModel();
                                                for(int i = 0; i < arrr.length(); i++){
                                                    element = arrr.getJSONObject(i);
                                                    String date = element.getString("tanggal_input");
                                                    String item[] = date.split("-");


                                                    if (item[1].contains("01")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_januari(element.getString("berat"));
                                                        m.setT_januari(element.getString("tinggi"));
                                                        m.setL_januari(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("02")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_februari(element.getString("berat"));
                                                        m.setT_februari(element.getString("tinggi"));
                                                        m.setL_februari(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("03")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_maret(element.getString("berat"));
                                                        m.setT_maret(element.getString("tinggi"));
                                                        m.setL_maret(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("04")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_april(element.getString("berat"));
                                                        m.setT_april(element.getString("tinggi"));
                                                        m.setL_april(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("05")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_mei(element.getString("berat"));
                                                        m.setT_mei(element.getString("tinggi"));
                                                        m.setL_mei(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("06")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_juni(element.getString("berat"));
                                                        m.setT_juni(element.getString("tinggi"));
                                                        m.setL_juni(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("07")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_juli(element.getString("berat"));
                                                        m.setT_juli(element.getString("tinggi"));
                                                        m.setL_juli(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("08")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_agustus(element.getString("berat"));
                                                        m.setT_agustus(element.getString("tinggi"));
                                                        m.setL_agustus(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("09")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_september(element.getString("berat"));
                                                        m.setT_september(element.getString("tinggi"));
                                                        m.setL_september(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("10")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_oktober(element.getString("berat"));
                                                        m.setT_oktober(element.getString("tinggi"));
                                                        m.setL_oktober(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("11")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_november(element.getString("berat"));
                                                        m.setT_november(element.getString("tinggi"));
                                                        m.setL_november(element.getString("lingkar_kepala"));
                                                    }
                                                    if (item[1].contains("12")) {
                                                        m.setLahir(item[2]+"-"+item[1]+"-"+item[0]);
                                                        m.setAyah(element.getString("ayah"));
                                                        m.setIbu(element.getString("ibu"));
                                                        m.setId(element.getString("nama"));
                                                        m.setB_desember(element.getString("berat"));
                                                        m.setT_desember(element.getString("tinggi"));
                                                        m.setL_desember(element.getString("lingkar_kepala"));
                                                    }

                                                }
                                                dms.add(m);
                                                data.add(new String[]{
                                                        String .valueOf(j+1),dms.get(j).getId(),dms.get(j).getLahir(), dms.get(j).getAyah(), dms.get(j).getIbu(),
                                                        dms.get(j).getB_januari(), dms.get(j).getT_januari(), dms.get(j).getL_januari(),
                                                        dms.get(j).getB_februari(), dms.get(j).getT_februari(), dms.get(j).getL_februari(),
                                                        dms.get(j).getB_maret(), dms.get(j).getT_maret(), dms.get(j).getL_maret(),
                                                        dms.get(j).getB_april(), dms.get(j).getT_april(), dms.get(j).getL_april(),
                                                        dms.get(j).getB_mei(), dms.get(j).getT_mei(), dms.get(j).getL_mei(),
                                                        dms.get(j).getB_juni(), dms.get(j).getT_juni(), dms.get(j).getL_juni(),
                                                        dms.get(j).getB_juli(), dms.get(j).getT_juli(), dms.get(j).getL_juli(),
                                                        dms.get(j).getB_agustus(), dms.get(j).getT_agustus(), dms.get(j).getL_agustus(),
                                                        dms.get(j).getB_september(), dms.get(j).getT_september(), dms.get(j).getL_september(),
                                                        dms.get(j).getB_oktober(), dms.get(j).getT_oktober(), dms.get(j).getL_oktober(),
                                                        dms.get(j).getB_november(), dms.get(j).getT_november(), dms.get(j).getL_november(),
                                                        dms.get(j).getB_desember(), dms.get(j).getT_desember(), dms.get(j).getL_desember(),
                                                });
                                                j++;
                                            }

                                            writer.writeAll(data);
                                            writer.close();
                                            progressDialog.dismiss();

                                        }
                                        catch (IOException e) {

                                            e.getStackTrace();
                                            AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan Saat Merubah Data", "Error", getActivity());
                                            builder.show();
                                        }

                                        break;
                                    case 1 :
                                        progressDialog.dismiss();
                                        AlertBuilder builder = new AlertBuilder("Data Tidak Tersedia", "Error", getActivity());
                                        builder.show();
                                        break;
                                }
                            }
                            catch (JSONException e) {
                                e.getStackTrace();
                                progressDialog.dismiss();
                                AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan Saat Parsing Data, "+e.getMessage(), "Error", getActivity());
                                builder.show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan Pada Koneksi", "Error", getActivity());
                            builder.show();
                        }
                    });
                    Controller.getmInstance().addToRequestQueue(exportall);
                }
            }
        });

    }

    public void perBulan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pencarian");
        LayoutInflater inflate = getLayoutInflater();
        final View alertView = inflate.inflate(R.layout.export_bulan, null);
        builder.setView(alertView);
        final TextInputLayout bulan = alertView.findViewById(R.id.export_bulan);
        final TextInputLayout tahun = alertView.findViewById(R.id.export_tahun);

        builder.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String bulan_val  = bulan.getEditText().getText().toString();
                String tahun_val = tahun.getEditText().getText().toString();
                boolean flags = false;
                if (bulan_val.length() > 2)
                    flags = true;
                bulan.setError("Invalid Bulan");
                progressDialog.dismiss();
                if(tahun_val.length() > 4)
                    flags = true;
                tahun.setError("Invalid Tahun");
                progressDialog.dismiss();
                if(!flags) {
                    dialog.dismiss();
                    bulan.setErrorEnabled(false);
                    tahun.setErrorEnabled(false);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("bulan", bulan_val);
                    params.put("tahun", tahun_val);
                    params.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
                    req(params);
                }
            }
        });

    }

    public void req(final Map params) {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/0"
                +"/condition/getMonth";
        JsonObjectRequest export = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0 :
                            JSONArray obj_arr = response.getJSONArray("data");
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Posyandu "+user.getPosyandu()+" "+params.get("bulan")+"_"+params.get("tahun")+".csv";
                            try {
                                CSVWriter writer = new CSVWriter(new FileWriter(path));
                                List<String[]> data = new ArrayList<>();
                                data.add(new String[]{"No", "Nama", "Jenis Kelamin", "Alamat", "Tanggal Input", "Berat", "Tinggi", "Lingkar Kepala", "Petugas"});
                                for (Integer i = 0; i< obj_arr.length(); i++) {
                                    JSONObject obj = obj_arr.getJSONObject(i);
                                    data.add(new String[]{
                                            String.valueOf(i+1),
                                            obj.getString("nama"),
                                            obj.getString("gender"),
                                            obj.getString("alamat"),
                                            obj.getString("tanggal_input"),
                                            obj.getString("berat")+" Kg",
                                            obj.getString("tinggi")+" Cm",
                                            obj.getString("lingkar_kepala")+" Cm",
                                            obj.getString("petugas")});
                                }
                                writer.writeAll(data);
                                writer.close();
                                progressDialog.dismiss();

                            }
                            catch (IOException e) {
                                e.getStackTrace();
                                AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan Saat Menyimpan Data", "Error", getActivity());
                                builder.show();
                            }

                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Data Tidak Tersedia", "Error", getActivity());
                            builder.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan Saat Parsing Data", "Error", getActivity());
                    builder.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan Pada Koneksi", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(export);
    }

    public void semua() {
        progressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/0"
                +"/condition/all";
        JsonObjectRequest exportall = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0 :
                            JSONArray obj_arr = response.getJSONArray("data");
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Posyandu "+user.getPosyandu()+"_FULL.csv";
                            try {
                                CSVWriter writer = new CSVWriter(new FileWriter(path));
                                List<String[]> data = new ArrayList<>();
                                data.add(new String[] {"No", "Nama", "Jenis Kelamin", "Alamat", "Tanggal Input", "Berat", "Tinggi", "Lingkar Kepala", "Petugas"});
                                for (Integer i = 0; i< obj_arr.length(); i++) {
                                    JSONObject obj = obj_arr.getJSONObject(i);
                                    data.add(new String[] {
                                            String.valueOf(i+1),
                                            obj.getString("nama"),
                                            obj.getString("gender"),
                                            obj.getString("alamat"),
                                            obj.getString("tanggal_input"),
                                            obj.getString("berat")+" Kg",
                                            obj.getString("tinggi")+" Cm",
                                            obj.getString("lingkar_kepala")+" Cm",
                                            obj.getString("petugas")});
                                }
                                writer.writeAll(data);
                                writer.close();
                                progressDialog.dismiss();

                            }
                            catch (IOException e) {
                                e.getStackTrace();
                                AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan saat Menyimpan Data", "Error", getActivity());
                                builder.show();
                            }

                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Data tidak Tersedia", "Error", getActivity());
                            builder.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan saat Memparsing Data",
                            "Error", getActivity());
                    builder.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(exportall);
    }

    public void getPermission() {
        // 1) Pastikan ContextCompat.checkSelfPermission(...) menggunakan versi support ]
        // library karena Context.checkSelfPermission(...) hanya tersedia di Marshmallow.
        // 2) Selalu lakukan pemeriksaan meskipun permission sudah pernah diberikan
        // karena pengguna dapat mencabut permission kapan saja lewat Settings
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Bila masuk ke blok ini artinya permission belum diberikan oleh user.
            // Periksa apakah pengguna pernah diminta permission ini dan menolaknya.
            // Jika pernah, kita akan memberikan penjelasan mengapa permission ini dibutuhkan.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Tampilkan penjelasan mengapa aplikasi ini perlu membaca kontak disini
                // sebelum akhirnya me-request permission dan menampilkan hasilnya
            }

            // Lakukan request untuk meminta permission (menampilkan jendelanya)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Pengecekan ini akan memastikan bahwa hasil yang diberikan berasal dari request
        // yang kita lakukan berdasarkan kode yang ditulis di atas
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false jika pengguna memilih Never Ask Again, jika tidak bernilai true
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (!showRationale) {
                    Toast.makeText(getActivity(), "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.ketua, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Cari....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_baby_add);
        final String[] pilih =  {"Ibu balita sudah terdaftar", "Ibu balita belum terdaftar"};
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pilih");
                builder.setSingleChoiceItems(pilih, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment = null;
                        Bundle bundle = new Bundle();
                        FragmentManager fragmentManager = getFragmentManager();
                        switch (which) {
                            case 0:
                                dialog.dismiss();
                                bundle.putParcelable("user", user);
                                fragment = new ListChooseIbu();
                                fragment.setArguments(bundle);

                                break;
                            case 1:
                                dialog.dismiss();
                                bundle.putString("role", "4");
                                bundle.putParcelable("user", user);
                                fragment = new TambahUser();
                                fragment.setArguments(bundle);
                                break;
                        }
                        fragmentManager.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                                .replace(R.id.content_ketua, fragment)
                                .addToBackStack(null)
                                .commit();

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        View view = inflater.inflate(R.layout.list_balita, container, false);
        findViewById(view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        rv.setLayoutManager(layoutManager);
        request();
        adapter = new ListBalitaAdapter(balita);
        adapter.setOnItemClickListener(new ListBalitaAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("balita", adapter.getItem(position));
                bundle.putParcelable("user", user);
                Fragment fragment = new DetailBalita();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.content_ketua, fragment)
                        .addToBackStack(null)
                        .commit();

            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
        rv.setAdapter(adapter);

        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
            }
        });

        if(balita.isEmpty())
        {
            rv.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
            load.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void findViewById(View view) {
        sw = view.findViewById(R.id.swipe_list_balita);
        rv = view.findViewById(R.id.list_balita);
        empty = view.findViewById(R.id.list_balita_empty);
        load = view.findViewById(R.id.list_balita_loading);
    }

    public void request() {
        Map<String, Integer> params = new HashMap<>();
        params.put("posyandu_id", user.getPosyandu_id());
        params.put("active", 1);
        String Url = Database.getUrl() + "/user/" + user.getId() + "/posyandu/"+user.getPosyandu_id()+"/baby/fetch";
        Log.d("BALITA_REQUEST", "request: "+Url+" with params "+ new JSONObject(params));
        JsonObjectRequest RequestBalita = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    balita.clear();
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0 :
                            JSONArray obj_arr = response.getJSONArray("data");
                            for (int i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
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
                                balita.add(balitaList);
                            }
                            rv.setVisibility(View.VISIBLE);
                            empty.setVisibility(View.GONE);
                            load.setVisibility(View.GONE);
                            break;
                        case 1 :
                            rv.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                            load.setVisibility(View.GONE);
                            break;
                    }
                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing Data",
                            "Error", getActivity());
                    builder.show();
                    Log.e("REQUEST_BALITA", "onResponse: error", e);
                }
                sw.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                rv.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                load.setVisibility(View.GONE);
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestBalita);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fab.hide();
        fab.setImageResource(R.drawable.ic_group_add_white_24dp);
    }

    @Override
    public void onPause() {
        super.onPause();
        fab.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.show();
    }
}

