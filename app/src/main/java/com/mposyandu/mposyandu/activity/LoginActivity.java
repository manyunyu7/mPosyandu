package com.mposyandu.mposyandu.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputLayout;
import android.app.AlertDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;

public class LoginActivity extends AppCompatActivity {
private TextInputLayout Username, Password, newUser;
private Button Login;
private TextView forget;
private ProgressDialog progressDialog;
private UserModel users = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Sign in");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        Username = findViewById(R.id.login_username);
        Password = findViewById(R.id.login_password);
        Login = findViewById(R.id.login);
        forget = findViewById(R.id.forget);
        dialogCheckPermission();
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View view = getLayoutInflater().inflate(R.layout.forget_password, null);
                TextInputLayout pass = view.findViewById(R.id.forget_password);
                newUser = view.findViewById(R.id.forget_email);
                pass.setVisibility(View.GONE);
                builder.setView(view);
                builder.setTitle("Lupa Password");
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forgetPassword(dialog);
                    }
                });
            }
        });
        if(!checksessions()){
            Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    String Username_value = Username.getEditText().getText().toString();
                    String Password_value = Password.getEditText().getText().toString();
                    Boolean flags= false;
                    Validator validator = new Validator();
                    if (!validator.Pass(Password_value)) {
                        flags = true;
                        Password.setError("Password tidak Valid");
                        progressDialog.dismiss();
                    } if(!validator.Email(Username_value)) {
                        flags = true;
                        Username.setError("Email tidak Valid");
                        progressDialog.dismiss();
                    } if (!flags) {
                        progressDialog.dismiss();
                        progressDialog.setMessage("Authenticating");
                        Username.setErrorEnabled(false);
                        Password.setErrorEnabled(false);
                        Map<String, String> params = new HashMap<>();
                        params.put("email", Username_value);
                        params.put("password", Password_value);
                        params.put("firebase_token", FirebaseInstanceId.getInstance().getToken());
                        login(params);
                    }
                }
            });
        }
        else {
            moveActivity();
        }
    }

    public void forgetPassword(AlertDialog dialog) {
        progressDialog.show();
        String Username_value = newUser.getEditText().getText().toString();
        Boolean flags= false;
        Validator validator = new Validator();
        if (!validator.Email(Username_value)) {
            flags = true;
            newUser.setError("Email tidak Valid");
            progressDialog.dismiss();
        }
        if(!flags) {
            dialog.dismiss();
            Map<String, String> params = new HashMap<>();
            params.put("email", Username_value);
            params.put("header", String.valueOf(1));
            params.put("password", "12345678"); // Default Password
            resetPassword(params);
        }
    }

    public  void resetPassword(Map map) {
        String Url = Database.getUrl()+"/user/"+0+"/reset";
        JsonObjectRequest Reset = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            try {
                progressDialog.setMessage("Retriving data");
                Integer errors = response.getInt("msg");
                switch (errors) {
                    case 0 :
                        progressDialog.dismiss();
                        AlertBuilder builder = new AlertBuilder("Password Berhasil direset", "Success", LoginActivity.this);
                        builder.show();
                        break;
                    case 1:
                        progressDialog.dismiss();
                        AlertBuilder builder2 = new AlertBuilder("Password gagal direset", "Error", LoginActivity.this);
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

    public void login(Map map) {
        progressDialog.show();
        String Url = Database.getUrl()+"/login";
        JsonObjectRequest RequestLogin = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progressDialog.dismiss();
                            progressDialog.setMessage("Menerima data ...");
                            progressDialog.show();
                            Integer errors = response.getInt("msg");
                            Log.d("LOGS", "onResponse: "+ errors);
                            switch (errors) {
                                case 0 :
                                    JSONObject obj = response.getJSONObject("data");
//                                    users.setId(obj.getInt("id"));
//                                    users.setEmail(obj.getString("email"));
//                                    users.setNama(obj.getString("nama"));
//                                    users.setTelepon(obj.getString("telepon"));
//                                    users.setRole_id(obj.getInt("role_id"));
//                                    users.setPosyandu_id(obj.getInt("posyandu_id"));
//                                    users.setPhoto(obj.getString("photo"));
//                                    users.setAlamat(obj.getString("alamat"));
//                                    users.setCreator_id(obj.getInt("creator_id"));
//                                    users.setActive(obj.getInt("active"));
//                                    users.setToken(obj.getString("token"));
//                                    users.setPosyandu(obj.getString("posyandu"));
//                                    users.setFirebase_token(obj.getString("firebase_token"));
//                                    users.setVerified(obj.getInt("verified"));
//                                    users.setVerification_code(obj.getString("verification_code"));
//                                    session();
//                                    moveActivity();
//                                    progressDialog.dismiss();
//                                    break;
                                    if(obj.getInt("verified") == 1) {
                                        users.setId(obj.getInt("id"));
                                        users.setEmail(obj.getString("email"));
                                        users.setNama(obj.getString("nama"));
                                        users.setTelepon(obj.getString("telepon"));
                                        users.setRole_id(obj.getInt("role_id"));
                                        users.setPosyandu_id(obj.getInt("posyandu_id"));
                                        users.setPhoto(obj.getString("photo"));
                                        users.setAlamat(obj.getString("alamat"));
                                        users.setCreator_id(obj.getInt("creator_id"));
                                        users.setActive(obj.getInt("active"));
                                        users.setToken(obj.getString("token"));
                                        users.setPosyandu(obj.getString("posyandu"));
                                        users.setFirebase_token(obj.getString("firebase_token"));
                                        users.setVerified(obj.getInt("verified"));
                                        users.setVerification_code(obj.getString("verification_code"));
                                        session();
                                        moveActivity();
                                        progressDialog.dismiss();
                                        break;
                                    }else{
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("Peringatan")
                                                .setMessage("Anda belum melakukan verifikasi, mohon periksa email anda.")
                                                .setNegativeButton(android.R.string.no, null)
                                                .setPositiveButton("Kirim Ulang", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        resendVerificationEmail(Username.getEditText().getText().toString());
                                                    }
                                                })
                                                .show();
                                        progressDialog.dismiss();
                                        break;
                                    }
                                case 1 :
                                    progressDialog.dismiss();
                                    AlertBuilder builder1 = new AlertBuilder("Password Salah", "Error", LoginActivity.this);
                                    builder1.show();
                                    break;
                                case 2 :
                                    progressDialog.dismiss();
                                    AlertBuilder builder2 = new AlertBuilder("Email Tidak Aktif", "Error", LoginActivity.this);
                                    builder2.show();
                                    break;
                                case 3:
                                    progressDialog.dismiss();
                                    AlertBuilder builder3 = new AlertBuilder("Email Salah", "Error", LoginActivity.this);
                                    builder3.show();
                                    break;
                            }
                        }
                        catch (JSONException e) {
                            e.getStackTrace();
                            progressDialog.dismiss();
                            AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing Data", "Error", LoginActivity.this);
                            builder.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.getStackTrace();
                        progressDialog.dismiss();
                        AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi", "Error", LoginActivity.this);
                        builder.show();
                    }
        });
        RequestLogin.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Controller.getmInstance().addToRequestQueue(RequestLogin);
    }

    private void resendVerificationEmail(String email) {
        String Url = Database.getUrl()+"/admin/resendemail";
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        JsonObjectRequest resendVerification = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("LOGS", "onResponse: " + response);
                Toast.makeText(LoginActivity.this, "Verifikasi Email Terkirim", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOGS", "onErrorResponse: ", error );
                Toast.makeText(LoginActivity.this, "Terjadi kesalahan dalam pengiriman ulang", Toast.LENGTH_SHORT).show();
            }
        });
        resendVerification.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Controller.getmInstance().addToRequestQueue(resendVerification);
    }

    public void session() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", users.getId());
        editor.putInt("role_id", users.getRole_id());
        editor.putInt("posyandu_id", users.getPosyandu_id());
        editor.putString("posyandu", users.getPosyandu());
        editor.putString("email", users.getEmail());
        editor.putString("nama", users.getNama());
        editor.putString("alamat", users.getAlamat());
        editor.putString("telepon", users.getTelepon());
        editor.putString("token", users.getToken());
        editor.putInt("creator_id", users.getCreator_id());
        editor.putString("firebase_token", users.getFirebase_token());
        editor.putInt("active", users.getActive());
        editor.putString("photo", users.getPhoto());
        editor.putBoolean("sign-in", TRUE);
        editor.apply();
    }

    public boolean checksessions() {
        ProgressDialog progressDialog1 = new ProgressDialog(LoginActivity.this);
        progressDialog1.setMessage("Loading ...");
        progressDialog1.setTitle("Check Sesi");
        progressDialog1.show();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        Boolean in = sharedPreferences.getBoolean("sign-in", false);
        if(!in) {
            progressDialog1.dismiss();
            return false;
        }
        else {
            users.setId(sharedPreferences.getInt("id", 0));
            users.setNama(sharedPreferences.getString("nama", "0"));
            users.setFirebase_token(sharedPreferences.getString("firebase_token", "0"));
            users.setRole_id(sharedPreferences.getInt("role_id", 0));
            users.setPosyandu_id(sharedPreferences.getInt("posyandu_id", 0));
            users.setEmail(sharedPreferences.getString("email", "0"));
            users.setToken(sharedPreferences.getString("alamat", "0"));
            users.setTelepon(sharedPreferences.getString("telepon","0"));
            users.setActive(sharedPreferences.getInt("active", 0));
            users.setPhoto(sharedPreferences.getString("photo", "0"));
            users.setAlamat(sharedPreferences.getString("alamat", "0"));
            users.setPosyandu(sharedPreferences.getString("posyandu", "0"));
            users.setCreator_id(sharedPreferences.getInt("creator_id", 0));
            progressDialog1.dismiss();
            return true;
        }


    }

    public void moveActivity() {
        Integer role_id = users.getRole_id();
        Intent intent = null;
        switch (role_id) {
            case 1:
                Toast.makeText(LoginActivity.this, "Login Sebagai Admin", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(LoginActivity.this, "Login Sebagai Ketua", Toast.LENGTH_SHORT).show();
                intent = new Intent(LoginActivity.this, KetuaActivity.class);
                break;
            case 3:
                Toast.makeText(LoginActivity.this, "Login Sebagai Anggota", Toast.LENGTH_SHORT).show();
                intent = new Intent(LoginActivity.this, AnggotaActivity.class);
                break;
            case 4:
                Toast.makeText(LoginActivity.this, "Login Sebagai Ibu Balita", Toast.LENGTH_SHORT).show();
                intent = new Intent(LoginActivity.this, IbuActivity.class);
                break;
        }
        intent.putExtra("user", users);
        startActivity(intent);
        finish();
    }
    private void dialogCheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.CAMERA}, 50);
            Toast.makeText(this, "Mohon Aktifkan Permission Camera Untuk Fitur QR Code", Toast.LENGTH_LONG).show();
        }
    }
}
