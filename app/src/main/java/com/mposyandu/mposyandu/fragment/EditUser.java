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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.data.AnggotaModel;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class EditUser extends Fragment {
    private String alamatvalidator = "^(.*)(?:\\ )+(.*)(?:\\-)(\\d*)$";
    private Pattern pattern = Pattern.compile(alamatvalidator);
    private Matcher matcher;
    private UserModel user;
    private AnggotaModel anggota;
    private DialogBuilder dialogBuilder;
    private RelativeLayout rl;
    private ImageView DisplayPicture, Cam;
    private String filenameimage, encodedimage;
    private TextInputLayout Email, Nama, Alamat, Blok, No, Telepon, newUser, newPass;
    private LinearLayout toolbar;
    private View LayoutToolbar;
    private Button Save, Enable, Reset;
    private FloatingActionButton fab;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        anggota = bundle.getParcelable("posyandu");
        user = bundle.getParcelable("user");
        View view = inflater.inflate(R.layout.profile_edit_user, container, false);
        toolbar = getActivity().findViewById(R.id.toolbar_layout);
        LayoutToolbar = getLayoutInflater().inflate(R.layout.profile_user_image, null);
        DisplayPicture = LayoutToolbar.findViewById(R.id.form_user_thumb);
        Cam = LayoutToolbar.findViewById(R.id.form_user_cam);
        rl = LayoutToolbar.findViewById(R.id.form_user_layoutcam);
        Picasso.get().load(Database.getUrl()+"/"+anggota.getPhoto())
                .transform(new CircleTransform())
                .into(DisplayPicture);
        dialogBuilder = new DialogBuilder("Submiting...","Progress", getActivity());
        findViewById(view);
        fab = getActivity().findViewById(R.id.fab);
        if (user.getNama().equals(anggota.getNama())) {
            Enable.setVisibility(View.GONE);
        }
        Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSearch();
            }
        });
        Enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.show();
                if(anggota.getActive() == 0) {
                    Enable.setText("Aktif");
                    Enable.setTextColor(getResources().getColor(R.color.colorAccent));
                    anggota.setActive(1);
                }
                else{
                    Enable.setText("Tidak Aktif");
                    Enable.setTextColor(getResources().getColor(R.color.colorGrey));
                    anggota.setActive(0);
                }
                changestatus();
            }
        });
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getLayoutInflater().inflate(R.layout.forget_password, null);
                newPass = view.findViewById(R.id.forget_password);
                newUser = view.findViewById(R.id.forget_email);
                newUser.setVisibility(View.GONE);
                builder.setView(view);
                builder.setTitle("Ubah Password");
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        forgetPassword();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Simpan");
                builder.setMessage("Anda yakin ingin mengubah data ini ?");
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
        editable();
        setText();
        return view;
    }

    public void forgetPassword() {
        String Password_value = newPass.getEditText().getText().toString();
        Boolean flags= false;
        Validator validator = new Validator();
        if (!validator.Pass(Password_value)) {
            flags = true;
            newPass.setError("Email tidak Valid");
        }
        if(!flags) {
            Map<String, String> params = new HashMap<>();
            params.put("header", String.valueOf(0));
            params.put("password", Password_value);
            params.put("email", anggota.getEmail());
            resetPassword(params);
        }
    }

    public  void resetPassword(Map map) {
        String Url = Database.getUrl()+"/user/"+0+"/reset";
        JsonObjectRequest Reset = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            AlertBuilder builder = new AlertBuilder("Password Berhasil direset", "Sukses", getActivity());
                            builder.show();
                            break;
                        case 1:
                            AlertBuilder builder2 = new AlertBuilder("Password gagal direset", "Error", getActivity());
                            builder2.show();
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

            }
        });
        Controller.getmInstance().addToRequestQueue(Reset);
    }

    public void findViewById(View view) {
        Email = view.findViewById(R.id.form_user_email);
        Nama = view.findViewById(R.id.form_user_nama);
        Alamat = view.findViewById(R.id.form_user_alamat);
        Blok = view.findViewById(R.id.form_user_blok);
        No = view.findViewById(R.id.form_user_no);
        Telepon = view.findViewById(R.id.form_user_telepon);
        String a = anggota.getAlamat();
        matcher = pattern.matcher(a);
        Reset = view.findViewById(R.id.form_user_reset);
        Enable = view.findViewById(R.id.form_user_aktif);
        Save = view.findViewById(R.id.form_user_submit);
    }

    public void editable() {
        Nama.setEnabled(true);
        Email.setEnabled(true);
        Alamat.setEnabled(true);
        Blok.setEnabled(true);
        No.setEnabled(true);
        Telepon.setEnabled(true);
    }

    public void changestatus() {
        String Url = Database.getUrl()+"/user/"+user.getId()+"/status";
        Map<String, Integer> map = new HashMap<>();
        map.put("id", anggota.getId());
        map.put("active", anggota.getActive());
        JsonObjectRequest statusEdit = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialogBuilder.dismis();
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Update Berhasil", Toast.LENGTH_SHORT).show();
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
        Controller.getmInstance().addToRequestQueue(statusEdit);
    }

    public void setText() {
        String alamat = "";
        String no = "";
        String blok = "";
        while (matcher.find()) {
            alamat = matcher.group(1);
            blok = matcher.group(2);
            no = matcher.group(3);
        }
        (Nama.getEditText()).setText(anggota.getNama());
        (Email.getEditText()).setText(anggota.getEmail());
        (Alamat.getEditText()).setText(alamat);
        (Blok.getEditText()).setText(blok);
        (No.getEditText()).setText(no);
        (Telepon.getEditText()).setText(anggota.getTelepon());
        Picasso.get().load(Database.getUrl()+"/"+anggota.getPhoto())
                .transform(new CircleTransform())
                .into(DisplayPicture);
        if(anggota.getActive() == 0) {

            Enable.setText("Tidak Aktif");
            Enable.setTextColor(getResources().getColor(R.color.colorGrey));
        }
        else{
            Enable.setText("Aktif");

        }
    }

    public void getText() {
        dialogBuilder.show();
        String Email_value = (Email.getEditText()).getText().toString();
        String Nama_value = (Nama.getEditText()).getText().toString();
        String Alamat_value = (Alamat.getEditText()).getText().toString()+" "+
                (Blok.getEditText()).getText().toString()+"-"+
                (No.getEditText()).getText().toString();
        String Telepon_value = (Telepon.getEditText()).getText().toString();
        Boolean flags = false;
        Validator validator = new Validator();
        if(!validator.Email(Email_value)) {
            dialogBuilder.dismis();
            Email.setError("Email Tidak Valid (contoh: adi@mail.com)");
            flags = true;
        }
        if(!validator.Nama(Nama_value)) {
            dialogBuilder.dismis();
            Nama.setError("Nama Tidak Valid (Minimum 6 karakter)");
            flags = true;
        }
        if(!validator.Alamat(Alamat_value)) {
            dialogBuilder.dismis();
            Alamat.setError("Alamat Tidak Valid ");
            Blok.setError("Alamat Tidak Valid");
            No.setError("Alamat Tidak Valid");
            flags = true;
        }
        if(!validator.Telepon(Telepon_value)) {
            dialogBuilder.dismis();
            Telepon.setError("Nomor Telepon Tidak Valid");
            flags = true;
        }
        if(!flags) {
            disableError();

            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(anggota.getId()));
            params.put("email", Email_value);
            params.put("nama", Nama_value);
            params.put("telepon", Telepon_value);
            params.put("role_id", String.valueOf(anggota.getRole_id()));
            params.put("posyandu_id", String.valueOf(anggota.getPosyandu_id()));
            if (encodedimage == null) {
                params.put("photo", anggota.getPhoto());
            }
            else {
                params.put("photo", filenameimage);
            }
            params.put("encodedphoto", encodedimage);
            params.put("alamat", Alamat_value);
            params.put("token", anggota.getToken());
            params.put("creator_id", String.valueOf(anggota.getCreator_id()));
            params.put("firebase_token", anggota.getFirebase_token());
            edit(params);
        }
    }

    public void edit(Map map) {
        String Url = Database.getUrl()+"/user/"+user.getId()+"/update";
        JsonObjectRequest edituser = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialogBuilder.dismis();
                try {
                    Integer errors = response.getInt("msg");
                    switch (errors) {
                        case 0 :
                            Toast.makeText(getActivity(), "Update Berhasil", Toast.LENGTH_SHORT).show();

                            // BACK TO USER LIST
                            if(getActivity() != null){
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                if(fm.getBackStackEntryCount() > 0){
                                    fm.popBackStack();
                                    fm.popBackStack();
                                }
                            }

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
        Controller.getmInstance().addToRequestQueue(edituser);
    }

    public void disableError() {
        Nama.setErrorEnabled(false);
        Email.setErrorEnabled(false);
        Alamat.setErrorEnabled(false);
        No.setErrorEnabled(false);
        Blok.setErrorEnabled(false);
        Telepon.setErrorEnabled(false);
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
        toolbar.removeView(LayoutToolbar);
        fab.hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        toolbar.removeView(LayoutToolbar);
        fab.hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        toolbar.removeView(LayoutToolbar);
        fab.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.addView(LayoutToolbar);
        fab.hide();
    }
}
