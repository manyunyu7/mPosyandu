package com.mposyandu.mposyandu.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.adapter.ViewPagerAdapter;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.ImunisasiOpsiModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.data.VitaminOpsiModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.Validator;
import com.mposyandu.mposyandu.tools.FirebaseNotification;
import com.mposyandu.mposyandu.tools.MonthConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailBalita extends android.support.v4.app.Fragment {
    private BalitaModel balita;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<VitaminOpsiModel> opsi = new ArrayList<>();
    private List<ImunisasiOpsiModel> opsi2 = new ArrayList<>();
    private UserModel user;
    private ArrayAdapter<String> adapter, adapter2;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> list2 = new ArrayList<>();
    private Spinner spinner, spinner2;
    private Bundle bundle;
    private int[] tabIcons = {
            R.drawable.ic_child_care_white_24dp,
            R.drawable.ic_kesehatan,
            R.drawable.ic_vitamin,
            R.drawable.ic_imunitation,
            R.drawable.ic_kms
    };
    private TextInputLayout tanggal, berat, tinggi, lingkarkepala, ke;
    private Calendar myCalendar;
    private String newDate;
    private ProgressDialog progressDialog;
    private AppBarLayout appBarLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        balita = bundle.getParcelable("balita");
        user = bundle.getParcelable("user");

        myCalendar = Calendar.getInstance();
        View view = inflater.inflate(R.layout.detail_balita, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Sedang Memproses...");
        progressDialog.setCancelable(false);

        fab = getActivity().findViewById(R.id.fab);
        appBarLayout = getActivity().findViewById(R.id.aaaaa);
        toolbar = getActivity().findViewById(R.id.toolbar);
        tabLayout = getActivity().findViewById(R.id.tab_ketua);

        newDate = "";
        viewPager = view.findViewById(R.id.viewPager);
        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setCurrentItem(0, true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0 :
                        fab.hide();
                        break;
                    case 1 :
                        fab.show();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addKondisi();
                            }
                        });
                        break;
                    case 2 :
                        fab.show();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addVitamin();

                            }
                        });
                        break;
                    case 3 :
                        fab.show();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addImunisasi();
                            }
                        });
                        fab.show();
                        break;
                    case 4:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupTabIcons();

        return view;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new ProfileBalita(), bundle);
        adapter.addFrag(new ListPertumbuhan(), bundle);
        adapter.addFrag(new ListVitamin(),  bundle);
        adapter.addFrag(new ListImunisasi(), bundle);
        adapter.addFrag(new GrafikBerat(), bundle);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toolbar.setTitle("Ketua");
        toolbar.setSubtitle(String.format("Posyandu %s", user.getPosyandu()));
        tabLayout.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.ic_baby_add);
        fab.show();
        appBarLayout.setBackgroundResource(R.drawable.half_circle);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark)); //status bar or the time bar at the top
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        toolbar.setTitle("Ketua");
        toolbar.setSubtitle(String.format("Posyandu %s", user.getPosyandu()));
        tabLayout.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.ic_baby_add);
        appBarLayout.setBackgroundResource(R.drawable.half_circle);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark)); //status bar or the time bar at the top
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setImageResource(R.drawable.ic_note_add_white_24dp);
        toolbar.setTitle(balita.getNama());
        toolbar.setSubtitle("Posyandu "+user.getPosyandu());
        tabLayout.setVisibility(View.VISIBLE);
        if (balita.getGender().equals("P")) {
            appBarLayout.setBackgroundResource(R.color.colorAccent);
            toolbar.setBackgroundResource(R.color.colorAccent);
            if (Build.VERSION.SDK_INT >= 21) {
                getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorAccentDark)); //status bar or the time bar at the top
            }
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(android.R.color.white));
            fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        }
        else {
            appBarLayout.setBackgroundResource(R.color.colorPrimary);
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));

        }
    }

    public void addKondisi() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflate = getLayoutInflater();
        final View alertView = inflate.inflate(R.layout.tambah_kondisi, null);
        builder.setView(alertView);
        builder.setTitle("Tambah Kesehatan");
        tanggal = alertView.findViewById(R.id.form_kondisi_add_tanggal);
        berat = alertView.findViewById(R.id.form_kondisi_add_berat);
        tinggi = alertView.findViewById(R.id.form_kondisi_add_tinggi);
        lingkarkepala = alertView.findViewById(R.id.form_kondisi_add_lingkar_kepala);
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
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Validator validator = new Validator();


        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
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
        final AlertDialog dialogAlert = builder.create();
        dialogAlert.show();
        dialogAlert.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final String berat_val = berat.getEditText().getText().toString();
                final String tinggi_val = tinggi.getEditText().getText().toString();
                final String lingkar_val = lingkarkepala.getEditText().getText().toString();
                Boolean flags = false;
                if(!validator.Date(newDate)) {
                    flags = true;
                    progressDialog.dismiss();
                    tanggal.setError("Invalid Tanggal");
                }
                if(!validator.Tinggi(tinggi_val)) {
                    flags = true;
                    progressDialog.dismiss();
                    tinggi.setError("Invalid Tinggi");
                }
                if(!validator.Berat(berat_val)) {
                    flags = true;
                    progressDialog.dismiss();
                    berat.setError("Invalid Berat");
                }
                if(!flags) {
                    dialogAlert.dismiss();
                    Map<String, String> map = new HashMap<>();
                    map.put("balita_id", String.valueOf(balita.getId()));
                    map.put("berat", berat_val);
                    map.put("tinggi", tinggi_val);
                    map.put("lingkar_kepala", lingkar_val);
                    map.put("tanggal_input", newDate);
                    map.put("petugas_id", String.valueOf(user.getId()));
                    map.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
                    addRequsetKondisi(map);
                }
            }
        });

    }

    public void addRequsetKondisi(final Map map) {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+
                balita.getId()+"/condition/register";
        Log.d("LOGS", "addRequsetKondisi: "+url);
        JsonObjectRequest AddKondisi = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Berhasil Menambahkan", Toast.LENGTH_SHORT).show();
                            String date = (String) map.get("tanggal_input");
                            String[] da = date.split("-");
                            MonthConverter monthConverter = new MonthConverter(Integer.parseInt(da[1])-1);
                            String newwaktu = da[2]+"/"+monthConverter.getMonth()+"/"+da[0];
                            ListPertumbuhan.getInstance().request();
                            GrafikBerat.getInstance().request();
                            FirebaseNotification firebaseNotification = new FirebaseNotification("Data Pertumbuhan tanggal "+newwaktu+
                                    ", dengan nama "+ balita.getNama()+" berhasil ditambahkan",
                                    "Posyandu "+user.getPosyandu(),
                                    "ibu%"+String.valueOf(balita.getIbu_id()));
                            firebaseNotification.PushNotification();
                            break;
                        case 1 :
                            Toast.makeText(getActivity(), "Gagal Menambahkan", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    progressDialog.dismiss();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing data",
                            "Error", getActivity());
                    builder.show();
                    Log.e("LOGS addRequsetKondisi", "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                progressDialog.dismiss();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(AddKondisi);
    }

    public void addVitamin() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflate = getLayoutInflater();
        @SuppressLint("InflateParams") final View alertView = inflate.inflate(R.layout.tambah_vitamin, null);
        builder.setView(alertView);
        builder.setTitle("Vitamin");
        tanggal = alertView.findViewById(R.id.form_vitamin_add_tanggal);
        spinner = alertView.findViewById(R.id.opsi_vitamin);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        if(adapter.isEmpty()) {
            getOpsiVitamin();
        }
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Deskripsi", opsi.get(position).getDeskripsi());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Validator validator = new Validator();


        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
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
        final AlertDialog dialogAlert = builder.create();
        dialogAlert.show();
        dialogAlert.getButton(dialogAlert.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final String vitamin_id = String.valueOf(opsi.get(spinner.getSelectedItemPosition()).getId());
                Boolean flags = false;
                if (!validator.Date(newDate)) {
                    progressDialog.dismiss();
                    flags = true;
                    tanggal.setError("Invalid Tanggal");
                }
                if (!flags) {
                    dialogAlert.dismiss();
                    Map<String, String> map = new HashMap<>();
                    map.put("balita_id", String.valueOf(balita.getId()));
                    map.put("vitamin_id", vitamin_id);
                    map.put("dosis", "1");
                    map.put("tanggal_input", newDate);
                    map.put("petugas_id", String.valueOf(user.getId()));
                    map.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
                    addRequsetVitamin(map);
                }
            }
        });
    }

    public void addRequsetVitamin(Map map) {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+
                balita.getId()+"/vitamin/register";
        Log.d("LOGS", "addRequsetVitamin: "+url);
        JsonObjectRequest AddVitamin = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Berhasil Menambahkan", Toast.LENGTH_SHORT).show();
                            ListVitamin.getInstance().request(0);
                            break;
                        case 1 :
                            Toast.makeText(getActivity(), "Gagal Menambahkan", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    progressDialog.dismiss();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing data",
                            "Error", getActivity());
                    builder.show();
                    Log.e("LOGS addRequsetVitamin", "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                progressDialog.dismiss();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(AddVitamin);
    }

    public void addImunisasi() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflate = getLayoutInflater();
        @SuppressLint("InflateParams") final View alertView = inflate.inflate(R.layout.tambah_imunisasi, null);
        builder.setView(alertView);
        builder.setTitle("Imunisasi");
        tanggal = alertView.findViewById(R.id.form_imunisasi_add_tanggal);
        spinner2 = alertView.findViewById(R.id.opsi_imunisasi);
        adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list2);
        if(adapter2.isEmpty()) {
            getOpsiImunisasi();
        }
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Deskripsi", opsi2.get(position).getNama());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Validator validator = new Validator();
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
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
        final AlertDialog dialogAlert = builder.create();
        dialogAlert.show();
        dialogAlert.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final String imunisasi_id = String.valueOf(opsi2.get(spinner2.getSelectedItemPosition()).getId());
                Boolean flags = false;
                if (!validator.Date(newDate)) {
                    flags = true;
                    progressDialog.dismiss();
                    tanggal.setError("Invalid Tanggal");
                }
                if (imunisasi_id.isEmpty()) {
                    flags = true;
                    progressDialog.dismiss();
                    tinggi.setError("Invalid Tinggi");
                }
                if (!flags) {
                    dialogAlert.dismiss();
                    Map<String, String> map = new HashMap<>();
                    map.put("balita_id", String.valueOf(balita.getId()));
                    map.put("imun_id", imunisasi_id);
                    map.put("tanggal_input", newDate);
                    map.put("petugas_id", String.valueOf(user.getId()));
                    map.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
                    addRequestImunisasi(map);
                }
            }
        });
    }

    public void addRequestImunisasi(Map map) {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+
                balita.getId()+"/imunisasi/register";
        Log.d("LOGS", "addRequestImunisasi: "+url);
        JsonObjectRequest AddImunisasi = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Berhasil Menambahkan", Toast.LENGTH_SHORT).show();
                            ListImunisasi.getInstance().request(0);
                            break;
                        case 1 :
                            Toast.makeText(getActivity(), "Gagal Menambahkan", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    progressDialog.dismiss();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing data",
                            "Error", getActivity());
                    builder.show();
                    Log.e("LOGS RequestImunisasi", "onResponse: error", e);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                progressDialog.dismiss();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(AddImunisasi);
    }

    public void getOpsiVitamin() {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/configure/vitamin/fetch";
        Log.d("LOGS", "getOpsiVitamin: "+url);
        JsonObjectRequest RequestOpsiVitamin = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            opsi.clear();
                            list.clear();
                            int i;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                VitaminOpsiModel opsiList = new VitaminOpsiModel();
                                opsiList.setId(obj.getInt("id"));
                                opsiList.setNama(obj.getString("nama"));
                                opsiList.setUsia(obj.getString("jarak"));
                                opsiList.setDeskripsi(obj.getString("deskripsi"));
                                opsi.add(opsiList);
                                list.add("Vitamin A Kapsul "+obj.getString("nama")+" Usia "+obj.getString("jarak"));
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Data Kosong", "Error", getActivity());
                            builder.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing data",
                            "Error", getActivity());
                    builder.show();
                    Log.e("LOGS getOpsiVitamin", "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan Pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestOpsiVitamin);
    }

    public void getOpsiImunisasi() {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/configure/imunisasi/fetch";
        Log.d("LOGS", "getOpsiImunisasi: "+url);
        JsonObjectRequest RequestOpsiImunisasi = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            opsi2.clear();
                            list2.clear();
                            int i;
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                ImunisasiOpsiModel opsiList = new ImunisasiOpsiModel();
                                opsiList.setId(obj.getInt("id"));
                                opsiList.setNama(obj.getString("nama"));
                                opsiList.setUsia(obj.getInt("usia"));
                                opsi2.add(opsiList);
                                list2.add(obj.getString("nama"));
                                adapter2.notifyDataSetChanged();
                            }
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Data Kosong", "Error", getActivity());
                            builder.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing data",
                            "Error", getActivity());
                    builder.show();
                    Log.e("LOGS getOpsiImunisasi", "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestOpsiImunisasi);
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
