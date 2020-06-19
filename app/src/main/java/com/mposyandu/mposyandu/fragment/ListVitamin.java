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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.adapter.ListVitaminAdapter;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.data.VitaminModel;
import com.mposyandu.mposyandu.data.VitaminOpsiModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;

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

public class ListVitamin extends Fragment {
    private List<VitaminModel> vitamin = new ArrayList<>();
    private List<VitaminOpsiModel> opsi = new ArrayList<>();
    private RecyclerView rv;
    private UserModel user;
    private BalitaModel balita;
    private ListVitaminAdapter adapter;
    private FloatingActionButton fab;
    private String newDate;
    private TextInputLayout tanggal;
    private Calendar myCalendar;
    private Spinner spinner;
    private ArrayAdapter<String> opsiAdapter;
    private ArrayList<String> opsiVitaminList = new ArrayList<>();
    private SwipeRefreshLayout sw;
    private Integer id_vit, id_position;
    private static ListVitamin instance = null;

    public static ListVitamin getInstance() {
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setHasOptionsMenu(true);
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

                return true;
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_vitamin, container, false);
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        balita = bundle.getParcelable("balita");
        fab = getActivity().findViewById(R.id.fab);
        myCalendar = Calendar.getInstance();
        newDate = "";
        rv = view.findViewById(R.id.list_vitamin);
        sw = view.findViewById(R.id.swipe_list_vitamin);
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setHasFixedSize(true);
        opsiAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, opsiVitaminList);
        rv.setLayoutManager(layoutManager);
        request(0);
        adapter = new ListVitaminAdapter(vitamin, opsi, ListVitamin.this, getActivity(), user);
        rv.setAdapter(adapter);
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request(0);
            }
        });
        return view;
    }

    public void request(final Integer e) {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/configure/vitamin/fetch";
        JsonObjectRequest RequestOpsiVitamin = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            opsi.clear();

                            if(e.equals(1)) {
                                opsiVitaminList.clear();
                            }
                            int i;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                VitaminOpsiModel opsiList = new VitaminOpsiModel();
                                opsiList.setId(obj.getInt("id"));
                                opsiList.setNama(obj.getString("nama"));
                                opsiList.setUsia(obj.getString("jarak"));
                                opsiList.setDeskripsi(obj.getString("deskripsi"));
                                opsi.add(opsiList);
                                opsiVitaminList.add("Vitamin A Kapsul "+obj.getString("nama")+" Usia "+obj.getString("jarak"));
                                opsiAdapter.notifyDataSetChanged();
                            }
                            if(e.equals(0)) {
                                request2();
                            }

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

    public void request2() {
        Map<String, Integer> params = new HashMap<>();
        params.put("balita_id", balita.getId());
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/vitamin/fetch";
        JsonObjectRequest RequestVit = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    vitamin.clear();
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
                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder(e.getMessage(), "Error", getActivity());
                    builder.show();
                }
                sw.setRefreshing(false);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        rv.setAdapter(null);
        adapter.notifyDataSetChanged();
        fab.hide();
    }

    public void delete(Integer postion, String id) {
        for (Integer i = 0; i < vitamin.size(); i++) {
            if (opsi.get(postion).getId().equals(vitamin.get(i).getVitamin_id())) {
                String waktu = vitamin.get(i).getTanggal_input();
                String[] item = waktu.split("-");
                String newWaktu = item[2] + "-" + item[1] + "-" + item[0];
                if(item[1].equals(id)) {
                    newDate = waktu;
                    id_vit = vitamin.get(i).getId();
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Persetujuan");
        builder.setMessage("Hapus data yang telah dipilih ?");
        builder.setNegativeButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                if(id_vit != null) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", String.valueOf(id_vit));
                    deleteReq(params);
                }
                else {
                    AlertBuilder builder = new AlertBuilder("Data tidak tersedia", "Error", getActivity());
                    builder.show();
                }
            }
        });

    }

    public void deleteReq(Map map) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/vitamin/delete";
        Log.d(TAG, "deleteReq: "+url+" with params "+ new JSONObject(map));
        JsonObjectRequest deleteVit = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Berhasil Menghapus", Toast.LENGTH_SHORT).show();
                            rv.getRecycledViewPool().clear();
                            request(0);
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
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing Data",
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
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(deleteVit);
    }

    public void edit(Integer position,  String id) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        newDate = "";
        LayoutInflater layoutInflater = getLayoutInflater();
        @SuppressLint("InflateParams") View alertView = layoutInflater.inflate(R.layout.tambah_vitamin, null);
        tanggal = alertView.findViewById(R.id.form_vitamin_add_tanggal);
        spinner =  alertView.findViewById(R.id.opsi_vitamin);
        request(1);
        spinner.setAdapter(opsiAdapter);
        spinner.setSelection(position, true);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        id_position = position;
        alertDialog.setTitle("Edit Vitamin");
        alertDialog.setView(alertView);
        alertDialog.setNegativeButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        tanggal.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    myCalendar.setTime(newsdf.parse(newDate));
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
        Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
        setText(position, id);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer id = spinner.getSelectedItemPosition()+1;
                Toast.makeText(getActivity(), String .valueOf(id), Toast.LENGTH_SHORT).show();

                if(id_vit != null) {
                    dialog.dismiss();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", String.valueOf(id_vit));
                    params.put("balita_id", String.valueOf(balita.getId()));
                    params.put("vitamin_id", String.valueOf(id));
                    params.put("petugas_id", String.valueOf(user.getId()));
                    params.put("tanggal_input", newDate);
                    editReq(params);
                }
                else {
                    AlertBuilder builder = new AlertBuilder("Data tidak tersedia", "Error", getActivity());
                    builder.show();
                }

            }
        });

    }
    public void editReq(Map map) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/vitamin/update";
        JsonObjectRequest editVit = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Update Berhasil", Toast.LENGTH_SHORT).show();
                            rv.getRecycledViewPool().clear();
                            adapter.notifyDataSetChanged();
                            break;
                        case 1 :
                            Toast.makeText(getActivity(), "Update Gagal", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    pd.dismiss();
                }
                catch (JSONException e) {
                    e.getStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
            }
        });
        Controller.getmInstance().addToRequestQueue(editVit);
    }

    public void setText(Integer p, String id) {
        for (Integer i = 0; i < vitamin.size(); i++) {
            if (opsi.get(p).getId().equals(vitamin.get(i).getVitamin_id())) {
                String waktu = vitamin.get(i).getTanggal_input();
                String[] item = waktu.split("-");
                String newWaktu = item[2] + "-" + item[1] + "-" + item[0];
                if(item[1].equals(id)) {
                    tanggal.getEditText().setText(newWaktu);
                    newDate = waktu;
                    id_vit = vitamin.get(i).getId();
                }
                if(item[1].equals(id)) {
                    tanggal.getEditText().setText(newWaktu);
                    newDate = waktu;
                    id_vit = vitamin.get(i).getId();
                }
            }
        }
    }

    public void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        String newFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat newsdf = new SimpleDateFormat(newFormat, Locale.US);
        tanggal.getEditText().setText(sdf.format(myCalendar.getTime()));
        newDate = newsdf.format(myCalendar.getTime());
    }
}
