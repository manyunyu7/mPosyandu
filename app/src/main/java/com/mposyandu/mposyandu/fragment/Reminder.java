package com.mposyandu.mposyandu.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.data.JadwalModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.FirebaseNotification;
import com.mposyandu.mposyandu.tools.MonthConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Reminder extends Fragment {
    private FloatingActionButton fab;
    private String newDate, newTime, currentDate;
    private TextInputLayout tanggal, waktu, tempat;
    private Calendar calendar;
    private UserModel user;
    private JadwalModel jadwal;
    private LinearLayout disp;
    private TextView date, time, place;
    private CalendarView calendarView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_event, container, false);
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        fab = getActivity().findViewById(R.id.fab);
        calendarView = view.findViewById(R.id.calendarView2);
        calendar = Calendar.getInstance();
        date = view.findViewById(R.id.cal_date);
        time = view.findViewById(R.id.cal_time);
        place = view.findViewById(R.id.cal_place);
        disp  = view.findViewById(R.id.disp_0);
        newDate = "";
        newTime = "";
        request();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
        return view;
    }

    public void updateLabel(int year, int month, int day) {

        String myFormat = "dd-MM-yyyy";
        String newFormat = "yyyy-MM-dd";
        currentDate = day+"-"+(month+1)+"-"+year;
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal.getEditText().setText(currentDate);
        SimpleDateFormat newsdf = new SimpleDateFormat(newFormat, Locale.US);
        newDate = year+"-"+(month+1)+"-"+day;
    }
    public void updateLabel2(int hour, int minute) {
        waktu.getEditText().setText(hour+":"+minute+":00");
        newTime = hour+":"+minute+":00";
    };

    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tambah Kegiatan");
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.tambah_event, null);
        builder.setView(view);
        tanggal = view.findViewById(R.id.form_event_tanggal);
        waktu = view.findViewById(R.id.form_event_waktu);
        tempat = view.findViewById(R.id.form_event_tempat);
        final int y = calendar.get(Calendar.YEAR);
        final int m = calendar.get(Calendar.MONTH);
        final int d = calendar.get(Calendar.DAY_OF_MONTH);

        tanggal.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        updateLabel(year, month, dayOfMonth);
                    }
                }, y,m,d);
                datePickerDialog.show();
            }
        });
        waktu.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        updateLabel2(hourOfDay, minute);
                    }
                }, 0,0,true);
                timePickerDialog.show();
            }
        });
        builder.setNegativeButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getText();
            }
        });
    }

    public void getText() {
        String tempat_val = tempat.getEditText().getText().toString();
        boolean flag = true;
        if(newDate.isEmpty()) {
            flag = false;
            tanggal.setError("Invalid Tanggal");
        }
        if(newTime.isEmpty()) {
            flag = false;
            waktu.setError("Invalid Waktu");
        }
        if(tempat_val.isEmpty()) {
            flag = false;
            tempat.setError("Invalid Tempat");
        }
        if(flag) {
            tanggal.setErrorEnabled(false);
            waktu.setErrorEnabled(false);
            tempat.setErrorEnabled(false);
            Map<String, String> params = new HashMap<String, String>();

            params.put("tanggal", newDate);
            params.put("waktu", newTime);
            params.put("tempat", tempat_val);
            params.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
            params.put("creator_id", String.valueOf(user.getId()));
            if(jadwal.getId() != null ){
                params.put("id", String.valueOf(jadwal.getId()));
                addRequest(params);
            }
            else {
                addRequest2(params);
            }

        }
    }

    public void request() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/jadwal/fetch";
        Log.d("LOGS", "request reminder: "+url);
        JsonObjectRequest listJadwal = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    jadwal = new JadwalModel();
                    Integer errors = response.getInt("msg");
                    Log.d("LOGS", "onResponse: reminder"+ response);
                    switch (errors) {
                        case 0 :
                            disp.setVisibility(View.VISIBLE);
                            JSONObject obj = response.getJSONObject("data");
                            jadwal.setId(obj.getInt("id"));
                            jadwal.setCreator(obj.getString("petugas"));
                            jadwal.setTanggal(obj.getString("tanggal"));
                            jadwal.setWaktu(obj.getString("waktu"));
                            jadwal.setPosyandu_id(obj.getInt("posyandu_id"));
                            jadwal.setKegiatan(obj.getString("tempat"));
                            jadwal.setCreator_id(obj.getInt("creator_id"));
                            setdisp();
                            break;
                        case 1 :
                            disp.setVisibility(View.GONE);
                            createDialog();
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
                AlertBuilder builder = new AlertBuilder("Terdapat masalah pada koneksi", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(listJadwal);
    }

    public void setdisp() {
        String waktu = jadwal.getTanggal();
        String item[] = waktu.split("-");
        Integer month = Integer.parseInt(item[1]);
        Integer year = Integer.parseInt(item[0]);
        Integer day = Integer.parseInt(item[2]);
        MonthConverter mc = new MonthConverter(month-1);



        if (calendar.get(Calendar.MONTH)+1 <=  month &&
                calendar.get(Calendar.DAY_OF_MONTH) <= day && calendar.get(Calendar.YEAR) <= year) {
            disp.setVisibility(View.VISIBLE);
            date.setText(item[2]+"-"+mc.getMonth()+"-"+item[0]);
            time.setText(jadwal.getWaktu());
            place.setText(jadwal.getKegiatan());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = null;
            try {
                date = (Date)dateFormat.parse(waktu);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long mills = date.getTime();
            Log.d("A", String.valueOf(mills));
            calendarView.setDate(mills);
        }
        else {
            disp.setVisibility(View.GONE);
        }
    }

    public void setnull() {
        date.setText("");
        time.setText("");
        place.setText("");
    }

    public void addRequest2(Map map) {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/jadwal/register";
        JsonObjectRequest addReminder = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Berhasil Menambahkan", Toast.LENGTH_SHORT).show();
                            String myFormat = "dd-MM-yyyy";
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            FirebaseNotification firebaseNotification = new FirebaseNotification("Jadwal Posyandu Berikutnya : "+currentDate ,"mobilePosyandu", "posyandu%"+user.getPosyandu_id());
                            firebaseNotification.PushNotification();
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Gagal Menambahkan", "Error", getActivity());
                            builder.show();
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
                AlertBuilder builder = new AlertBuilder("Terjadi kesalahan pada koneksi internet", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(addReminder);
    }

    public void addRequest(Map map) {
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/jadwal/update";
        JsonObjectRequest addReminder = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Berhasil Menambahkan", Toast.LENGTH_SHORT).show();
                            String myFormat = "dd-MM-yyyy";
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            FirebaseNotification firebaseNotification = new FirebaseNotification("Jadwal Posyandu Bulan depan : "+currentDate ,"mobilePosyandu", "posyandu%"+user.getPosyandu_id());
                            firebaseNotification.PushNotification();
                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Gagal Menambahkan", "Error", getActivity());
                            builder.show();
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
                AlertBuilder builder = new AlertBuilder("Terjadi kesalahan pada koneksi internet", "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(addReminder);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user.getRole_id() == 2) {
            fab.show();
            fab.setImageResource(R.drawable.ic_today_white_24dp);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fab.hide();
        fab.setImageResource(R.drawable.ic_group_add_white_24dp);
    }
}
