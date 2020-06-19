package com.mposyandu.mposyandu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import android.support.v4.app.FragmentManager;
import android.util.Base64;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.retrofitModel.UserModelPost;
import com.mposyandu.mposyandu.retrofitModel.UserModelResult;
import com.mposyandu.mposyandu.service.APIService;
import com.mposyandu.mposyandu.service.APIUtils;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.DialogBuilder;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.Validator;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;

public class TambahUser extends Fragment {
    private UserModel user;
    private DialogBuilder dialogBuilder;
    private ImageView Cam, DisplayPicture;
    private String filenameimage, encodedimage;
    private TextInputLayout Email, Nama, Alamat, Blok, No, Telepon;
    private Button Submit, Reset;
    private TextView Title;
    private Button Enable;
    private LinearLayout toolbar;
    private View LayoutToolbar;
    private FloatingActionButton fab;
    private String Role;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        Role = bundle.getString("role");
        View view = inflater.inflate(R.layout.profile_edit_user, container, false);
        dialogBuilder = new DialogBuilder("Submiting...","Progress",getActivity());
        toolbar = getActivity().findViewById(R.id.toolbar_layout);
        LayoutToolbar = getLayoutInflater().inflate(R.layout.profile_user_image, null);
        DisplayPicture = LayoutToolbar.findViewById(R.id.form_user_thumb);
        Cam = LayoutToolbar.findViewById(R.id.form_user_cam);
        fab = getActivity().findViewById(R.id.fab);
        Reset = view.findViewById(R.id.form_user_reset);
        Reset.setVisibility(View.GONE);
        fab.hide();
        findViewById(view);
        if (Role == "3") {
            Title.setText("Tambah Kader");
        }
        else if(Role == "4") {
            Title.setText("Tambah Ibu Balita");
        }
//        LayoutPassword.setVisibility(View.VISIBLE);
        Enable.setVisibility(View.GONE);
        editable();
        Picasso.get().load("http://mobileposyandu.000webhostapp.com/img/default_avatar.png")
                .transform(new CircleTransform())
                .into(DisplayPicture);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Simpan");
                builder.setMessage("Anda yakin untuk menambahkan data ini ?");
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getText();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSearch();
            }
        });
        return view;
    }

    public void findViewById(View view) {
        Email = view.findViewById(R.id.form_user_email);
        Nama = view.findViewById(R.id.form_user_nama);
//        Password = view.findViewById(R.id.form_user_password);
        Alamat = view.findViewById(R.id.form_user_alamat);
        Blok = view.findViewById(R.id.form_user_blok);
        No = view.findViewById(R.id.form_user_no);
        Telepon = view.findViewById(R.id.form_user_telepon);
        Title = view.findViewById(R.id.form_user_title);
//        LayoutPassword = view.findViewById(R.id.form_layout_password);
        Enable = view.findViewById(R.id.form_user_aktif);
        Submit = view.findViewById(R.id.form_user_submit);
    }

    public void registerUserRetrofit(final UserModelPost newUser){
        apiService.registerUser(user.getId(), newUser).enqueue(new Callback<UserModelResult>() {
            @Override
            public void onResponse(Call<UserModelResult> call, retrofit2.Response<UserModelResult> response) {
                if(response.isSuccessful()) {
                    Log.i("LOGS", "post submitted to API." + response.body().toString());
                    dialogBuilder.dismis();
                    Integer message = response.body().getMsg();
                    switch (message){
                        case 0 :
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                            builder1.setTitle("Sukses");
                            builder1.setMessage("Registrasi Berhasil");
                            builder1.setCancelable(true);
                            builder1.setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            // BACK TO USER LIST
                                            if(getActivity() != null){
                                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                                if(fm.getBackStackEntryCount() > 0){
                                                    fm.popBackStack();
                                                }
                                            }
                                        }
                                    });

                            AlertDialog alert = builder1.create();
                            alert.show();
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

    public void register(final Map map) {
        String Url = Database.getUrl()+"/user/"+user.getId()+"/register";
        Log.d("LOGS", "register: "+ map.toString());
        JsonObjectRequest AddUsers = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("LOGS", "onResponse: "+ response.toString());
                dialogBuilder.dismis();
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                            builder1.setTitle("Sukses");
                            builder1.setMessage("Registrasi Berhasil");
                            builder1.setCancelable(true);
                            builder1.setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            // BACK TO USER LIST
                                            if(getActivity() != null){
                                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                                if(fm.getBackStackEntryCount() > 0){
                                                    fm.popBackStack();
                                                }
                                            }
                                        }
                                    });

                            AlertDialog alert = builder1.create();
                            alert.show();
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

    public void editable() {
        Nama.setEnabled(true);
        Email.setEnabled(true);
        Alamat.setEnabled(true);
//        Password.setEnabled(true);
        Blok.setEnabled(true);
        No.setEnabled(true);
        Telepon.setEnabled(true);
    }

    public void disableError() {
        Nama.setErrorEnabled(false);
        Email.setErrorEnabled(false);
//        Password.setErrorEnabled(false);
        Alamat.setErrorEnabled(false);
        No.setErrorEnabled(false);
        Blok.setErrorEnabled(false);
        Telepon.setErrorEnabled(false);
    }

    public void getText() {
        dialogBuilder.show();
        String Email_value = Email.getEditText().getText().toString();
        String Nama_value = Nama.getEditText().getText().toString();
//        String Password_value = Password.getEditText().getText().toString();
        String Password_value = "12345678"; // DEFAULT Password for new user
        String Alamat_value = Alamat.getEditText().getText().toString()+" "+
                Blok.getEditText().getText().toString()+"-"+
                No.getEditText().getText().toString();
        String Telepon_value = Telepon.getEditText().getText().toString();
        Boolean flags = false;
        Validator validator = new Validator();
        if(!validator.Email(Email_value)) {
            dialogBuilder.dismis();
            Email.setError("Email Tidak Valid (contoh: adi@gmail.com)");
            flags = true;
        }
        if(!validator.Nama(Nama_value)) {
            dialogBuilder.dismis();
            Nama.setError("Nama Tidak Valid (Minimum 6 Karakter)");
            flags = true;
        }
//        if(!validator.Pass(Password_value)) {
//            dialogBuilder.dismis();
//            Password.setError("Password Tidak Valid (Minimum 6 Karakter)");
//            flags = true;
//        }
        if(!validator.Alamat(Alamat_value)) {
            dialogBuilder.dismis();
            Alamat.setError("Alamat Tidak Valid ");
            Blok.setError("Alamat Tidak Valid");
            No.setError("Alamat Tidak Valid");
            flags = true;
        }
        if(!validator.Telepon   (Telepon_value)) {
            dialogBuilder.dismis();
            Telepon.setError("Nomor Telepon Tidak Valid");
            flags = true;
        }
        if(!flags) {
            disableError();
            Map<String, String> params = new HashMap<>();
            params.put("email", Email_value);
            params.put("password", Password_value);
            params.put("nama", Nama_value);
            params.put("telepon", Telepon_value);
            params.put("role_id", Role);
            params.put("posyandu_id", String.valueOf(user.getPosyandu_id()));
            if (encodedimage == null) {
                params.put("photo", "");
            }
            else {
                params.put("photo", filenameimage);
            }

            params.put("encodedphoto", encodedimage);
            params.put("creator_id", String.valueOf(user.getCreator_id()));
            params.put("alamat", Alamat_value);
            params.put("token", UUID.randomUUID().toString());
            params.put("firebase_token", "");

            UserModelPost newUser = new UserModelPost();
            newUser.setEmail(Email_value);
            newUser.setPassword(Password_value);
            newUser.setNama(Nama_value);
            newUser.setTelepon(Telepon_value);
            newUser.setRoleId(Role);
            newUser.setPosyanduId(String.valueOf(user.getPosyandu_id()));
            if(encodedimage == null){
                newUser.setPhoto("");
            }else {
                newUser.setPhoto(filenameimage);
            }
            newUser.setEncodedphoto(encodedimage);
            newUser.setCreatorId(String.valueOf(user.getCreator_id()));
            newUser.setAlamat(Alamat_value);
            newUser.setToken(UUID.randomUUID().toString());
            newUser.setFirebaseToken("");

            if(Role.equals("3")){
                // Register Kader
//                register(params);
                registerUserRetrofit(newUser);

            }else if(Role.equals("4")){
                // Register Orang Tua Balita

                dialogBuilder.dismis();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Langkah berikutnya");
                builder.setMessage("Tambahkan balita");
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                bundle.putParcelable("posyandu", newUser);
                Fragment fragment = new TambahBalita();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_ketua, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
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
        fab.show();
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
