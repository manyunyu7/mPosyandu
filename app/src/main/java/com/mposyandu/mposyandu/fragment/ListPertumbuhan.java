package com.mposyandu.mposyandu.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.adapter.ListPertumbuhanAdapter;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.KondisiModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.DialogBuilder;
import com.mposyandu.mposyandu.Validator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.mposyandu.mposyandu.Controller.TAG;

public class ListPertumbuhan extends Fragment {
    private RecyclerView rv;
    private List<KondisiModel> kondisi = new ArrayList<>();
    private UserModel user;
    private BalitaModel balita;
    private FloatingActionButton fab;
    private ListPertumbuhanAdapter adapter;
    private DialogBuilder dialogBuilder;
    private TextInputLayout tanggal, berat, tinggi, lingkarkepala;
    private Calendar myCalendar;
    private String newDate, editDate;
    private SwipeRefreshLayout sw;
    private static ListPertumbuhan instance = null;

    public static ListPertumbuhan getInstance() {
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
        menu.clear();
        inflater.inflate(R.menu.ketua, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem item2 = menu.findItem(R.id.action_export);
        item2.setVisible(false);
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



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_pertumbuhan, container, false);
        myCalendar = Calendar.getInstance();
        dialogBuilder = new DialogBuilder("Sedang Menambahkan...", "Process", getActivity());
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        balita = bundle.getParcelable("balita");
        fab = getActivity().findViewById(R.id.fab);
        sw = view.findViewById(R.id.swipe_list_tumbuh);
        rv = view.findViewById(R.id.list_kondisi);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        if(user.getRole_id() == 4) {
            fab.hide();
        }
        else {
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy >0) {
                        // Scroll Down

                        if (fab.isShown()) {
                            fab.hide();
                        }
                    }
                    else if (dy <0) {
                        // Scroll Up
                        if (!fab.isShown()) {
                            fab.show();
                        }
                    }
                }
            });
        }

        rv.setHasFixedSize(true);
        rv.setLayoutManager(layoutManager);
        request();
        adapter = new ListPertumbuhanAdapter(kondisi, user, ListPertumbuhan.this, getActivity());
        rv.setAdapter(adapter);
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
            }
        });
        return view;
    }

    public void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        String newFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat newsdf = new SimpleDateFormat(newFormat, Locale.US);
        tanggal.getEditText().setText(sdf.format(myCalendar.getTime()));
        newDate = newsdf.format(myCalendar.getTime());
    }

    public void delete(final Integer position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Persetujuan");
        alertDialog.setMessage("Yakin ingin menghapus data ini ?");
        alertDialog.setNegativeButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = alertDialog.create();
        alert.show();
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(adapter.getItem(position).getId()));
                deletePertumbuhan(params);
            }
        });
    }

    public void deletePertumbuhan(Map map) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Sedang Memproses...");
        pd.setCancelable(true);
        pd.show();
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/condition/delete";
        Log.d(TAG, "deletePertumbuhan: "+url+" with params "+new JSONObject(map));
        JsonObjectRequest deletePertumbuhan = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Berhasil Menghapus", Toast.LENGTH_SHORT).show();
                            rv.getRecycledViewPool().clear();
                            adapter.notifyDataSetChanged();
                            request();
                            GrafikBerat.getInstance().request();
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Gagal Menghapus", "Error", getActivity());
                            builder.show();
                            break;
                    }
                    pd.dismiss();
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    pd.dismiss();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing data",
                            "Error", getActivity());
                    builder.show();
                    Log.e(TAG, "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                pd.dismiss();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi ",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(deletePertumbuhan);
    }
    public void edit(Integer position) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        newDate = "";
        LayoutInflater layoutInflater = getLayoutInflater();
        @SuppressLint("InflateParams") View alertView = layoutInflater.inflate(R.layout.tambah_kondisi, null);
        tanggal = alertView.findViewById(R.id.form_kondisi_add_tanggal);
        berat = alertView.findViewById(R.id.form_kondisi_add_berat);
        tinggi = alertView.findViewById(R.id.form_kondisi_add_tinggi);
        lingkarkepala = alertView.findViewById(R.id.form_kondisi_add_lingkar_kepala);
        final KondisiModel k = kondisi.get(position);
        alertDialog.setTitle("Edit Bulan #"+ k.getUsia());
        alertDialog.setView(alertView);
        alertDialog.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setNegativeButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        updateLabel();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };



        tanggal.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    myCalendar.setTime(newsdf.parse(k.getTanggal_input()));
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                catch (ParseException e) {
                    e.getStackTrace();
                    Log.d("Error", e.getMessage());
                }
            }
        });
        setText(k);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.show();
                Validator validator = new Validator();
                final String berat_val = berat.getEditText().getText().toString();
                final String tinggi_val = tinggi.getEditText().getText().toString();
                final String lingkar_val = lingkarkepala.getEditText().getText().toString();
                Boolean flags = false;
                if(newDate.isEmpty()) {
                    newDate = k.getTanggal_input();
                }
                if(!validator.Date(newDate)) {

                    flags = true;
                    dialogBuilder.dismis();
                    tanggal.setError("Invalid Tanggal");
                }
                if(!validator.Tinggi(tinggi_val)) {
                    flags = true;
                    dialogBuilder.dismis();
                    tinggi.setError("Invalid Tinggi");
                }
                if(!validator.Berat(berat_val)) {
                    flags = true;
                    dialogBuilder.dismis();
                    berat.setError("Invalid Berat");
                }
                if(!flags) {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(k.getId()));
                    map.put("balita_id", String.valueOf(balita.getId()));
                    map.put("berat", berat_val);
                    map.put("tinggi", tinggi_val);
                    map.put("lingkar_kepala", lingkar_val);
                    map.put("tanggal_input", newDate);
                    map.put("petugas_id", String.valueOf(user.getId()));
                    editReq(map);
                }
            }
        });
    }

    public void setText(KondisiModel k) {
        String waktu = k.getTanggal_input();
        String[] item = waktu.split("-");
        String[] month = new String[] {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "November", "Desember"};
        String newWaktu = item[2]+"-"+item[1]+"-"+item[0];
        tanggal.getEditText().setText(newWaktu);
        berat.getEditText().setText(String.valueOf(k.getBerat()));
        lingkarkepala.getEditText().setText(String.valueOf(k.getLingkar_kepala()));
        tinggi.getEditText().setText(String.valueOf(k.getTinggi()));
    }

    public void editReq(Map map) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Sedang Memproses...");
        pd.setCancelable(true);
        pd.show();
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+
                balita.getId()+"/condition/update";
        JsonObjectRequest EditKondisi = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dialogBuilder.dismis();
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Update Berhasil", Toast.LENGTH_SHORT).show();
                            rv.getRecycledViewPool().clear();
                            adapter.notifyDataSetChanged();
                            request();
                            GrafikBerat.getInstance().request();

                            break;
                        case 1 :
                            Toast.makeText(getActivity(), "Update Gagal", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    pd.dismiss();
                }
                catch (JSONException e) {
                    dialogBuilder.dismis();
                    e.getStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                dialogBuilder.dismis();
            }
        });
        Controller.getmInstance().addToRequestQueue(EditKondisi);
    }

    public void request() {
        Map<String, Integer> params = new HashMap<>();
        params.put("balita_id", balita.getId());
        params.put("empty", 1);
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+
                balita.getId()+"/condition/fetch";
        JsonObjectRequest RequestKondisi = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    kondisi.clear();
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            int i;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                KondisiModel kondisiList = new KondisiModel();
                                kondisiList.setUsia(obj.getString("usia"));
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
                                if(obj.getDouble("tinggi") == 0.0 || obj.getDouble("tinggi") == 0) {
                                    kondisiList.setTinggi(null);
                                }
                                else {
                                    kondisiList.setTinggi(obj.getDouble("tinggi"));
                                }
                                if(obj.getDouble("berat") == 0.0 || obj.getDouble("berat") == 0) {
                                    kondisiList.setBerat(null);
                                }
                                else {
                                    kondisiList.setBerat(obj.getDouble("berat") );
                                }
                                kondisi.add(kondisiList);
                            }
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Terdapat Error Saat Mengambil Data", "Error", getActivity());
                            builder.show();
                            break;
                    }
                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder(e.toString(), "Error", getActivity());
                    builder.show();
                }
                sw.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Error Pada Koneksi", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestKondisi, "KONDISI");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        rv.setAdapter(null);
        adapter.notifyDataSetChanged();
    }
}
