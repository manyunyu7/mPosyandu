package com.mposyandu.mposyandu.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.Validator;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.DialogBuilder;
import com.mposyandu.mposyandu.tools.FirebaseNotification;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class EditBalita extends Fragment {
    private LinearLayout toolbar;
    private FloatingActionButton fab;
    private TextInputLayout Nama, Ayah, Ibu, Lahir, Alamat;
    private View LayoutToolbar;
    private RadioGroup radioGroup;
    private ImageView DisplayPicture, Cam;
    private DialogBuilder dialogBuilder;
    private String Gender, newDate, encodedimage, filenameimage;
    private Button Submit;
    private AppBarLayout appBarLayout;
    private Calendar myCalendar;
    private UserModel user;
    private Toolbar toolbar2;
    private BalitaModel balita;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tambah_balita, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = getActivity().findViewById(R.id.toolbar_layout);
        fab = getActivity().findViewById(R.id.fab);
        appBarLayout = getActivity().findViewById(R.id.aaaaa);
        toolbar2 = getActivity().findViewById(R.id.toolbar);
        fab.hide();
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        balita = bundle.getParcelable("balita");
        findViewById(view);
        newDate = "";
        myCalendar = Calendar.getInstance();
        LayoutToolbar = getLayoutInflater().inflate(R.layout.profile_balita_image, null);
        dialogBuilder = new DialogBuilder("Submiting...","Progress", getActivity());
        DisplayPicture = LayoutToolbar.findViewById(R.id.form_balita_thumb);
        Cam = LayoutToolbar.findViewById(R.id.form_balita_cam);
        radioGroup = view.findViewById(R.id.form_balita_gender);
        RadioButton r1 = view.findViewById(R.id.radioButton2);
        RadioButton r2 = view.findViewById(R.id.radioButton);
        Gender = "";
        if (balita.getPhoto().equals("0")) {
            if (balita.getGender().equals("L")) {
                Picasso.get().load(R.drawable.boy).into(DisplayPicture);
                r1.setChecked(true);
            }
            else {
                Picasso.get().load(R.drawable.girl).into(DisplayPicture);
                r2.setChecked(true);

            }
        }
        else {
            Picasso.get().load(Database.getUrl()+"/"+balita.getPhoto())
                    .transform(new CircleTransform())
                    .into(DisplayPicture);
        }
        setText();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton check = (RadioButton)group.findViewById(checkedId);

                boolean isChecked = check.isChecked();
                if (check.getText().toString().equals("L"))  {
                    Gender = "L";
                    Picasso.get().load(R.drawable.boy).into(DisplayPicture);
                }
                else if(check.getText().toString().equals("P")) {
                    Gender = "P";
                    Picasso.get().load(R.drawable.girl).into(DisplayPicture);
                }
            }
        });
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        Lahir.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    myCalendar.setTime(newsdf.parse(balita.getTanggal_lahir()));
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
        Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSearch();
            }
        });

        Submit.setText("Submit");
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getText();
            }
        });

        return view;
    }


    public void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        String newFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat newsdf = new SimpleDateFormat(newFormat, Locale.US);
        Lahir.getEditText().setText(sdf.format(myCalendar.getTime()));
        newDate = newsdf.format(myCalendar.getTime());
    }

    public void getText() {
        boolean flags = false;
        dialogBuilder.show();
        String nama_val = Nama.getEditText().getText().toString();
        String ayah_val = Ayah.getEditText().getText().toString();
        Validator validator = new Validator();
        if(newDate.isEmpty()) {
            newDate = balita.getTanggal_lahir();
        }
        if(!validator.Nama(nama_val)) {
            dialogBuilder.dismis();
            Nama.setError("Nama Tidak Valid");
            flags = true;
        }
        if(!validator.Nama(ayah_val)) {
            dialogBuilder.dismis();
            Ayah.setError("Nama Tidak Valid");
            flags = true;
        }
        if(!validator.Date(newDate)) {
            flags = true;
            dialogBuilder.dismis();
            Lahir.setError("Invalid Tanggal");
        }
        if(Gender.isEmpty()) {
            flags = true;
            dialogBuilder.dismis();
        }
        if(!flags) {
            Map<String, String> params = new HashMap<>();
            params.put("email", balita.getEmail());
            params.put("id", String.valueOf(balita.getId()));
            params.put("nama", nama_val);
            params.put("gender", Gender);
            params.put("tanggal_lahir", newDate);
            params.put("alamat", balita.getAlamat());
            params.put("ibu_id", String.valueOf(balita.getIbu_id()));
            params.put("ayah", ayah_val);
            if (encodedimage == null) {
                params.put("photo", balita.getPhoto());
            }
            else {
                params.put("photo", filenameimage);
            }
            params.put("encodedphoto", encodedimage);
            params.put("posyandu_id", String.valueOf(balita.getPosyandu_id()));
            params.put("creator_id", String.valueOf(balita.getCreator_id()));
            edit(params);
        }
    }

    public void edit(Map map) {
        String Url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/update";
        JsonObjectRequest editbalita = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialogBuilder.dismis();
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Update Berhasil", Toast.LENGTH_SHORT).show();

                            // SENDING NOTIFICATION TO MOTHER
                            String body = "Akun anak "+ balita.getNama() +" anda berhasil diperbaharui";
                            FirebaseNotification firebaseNotification = new FirebaseNotification(body, "Mobile Posyandu", "ibu%"+String.valueOf(balita.getIbu_id()));
                            firebaseNotification.PushNotification();

                            // BACK TO BALITA LIST
                            if(getActivity() != null){
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                if(fm.getBackStackEntryCount() > 0){
                                    fm.popBackStack();
                                    fm.popBackStack();
                                }
                            }

                            break;
                        case 1 :
                            AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan saat merubah data", "Error", getActivity());
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
                dialogBuilder.dismis();
                AlertBuilder builder = new AlertBuilder("Terdapat Kesalahan pada koneksi", "Error", getActivity());
                builder.show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json; charset=utf-8");
                return header;
            }
        };
        Controller.getmInstance().addToRequestQueue(editbalita);
    }
    public void setText() {
        Nama.getEditText().setText(balita.getNama());
        Ayah.getEditText().setText(balita.getAyah());
        Ibu.getEditText().setText(balita.getIbu());
        String waktu = balita.getTanggal_lahir();
        String[] item = waktu.split("-");
        String newWaktu = item[2]+"-"+item[1]+"-"+item[0];
        Lahir.getEditText().setText(newWaktu);
        Alamat.getEditText().setText(balita.getAlamat());
        Gender = balita.getGender();
    }

    public void findViewById(View view) {
        Nama = view.findViewById(R.id.form_balita_nama);
        Ayah = view.findViewById(R.id.form_balita_ayah);
        Ibu = view.findViewById(R.id.form_balita_ibu);
        Ibu.setEnabled(false);
        Lahir = view.findViewById(R.id.form_balita_tanggal);
        Alamat = view.findViewById(R.id.form_balita_alamat);
        Alamat.setEnabled(false);
        Submit = view.findViewById(R.id.form_balita_next);
    }

    public void imageSearch() {
        Intent intent = new Intent();
        intent.setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), 123);
    }

    public void getImage(Uri file) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), file);
            String encoded = base64Image(bitmap);
            String filename = imageFilename(file);
            this.encodedimage = encoded;
            this.filenameimage = filename;
            Picasso.get()
                    .load(file)
                    .transform(new CircleTransform())
                    .into(DisplayPicture);
        }
        catch (Exception e) {
            e.getStackTrace();
        }
    }

    public String imageFilename(Uri file) {
        Cursor returnCursor =
                getActivity().getContentResolver()
                        .query(file, null, null, null, null);
        int nameIndex = (returnCursor).getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

        returnCursor.moveToFirst();
        return returnCursor.getString(nameIndex);
    }

    public String base64Image(Bitmap bitmap) {
        ByteArrayOutputStream baos;
        byte[] baat;
        String encodeString = null;
        try
        {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            baat = baos.toByteArray();
            encodeString = Base64.encodeToString(baat, Base64.DEFAULT);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return encodeString;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedfile = data.getData();
            getImage(selectedfile);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toolbar2.setTitle("Ketua");
        toolbar2.setSubtitle(String.format("Posyandu %s", user.getPosyandu()));
        toolbar.removeView(LayoutToolbar);
    }

    @Override
    public void onPause() {
        super.onPause();
        toolbar.removeView(LayoutToolbar);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.addView(LayoutToolbar);
        toolbar2.setTitle(balita.getNama());
        toolbar2.setSubtitle("Posyandu "+user.getPosyandu());
        if (balita.getGender().equals("P")) {
            appBarLayout.setBackgroundResource(R.drawable.half_circle_w);
            toolbar2.setBackgroundResource(R.color.colorAccent);
            if (Build.VERSION.SDK_INT >= 21) {
                getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorAccentDark)); //status bar or the time bar at the top
            }
            fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            appBarLayout.setBackgroundResource(R.drawable.half_circle);
            toolbar2.setBackgroundResource(R.color.colorPrimary);

        }
    }
}
