package com.mposyandu.mposyandu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;

import com.mposyandu.mposyandu.activity.AnggotaActivity;
import com.mposyandu.mposyandu.activity.scanQRPeserta;
import com.mposyandu.mposyandu.data.AnggotaModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.DialogBuilder;
import com.squareup.picasso.Picasso;

public class ProfileUser extends Fragment {
    private UserModel user;
    private DialogBuilder dialogBuilder;
    private RelativeLayout rl;
    private ImageView Cam,  DisplayPicture;
    private String filenameimage, encodedimage;
    private TextView Title, Email, Nama, Alamat, Telepon, newUser, newPass;
    private LinearLayout toolbar;
    private View LayoutToolbar;
    private Button Edit;
    private FloatingActionButton fab;
    private AnggotaModel anggota;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        menu.findItem(R.id.actionQR).setVisible(true);
        menu.findItem(R.id.actionQR).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent a = new Intent(getContext(), scanQRPeserta.class);
                startActivity(a);
                return true;
            }
        });
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
        user = bundle.getParcelable("user");
        String from  = bundle.getString("from");

        View view = inflater.inflate(R.layout.profile_user, container, false);
        toolbar = getActivity().findViewById(R.id.toolbar_layout);


        LayoutToolbar = getLayoutInflater().inflate(R.layout.profile_user_image, null);
        toolbar.addView(LayoutToolbar);


        DisplayPicture = LayoutToolbar.findViewById(R.id.form_user_thumb);
        Cam = LayoutToolbar.findViewById(R.id.form_user_cam);

        rl = LayoutToolbar.findViewById(R.id.form_user_layoutcam);
        findViewById(view);
        String photo = null;
        if (from.equals("1")) {
            anggota = new AnggotaModel();
            anggota.setNama(user.getNama());
            anggota.setPassword("0");
            anggota.setEmail(user.getEmail());
            anggota.setToken(user.getToken());
            anggota.setFirebase_token(user.getFirebase_token());
            anggota.setPosyandu_id(user.getPosyandu_id());
            anggota.setId(user.getId());
            anggota.setRole_id(user.getRole_id());
            anggota.setCreator_id(user.getCreator_id());
            anggota.setAlamat(user.getAlamat());
            anggota.setActive(user.getActive());
            anggota.setPhoto(user.getPhoto());
            anggota.setTelepon(user.getTelepon());
            setText();
            photo = user.getPhoto();
        }
        else {
            anggota = bundle.getParcelable("posyandu");
            setTextd();
            photo = anggota.getPhoto();
        }
        rl.setVisibility(View.GONE);
        Picasso.get().load(Database.getUrl()+"/"+photo)
                .transform(new CircleTransform())
                .into(DisplayPicture);


        Title.setText("Profile Diri");
        fab = getActivity().findViewById(R.id.fab);


        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = new EditUser();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                bundle.putParcelable("posyandu", anggota);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_left, R.anim.exit_right, R.anim.enter_right, R.anim.ext_left)
                        .replace(R.id.content_ketua, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        fab.hide();
        return view;
    }


    public void findViewById(View view) {
        Email = view.findViewById(R.id.profile_user_email);
        Nama = view.findViewById(R.id.profile_user_nama);
        Alamat = view.findViewById(R.id.profile_user_alamat);
        Telepon = view.findViewById(R.id.profile_user_telepon);
        Title = view.findViewById(R.id.profile_user_title);
        Edit = view.findViewById(R.id.profile_user_edit);
    }



    public void setText() {

        Nama.setText(user.getNama());
        Email.setText(user.getEmail());
        Alamat.setText(user.getAlamat());
        Telepon.setText(user.getTelepon());

    }

    public void setTextd() {

        Nama.setText(anggota.getNama());
        Email.setText(anggota.getEmail());
        Alamat.setText(anggota.getAlamat());
        Telepon.setText(anggota.getTelepon());
    }

    @Override
    public void onResume() {
        super.onResume();

        fab.hide();

    }

    @Override
    public void onDestroyView() {
        toolbar.removeView(LayoutToolbar);
        super.onDestroyView();

    }

    @Override
    public void onPause() {
        super.onPause();
        toolbar.removeView(LayoutToolbar);
    }
}
