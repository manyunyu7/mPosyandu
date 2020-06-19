package com.mposyandu.mposyandu.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.ImunisasiModel;
import com.mposyandu.mposyandu.data.ImunisasiOpsiModel;
import com.mposyandu.mposyandu.data.KondisiModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.data.VitaminModel;
import com.mposyandu.mposyandu.data.VitaminOpsiModel;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.MonthConverter;
import com.opencsv.CSVWriter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mposyandu.mposyandu.Controller.TAG;

public class ProfileBalita extends Fragment {
    private TextView Nama, Ayah, Ibu, Usia, Lahir, Gender, Alamat, Posyandu;
    private Button Edit;
    private UserModel user;
    private BalitaModel balita;
    private ProgressDialog progressDialog;
    private List<KondisiModel> kondisi = new ArrayList<>();
    private List<VitaminModel> vitamin = new ArrayList<>();
    private List<ImunisasiModel> imunisasi = new ArrayList<>();
    private List<VitaminOpsiModel> vitaminOpsi = new ArrayList<>();
    private List<ImunisasiOpsiModel> imunisasiOpsi = new ArrayList<>();
    private Integer usia = null;
    private LinearLayout toolbar;
    private ImageView Thumb;
    private FloatingActionButton fab;
    private static ProfileBalita instance = null;

