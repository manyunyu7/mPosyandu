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
import com.mposyandu.mposyandu.adapter.ListImunisasiAdapter;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.ImunisasiModel;
import com.mposyandu.mposyandu.data.ImunisasiOpsiModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.DialogBuilder;

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

public class ListImunisasi extends Fragment {
    private List<ImunisasiOpsiModel> opsi = new ArrayList<>();
    private List<ImunisasiModel> imun = new ArrayList<>();
    private ListImunisasiAdapter adapter;
    private UserModel user;
    private BalitaModel balita;
    private Calendar myCalendar;
    private FloatingActionButton fab;
    private TextInputLayout tanggal;
    private ArrayAdapter<String> opsiAdapter;
    private ArrayList<String> opsiImunisasi = new ArrayList<>();
    private DialogBuilder dialogBuilder;
    private RecyclerView rv;
    private SwipeRefreshLayout sw;
    private Spinner spinner;
    private String newDate;
    private String id_imun;
    private static ListImunisasi instance = null;

    public static ListImunisasi getInstance() {
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

                return true;
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_imunisasi, container, false);
        myCalendar = Calendar.getInstance();
        dialogBuilder = new DialogBuilder("Sedang Menambahkan...", "Process", getActivity());
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        balita = bundle.getParcelable("balita");
        myCalendar = Calendar.getInstance();
        newDate = "";
        fab = getActivity().findViewById(R.id.fab);
        rv = view.findViewById(R.id.list_imun);
        sw = view.findViewById(R.id.swipe_list_imun);

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
        rv.setLayoutManager(layoutManager);
        request(0);
        opsiAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, opsiImunisasi);
        adapter = new ListImunisasiAdapter(opsi, imun, user, ListImunisasi.this , getActivity());
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
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/configure/imunisasi/fetch";
        JsonObjectRequest RequestOpsiImun = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            opsi.clear();
                            int i;

                            if(e.equals(1)) {
                                opsiImunisasi.clear();
                            }

                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                ImunisasiOpsiModel opsiList = new ImunisasiOpsiModel();
                                opsiList.setId(obj.getInt("id"));
                                opsiList.setNama(obj.getString("nama"));
                                opsiList.setUsia(obj.getInt("usia"));
                                opsi.add(opsiList);
                                opsiImunisasi.add(obj.getString("nama"));
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
        Controller.getmInstance().addToRequestQueue(RequestOpsiImun);
    }

    public void request2() {
        Map<String, Integer> params = new HashMap<>();
        params.put("balita_id", balita.getId());
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/imunisasi/fetch";
        JsonObjectRequest RequestImun = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    imun.clear();
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
                                imun.add(imunList);
                            }
                            break;
                        case 1 :

                            break;
                    }
                        adapter.notifyDataSetChanged();

                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder2 = new AlertBuilder(e.getMessage(), "Error", getActivity());
                    builder2.show();
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
        Controller.getmInstance().addToRequestQueue(RequestImun);
    }

    public void edit (final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getLayoutInflater();
        @SuppressLint("InflateParams") View alertView = layoutInflater.inflate(R.layout.tambah_imunisasi, null);
        builder.setTitle("Edit Imunisasi");
        builder.setView(alertView);
        tanggal = alertView.findViewById(R.id.form_imunisasi_add_tanggal);
        spinner =  alertView.findViewById(R.id.opsi_imunisasi);

        if(opsiAdapter.isEmpty()) {
            request(1);
        }


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
        setText(position);
        builder.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( imun.get(position).getId() != null ) {
                    dialog.dismiss();
                    Integer id = spinner.getSelectedItemPosition()+1;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("balita_id", String.valueOf(balita.getId()));
                    params.put("id", String.valueOf(imun.get(position).getId()));
                    params.put("petugas_id", String.valueOf(user.getId() ));
                    params.put("tanggal_input", newDate);
                    params.put("imun_id", String.valueOf(id));
                    editImun(params);
                }
            }
        });

    }
    public void editImun(Map map) {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/imunisasi/update";
        JsonObjectRequest editImun = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Update Berhasil", Toast.LENGTH_SHORT).show();
                            rv.getRecycledViewPool().clear();
                            adapter.notifyDataSetChanged();
                            request(0);
                            break;
                        case 1 :
                            Toast.makeText(getActivity(), "Update Gagal", Toast.LENGTH_SHORT).show();
                            break;
                    }
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
        Controller.getmInstance().addToRequestQueue(editImun);
    }

    public void setText(Integer p) {
        for (Integer i = 0; i < imun.size(); i++) {
            if (opsi.get(p).getId().equals(imun.get(i).getImun_id())) {
                String waktu = imun.get(i).getTanggal_input();
                String[] item = waktu.split("-");
                String newWaktu = item[2] + "-" + item[1] + "-" + item[0];
                tanggal.getEditText().setText(newWaktu);
                newDate = waktu;
                id_imun = String.valueOf(imun.get(i).getId());

            }
        }
    }

    public void delete (final Integer position) {
        for (Integer i =0; i<imun.size();i++) {
            if(imun.get(i).getImun_id().equals(opsi.get(position).getId())) {
                id_imun = String.valueOf(imun.get(i).getId());
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
                Toast.makeText(getActivity(), id_imun, Toast.LENGTH_SHORT).show();
                if(id_imun != null ) {
                    alert.dismiss();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", id_imun);
                    deleteReq(params);
                }
            }
        });
    }

    public void deleteReq(Map map) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+balita.getId()+"/imunisasi/delete";
        JsonObjectRequest deleteImun = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
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
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                pd.dismiss();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi ", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(deleteImun);
    }

    public void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        String newFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat newsdf = new SimpleDateFormat(newFormat, Locale.US);
        tanggal.getEditText().setText(sdf.format(myCalendar.getTime()));
        newDate = newsdf.format(myCalendar.getTime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rv.setAdapter(null);
        adapter.notifyDataSetChanged();
        fab.hide();
    }
}
