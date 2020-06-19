package com.mposyandu.mposyandu.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.Validator;
import com.mposyandu.mposyandu.data.AnggotaModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.retrofitModel.BalitaModelPost;
import com.mposyandu.mposyandu.retrofitModel.UserModelPost;
import com.mposyandu.mposyandu.retrofitModel.UserModelResult;
import com.mposyandu.mposyandu.service.APIService;
import com.mposyandu.mposyandu.service.APIUtils;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.DialogBuilder;
import com.mposyandu.mposyandu.tools.FirebaseNotification;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;


public class TambahBalita extends Fragment {
    private Calendar myCalendar;
    private String newDate;
    private LinearLayout toolbar;
    private RelativeLayout relativeLayout;
    private String filenameimage, encodedimage;
    private View LayoutToolbar;
    private ImageView DisplayPicture, Cam;
    private TextInputLayout Nama, Ayah, Ibu, Lahir, Alamat, Berat, Tinggi, Lingkar;
    private String Gender;
    private UserModel user;
    private FloatingActionButton fab;
    private RadioGroup radioGroup;
    private Button next, back, submit;
    private DialogBuilder dialogBuilder;
    private ScrollView firstL, secondL;
    private UserModelPost anggota;
    private APIService apiService;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        apiService = APIUtils.getAPIService();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        anggota = bundle.getParcelable("posyandu");
        fab = getActivity().findViewById(R.id.fab);
        myCalendar = Calendar.getInstance();
        dialogBuilder = new DialogBuilder("Loading", "Submiting" , getActivity());
        fab.hide();
        View view = inflater.inflate(R.layout.tambah_balita, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = getActivity().findViewById(R.id.toolbar_layout);
        findViewById(view);
        LayoutToolbar = getLayoutInflater().inflate(R.layout.profile_balita_image, null);
        DisplayPicture = LayoutToolbar.findViewById(R.id.form_balita_thumb);
        Cam = LayoutToolbar.findViewById(R.id.form_balita_cam);
        Picasso.get().load(R.drawable.boy).into(DisplayPicture);
        Gender = "L";
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

                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        next = view.findViewById(R.id.form_balita_next);
        back = view.findViewById(R.id.form_balita_back);
        submit = view.findViewById(R.id.form_balita_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getText();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondL.setVisibility(View.VISIBLE);
                firstL.setVisibility(View.GONE);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondL.setVisibility(View.GONE);
                firstL.setVisibility(View.VISIBLE);
            }
        });
        Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSearch();
            }
        });
        setText();

        return view;
    }

    public void findViewById(View view) {
        Nama = view.findViewById(R.id.form_balita_nama);
        Ayah = view.findViewById(R.id.form_balita_ayah);
        Ibu = view.findViewById(R.id.form_balita_ibu);
        Ibu.setEnabled(false);
        Lahir = view.findViewById(R.id.form_balita_tanggal);
        Alamat = view.findViewById(R.id.form_balita_alamat);
        Alamat.setEnabled(false);
        Berat = view.findViewById(R.id.form_balita_berat);
        Tinggi = view.findViewById(R.id.form_balita_tinggi);
        Lingkar = view.findViewById(R.id.form_balita_lingkar);
        firstL = view.findViewById(R.id.first_layout);
        secondL = view.findViewById(R.id.second_layout);
        radioGroup = view.findViewById(R.id.form_balita_gender);
    }

    public void getText() {
        dialogBuilder.show();
        String nama_val = Nama.getEditText().getText().toString();
        String ayah_val = Ayah.getEditText().getText().toString();
        String berat_val = Berat.getEditText().getText().toString();
        String tinggi_val = Tinggi.getEditText().getText().toString();
        String lingkar_val = Lingkar.getEditText().getText().toString();
        Boolean flags = false;
        Validator validator = new Validator();
        if (!validator.Nama(nama_val)) {
            dialogBuilder.dismis();
            Nama.setError("Nama Tidak Valid (Minimum 6 Karakter)");
            flags = true;
        }
        if (!validator.Nama(ayah_val)) {
            dialogBuilder.dismis();
            Ayah.setError("Nama Ayah Tidak Valid (Minimum 6 Karakter)");
            flags = true;
        }
        if(!validator.Berat(berat_val)) {
            dialogBuilder.dismis();
            Berat.setError("Berat Tidak Valid");
            flags = true;
        }
        if(!validator.Tinggi(tinggi_val)) {
            dialogBuilder.dismis();
            Tinggi.setError("Berat Tidak Valid");
            flags = true;
        }
        if(!flags) {

            disableError();

            // CHILD PARAMETER FOR HTTP BODY REQUEST
//            Map<String, String> childParams = new HashMap<>();
//            childParams.put("email", String.valueOf(anggota.getEmail()));
//            childParams.put("nama", nama_val);
//            childParams.put("gender", String.valueOf(Gender));
//            childParams.put("tanggal_lahir", String.valueOf(newDate));
//            childParams.put("alamat", String.valueOf(anggota.getAlamat()));
//            childParams.put("ayah", ayah_val);
//            if (encodedimage == null) {
//                childParams.put("photo", "0");
//            }
//            else {
//                childParams.put("photo", String.valueOf(filenameimage));
//            }
//            childParams.put("encodedphoto", String.valueOf(encodedimage));
//            childParams.put("posyandu_id", String.valueOf(anggota.getPosyandu_id()));
//            childParams.put("creator_id", String.valueOf(user.getId()));
//            childParams.put("berat", berat_val);
//            childParams.put("tinggi", tinggi_val);
//            childParams.put("lingkar_kepala", lingkar_val);

            BalitaModelPost newBalita = new BalitaModelPost();
            newBalita.setEmail(String.valueOf(anggota.getEmail()));
            newBalita.setNama(nama_val);
            newBalita.setGender(String.valueOf(Gender));
            newBalita.setTanggalLahir(String.valueOf(newDate));
            newBalita.setAlamat(String.valueOf(anggota.getAlamat()));
            newBalita.setAyah(ayah_val);
            if (encodedimage == null) {
                newBalita.setPhoto("0");
            }
            else {
                newBalita.setPhoto(String.valueOf(filenameimage));
            }
            newBalita.setEncodedphoto(String.valueOf(encodedimage));
            newBalita.setPosyanduId(String.valueOf(anggota.getPosyanduId()));
            newBalita.setCreatorId(String.valueOf(user.getId()));
            newBalita.setBerat(berat_val);
            newBalita.setTinggi(tinggi_val);
            newBalita.setLingkarKepala(lingkar_val);

            // MOTHER PARAMETER FOR HTTP BODY REQUEST
//            Map<String, String> motherParams = new HashMap<>();
//            motherParams.put("email", String.valueOf(anggota.getEmail()));
//            motherParams.put("password", String.valueOf(anggota.getPassword()));
//            motherParams.put("nama", String.valueOf(anggota.getNama()));
//            motherParams.put("telepon", String.valueOf(anggota.getTelepon()));
//            motherParams.put("role_id", String.valueOf(anggota.getRole_id()));
//            motherParams.put("posyandu_id", String.valueOf(anggota.getPosyandu_id()));
//            motherParams.put("photo", String.valueOf(anggota.getPhoto()));
//            motherParams.put("encodedphoto", String.valueOf(anggota.getEncodedphoto()));
//            motherParams.put("creator_id", String.valueOf(anggota.getCreator_id()));
//            motherParams.put("alamat", String.valueOf(anggota.getAlamat()));
//            motherParams.put("token", String.valueOf(anggota.getToken()));
//            motherParams.put("firebase_token", String.valueOf(anggota.getFirebase_token()));

            UserModelPost newUser = new UserModelPost();
            newUser.setEmail(String.valueOf(anggota.getEmail()));
            newUser.setPassword(String.valueOf(anggota.getPassword()));
            newUser.setNama(String.valueOf(anggota.getNama()));
            newUser.setTelepon(String.valueOf(anggota.getTelepon()));
            newUser.setRoleId(String.valueOf(anggota.getRoleId()));
            newUser.setPosyanduId(String.valueOf(anggota.getPosyanduId()));
            newUser.setPhoto(String.valueOf(anggota.getPhoto()));
            newUser.setEncodedphoto(String.valueOf(anggota.getEncodedphoto()));
            newUser.setCreatorId(String.valueOf(anggota.getCreatorId()));
            newUser.setAlamat(String.valueOf(anggota.getAlamat()));
            newUser.setToken(String.valueOf(anggota.getToken()));
            newUser.setFirebaseToken(String.valueOf(anggota.getFirebaseToken()));

            // REQUEST TO SERVER
            Bundle bundle = getArguments();
            if(bundle != null){
                String options = bundle.getString("options");
                if(options != null && options.equals("CREATE_BABY_FROM_EXISTING_MOTHER")){
                    Log.d("LOGS", "getText: CREATE BABY FROM EXISTING MOTHER");
                    newBalita.setIbuId(String.valueOf(anggota.getId()));
                    registerChildRetrofit(newBalita); // REQUEST POST to register children
                }else{
                    registerMotherThenChildRetrofit(newUser, newBalita);
                }
            }else{
                registerMotherThenChildRetrofit(newUser, newBalita);
            }
        }

    }

    public void setText() {
        Ibu.getEditText().setText(anggota.getNama());
        Alamat.getEditText().setText(anggota.getAlamat());
    }

    private void registerMotherThenChildRetrofit(final UserModelPost newUser, final BalitaModelPost newBalita){
        apiService.registerUser(user.getId(), newUser).enqueue(new Callback<UserModelResult>() {
            @Override
            public void onResponse(Call<UserModelResult> call, retrofit2.Response<UserModelResult> response) {
                if(response.isSuccessful()) {
                    Log.i("LOGS", "post submitted to API." + response.body().toString());
                    dialogBuilder.dismis();
                    Integer id = response.body().getData();
                    Integer message = response.body().getMsg();
                    switch (message){
                        case 0 :
                            newBalita.setIbuId(String.valueOf(id)); // MOTHER ID FROM REGISTERED USER
                            dialogBuilder.show();
                            registerChildRetrofit(newBalita); // REQUEST POST to register children
                            break;
                        case 1 :
                            AlertBuilder builder2 = new AlertBuilder("Registrasi Gagal, harap penuhi persyataan", "Error", getActivity());
                            builder2.show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserModelResult> call, Throwable t) {
                Log.e("LOGS", "Unable to submit post to API.");
                dialogBuilder.dismis();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi", "Error", getActivity());
                builder.show();
            }
        });
    }

    private void registerChildRetrofit(final BalitaModelPost newBalita){
        Log.d("LOGS", "registerChildRetrofit: "+newBalita.toString());
        apiService.registerBalita(user.getId(), user.getPosyandu_id(), newBalita).enqueue(new Callback<UserModelResult>() {
            @Override
            public void onResponse(Call<UserModelResult> call, retrofit2.Response<UserModelResult> response) {
                dialogBuilder.dismis();
                if(response.isSuccessful()){
                    Log.i("LOGS", "post submitted to API." + response.body().toString());
                    Integer message = response.body().getMsg();
                    switch (message) {
                        case 0 :
                            Toast.makeText(getActivity(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                            String body = "Akun anak anda berhasil didaftarkan";
                            FirebaseNotification firebaseNotification = new FirebaseNotification(body, "Mobile Posyandu", "ibu%"+ newBalita.getIbuId());
                            firebaseNotification.PushNotification();
                            // BACK TO LIST
                            if(getActivity() != null){
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                if(fm.getBackStackEntryCount() > 0){
                                    fm.popBackStack();
                                    fm.popBackStack();
                                }
                            }
                            break;
                        case 1 :
                            Toast.makeText(getActivity(), "Registrasi Gagal", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserModelResult> call, Throwable t) {
                Log.e("LOGS", "Unable to submit post to API.");
                dialogBuilder.dismis();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi", "Error", getActivity());
                builder.show();
            }
        });
    }


    private void registerMotherThenChild(Map motherParams, final Map childParams) {
        // POST to register mother FIRST
        String Url = Database.getUrl()+"/user/"+user.getId()+"/register";
        Log.d("LOGS", "registerMotherThenChild: ibu "+ motherParams.toString());
        Log.d("LOGS", "registerMotherThenChild: balita "+ childParams.toString());
        JsonObjectRequest AddUsers = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(motherParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("LOGS", "onResponse: "+ response.toString());
                dialogBuilder.dismis();
                try {
                    Integer id = response.getInt("data");
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            childParams.put("ibu_id", String.valueOf(id)); // MOTHER ID FROM REGISTERED USER
                            dialogBuilder.show();
                            registerBalita(childParams); // REQUEST POST to register children
                            break;
                        case 1 :
                            AlertBuilder builder2 = new AlertBuilder("Registrasi Gagal, harap penuhi persyataan", "Error", getActivity());
                            builder2.show();
                            break;
                    }
                }
                catch (JSONException e) {
                    e.getStackTrace();
                    Log.w("LOGS", "onResponse: ", e);
                    AlertBuilder builder = new AlertBuilder("Registrasi Gagal, email sudah digunakan", "Error", getActivity());
                    builder.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                dialogBuilder.dismis();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi", "Error", getActivity());
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
        AddUsers.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Controller.getmInstance().addToRequestQueue(AddUsers);
    }

    public void registerBalita(final Map params) {
        String Url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/register";
        final JSONObject balitaData = new JSONObject(params);
        JsonObjectRequest AddBalita = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialogBuilder.dismis();
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                            String body = "Akun anak anda berhasil didaftarkan";
                            FirebaseNotification firebaseNotification = new FirebaseNotification(body, "Mobile Posyandu", "ibu%"+ balitaData.getInt("ibu_id"));
                            firebaseNotification.PushNotification();
                            // BACK TO LIST
                            if(getActivity() != null){
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                if(fm.getBackStackEntryCount() > 0){
                                    fm.popBackStack();
                                    fm.popBackStack();
                                }
                            }
                            break;
                        case 1 :
                            Toast.makeText(getActivity(), "Registrasi Gagal", Toast.LENGTH_SHORT).show();
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
                dialogBuilder.dismis();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json; charset=utf-8");
                return header;
            }
        };
        AddBalita.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Controller.getmInstance().addToRequestQueue(AddBalita);
    }

    public void disableError() {
        Nama.setErrorEnabled(false);
        Ayah.setErrorEnabled(false);
        Berat.setErrorEnabled(false);
        Tinggi.setErrorEnabled(false);
    }

    public void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        String newFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat newsdf = new SimpleDateFormat(newFormat, Locale.US);
        Lahir.getEditText().setText(sdf.format(myCalendar.getTime()));
        newDate = newsdf.format(myCalendar.getTime());
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
            Picasso.get().load(file)
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
        int nameIndex =returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
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
    }
}