    public static ProfileBalita getInstance() {
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        instance = this;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        menu.clear();
        inflater.inflate(R.menu.ketua, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.action_export) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPermission();
            }
            request2();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_balita, container, false);
        Bundle bundle =  getArguments();
        user = bundle.getParcelable("user");
        balita = bundle.getParcelable("balita");
        Thumb = view.findViewById(R.id.profile_balita_thumb);
        fab = getActivity().findViewById(R.id.fab);
        findViewById(view);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");

        request();
        if(balita.getPhoto().equals("0")) {
            if (balita.getGender().equals("L")) {
                Picasso.get().load(R.drawable.boy)
                        .into(Thumb);
            }
            else {
                Picasso.get().load(R.drawable.girl)
                        .into(Thumb);
            }
        }
        else {
            Picasso.get().load(Database.getUrl()+"/"+balita.getPhoto())
                    .transform(new CircleTransform())
                    .into(Thumb);
        }




        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                bundle.putParcelable("balita", balita);
                Fragment fragment = new EditBalita();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_left, R.anim.exit_right, R.anim.enter_right, R.anim.ext_left)
                        .replace(R.id.content_ketua, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    public void findViewById(View view) {
        Nama = view.findViewById(R.id.profile_balita_nama);
        Ayah = view.findViewById(R.id.profile_balita_ayah);
        Ibu = view.findViewById(R.id.profile_balita_ibu);
        Usia = view.findViewById(R.id.profile_balita_usia);
        Lahir = view.findViewById(R.id.profile_balita_lahir);
        Gender = view.findViewById(R.id.profile_balita_gender);
        Alamat = view.findViewById(R.id.profile_balita_alamat);
        Posyandu = view.findViewById(R.id.profile_balita_posyandu);
        Edit = view.findViewById(R.id.profile_balita_edit);
    }

    public void request() {
        progressDialog.show();
        Map<String, Integer> params = new HashMap<>();
        params.put("balita_id", balita.getId());
        params.put("empty", 0);
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+
                balita.getId()+"/condition/fetch";
        Log.d("LOGS PROFILE_BALITA", "request: "+url+" with params "+new JSONObject(params));
        JsonObjectRequest RequestKondisi1 = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            usia = obj_arr.length() - 1;
                            int i = 0;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                KondisiModel kondisiList = new KondisiModel();
//                                kondisiList.setUsia(obj.getString("usia"));
                                kondisiList.setBalita_id(obj.getInt("balita_id"));
                                kondisiList.setId(obj.getInt("id"));
                                kondisiList.setTanggal_input(obj.getString("tanggal_input"));
                                kondisiList.setPetugas_id(obj.getInt("petugas_id"));
                                kondisiList.setPetugas(obj.getString("petugas"));
                                if(obj.isNull("lingkar_kepala") || obj.getDouble("lingkar_kepala") == 0.0) {
                                    kondisiList.setLingkar_kepala(null);
                                }
                                else {
                                    kondisiList.setLingkar_kepala(obj.getDouble("lingkar_kepala"));
                                }
                                if(obj.getDouble("tinggi") == 0.0 || obj.isNull("tinggi")) {
                                    kondisiList.setTinggi(null);
                                }
                                else {
                                    kondisiList.setTinggi(obj.getDouble("tinggi"));
                                }
                                if(obj.getDouble("berat") == 0.0 || obj.getDouble("berat") == 0) {
                                    kondisiList.setBerat(null);
                                }
                                else {
                                    kondisiList.setBerat(obj.getDouble("berat"));
                                }
                                kondisi.add(kondisiList);
                            }
                            progressDialog.dismiss();
                            setText();
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Terdapat Error Saat Mengambil Data", "Error", getActivity());
                            builder.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder(e.getMessage(), "Error", getActivity());
                    builder.show();
                    Log.e("LOGS PROFILE_BALITA", "onResponse: error",e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Error Pada Koneksi", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestKondisi1, "KONDISI");
    }

    public void request2() {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/configure/vitamin/fetch";
        Log.d(TAG, "request2: "+url);
        JsonObjectRequest RequestOpsiVitamin = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            int i;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                VitaminOpsiModel opsiList = new VitaminOpsiModel();
                                opsiList.setId(obj.getInt("id"));
                                opsiList.setNama(obj.getString("nama"));
                                opsiList.setUsia(obj.getString("jarak"));
                                opsiList.setDeskripsi(obj.getString("deskripsi"));
                                vitaminOpsi.add(opsiList);
                            }
                            request3();
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Terdapat Error Saat Mengambil Data", "Error", getActivity());
                            builder.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder(e.getMessage(), "Error", getActivity());
                    builder.show();
                    Log.e(TAG, "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Error Pada Koneksi", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestOpsiVitamin);
    }


    public void request3() {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/configure/imunisasi/fetch";
        Log.d(TAG, "request3: "+url);
        JsonObjectRequest RequestOpsiImun = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            int i;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                ImunisasiOpsiModel opsiList = new ImunisasiOpsiModel();
                                opsiList.setId(obj.getInt("id"));
                                opsiList.setNama(obj.getString("nama"));
                                opsiList.setUsia(obj.getInt("usia"));
                                imunisasiOpsi.add(opsiList);
                            }
                           request4();
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Terdapat Error Saat Mengambil Data", "Error", getActivity());
                            builder.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder(e.getMessage(), "Error", getActivity());
                    builder.show();
                    Log.e(TAG, "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Error Pada Koneksi", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestOpsiImun);
    }

    public void request4() {
        Map<String, Integer> params = new HashMap<>();
        params.put("balita_id", balita.getId());
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/vitamin/fetch";
        Log.d(TAG, "request4: "+url);
        JsonObjectRequest RequestVit = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            int i;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                VitaminModel v = new VitaminModel();
                                v.setBalita_id(obj.getInt("balita_id"));
                                v.setId(obj.getInt("id"));
                                v.setPetugas(obj.getString("petugas"));
                                v.setPosyandu_id(obj.getInt("posyandu_id"));
                                v.setTanggal_input(obj.getString("tanggal_input"));
                                v.setVitamin_id(obj.getInt("vitamin_id"));
                                v.setPetugas_id(obj.getInt("petugas_id"));
                                vitamin.add(v);
                            }
                            break;
                        case 1 :
                            break;
                    }

                    request5();
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder(e.getMessage(), "Error", getActivity());
                    builder.show();
                    Log.e(TAG, "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder(error.getMessage(), "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestVit);
    }

    public void request5() {
        Map<String, Integer> params = new HashMap<>();
        params.put("balita_id", balita.getId());
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/imunisasi/fetch";
        Log.d(TAG, "request5: "+url);
        JsonObjectRequest RequestImun = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            int i;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                ImunisasiModel imunList = new ImunisasiModel();
                                imunList.setBalita_id(obj.getInt("balita_id"));
                                imunList.setId(obj.getInt("id"));
                                imunList.setPetugas(obj.getString("petugas"));
                                imunList.setPetugas_id(obj.getInt("petugas_id"));
                                imunList.setTanggal_input(obj.getString("tanggal_input"));
                                imunList.setImun_id(obj.getInt("imun_id"));
                                imunList.setPosyandu_id(obj.getInt("posyandu_id"));
                                imunisasi.add(imunList);
                            }

                            break;
                        case 1 :

                            break;
                    }
                    exporting();


                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder(e.getMessage(), "Error", getActivity());
                    builder.show();
                    Log.e(TAG, "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Error Pada Koneksi", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestImun);
    }

    public void setText() {
        Nama.setText(balita.getNama());
        Ayah.setText(balita.getAyah());
        Ibu.setText(balita.getIbu());
        Usia.setText(String.format("%s Bulan", usia));
        String waktu = balita.getTanggal_lahir();
        String[] item = waktu.split("-");
        MonthConverter mc = new MonthConverter(Integer.parseInt(item[1])-1);
        String newWaktu = item[2]+"-"+mc.getMonth()+"-"+item[0];
        Lahir.setText(newWaktu);
        String gender = "Laki-Laki";
        if (balita.getGender().equals("P")) {
            gender = "Perempuan";
        }
        Gender.setText(gender);
        Alamat.setText(balita.getAlamat());
        Posyandu.setText(String.format("Posyandu %s", balita.getPosyandu()));
    }

    public void exporting() {

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+balita.getNama()+"_"+balita.getPosyandu()+".csv";
        try {
            Toast.makeText(getActivity(),path, Toast.LENGTH_SHORT).show();
            CSVWriter writer = new CSVWriter(new FileWriter(path));
            List<String[]> data = new ArrayList<>();
            data.add(new String[] {"Nama ", balita.getNama()});
            data.add(new String[] {"Ayah ", balita.getAyah()});
            data.add(new String[] {"Ibu ", balita.getIbu()});
            data.add(new String[] {"Tanggal Lahir ", balita.getTanggal_lahir()});
            data.add(new String[] {""});
            data.add(new String[] {"Pertumbuhan"});
            data.add(new String[] {"Usia","Berat", "Tinggi", "Lingkar Kepala", "Tanggal", "Petugas"});
            for(Integer i=0; i<kondisi.size(); i++)
            {
                KondisiModel k = kondisi.get(i);
                String waktu = k.getTanggal_input();
                String[] item5 = waktu.split("-");
                MonthConverter mc = new MonthConverter(Integer.parseInt(item5[1])-1);
                String newWaktu = item5[2]+"-"+mc.getMonth()+"-"+item5[0];
                data.add(new String[] {i+" Bulan", k.getBerat()+" Kg", k.getTinggi()+" Cm", k.getLingkar_kepala()+" Cm", newWaktu, k.getPetugas()});
            }
            data.add(new String[] {""});
            data.add(new String[] {"Imunisasi"});
            data.add(new String[] {"Usia", "Imunisasi", "Tanggal", "Petugas"});
            for(Integer i=0; i<imunisasiOpsi.size(); i++)
            {
                ImunisasiOpsiModel o = imunisasiOpsi.get(i);

                if(i<imunisasi.size()) {
                    ImunisasiModel im = imunisasi.get(i);
                    data.add(new String[] {o.getUsia() + " bln", o.getNama(), im.getTanggal_input(), im.getPetugas(), "", ""});
                }
                else {
                    data.add(new String[] {o.getUsia()+" bln",o.getNama(),null,null,"",""});
                }
            }
            data.add(new String[] {""});
            data.add(new String[] {"Vitamin"});
            data.add(new String[] {"Usia", "Vitamin", "Tanggal", "", "Petugas"});
            for(Integer i=0; i<vitaminOpsi.size(); i++)
            {
                String dt = null;
                String pt = null;
                String dt2 = null;
                String pt2 = null;
                VitaminOpsiModel o = vitaminOpsi.get(i);
                for(Integer i2=0; i2<vitamin.size(); i2++) {
                    VitaminModel v2 = vitamin.get(i2);
                    if (o.getId().equals(v2.getVitamin_id())) {
                        String d = v2.getTanggal_input();
                        String[] item2 = d.split("-");

                        if (item2[1].equals("02")) {
                            dt = v2.getTanggal_input();
                            pt = v2.getPetugas();
                        }
                        if (item2[1].equals("08")) {
                            dt2 = v2.getTanggal_input();
                            pt2 = v2.getPetugas();
                        }
                    }
                }
                data.add(new String[]{o.getUsia() + " bln", "Vitamin A " + o.getNama(), dt, dt2, pt, pt2});
            }
            writer.writeAll(data);
            writer.close();
        }
        catch (IOException e) {
            e.getStackTrace();
            Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fab.show();
    }
}
