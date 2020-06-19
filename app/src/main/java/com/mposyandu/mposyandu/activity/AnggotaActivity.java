package com.mposyandu.mposyandu.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.fragment.About;
import com.mposyandu.mposyandu.fragment.Guide;
import com.mposyandu.mposyandu.fragment.HomeAnggota;
import com.mposyandu.mposyandu.fragment.ListBalita;
import com.mposyandu.mposyandu.fragment.ListIbu;
import com.mposyandu.mposyandu.fragment.ProfileUser;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.fragment.Reminder;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.Logout;
import com.squareup.picasso.Picasso;

public class AnggotaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private UserModel user;
    private View headerview;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private static final int TIME_INTERVAL = 3000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anggota);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        setTitle("Kader");
        toolbar.setSubtitle("Posyandu "+user.getPosyandu());
        dialogCheckPermission();

        subscribeTopic();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        headerview = navigationView.getHeaderView(0);
        TextView nama = headerview.findViewById(R.id.nav_header_anggota_nama);
        TextView email = headerview.findViewById(R.id.nav_header_anggota_email);
        ImageView thumbs = headerview.findViewById(R.id.nav_header_anggota_thumbs);

        nama.setText(user.getNama());
        email.setText(user.getEmail());
        Picasso.get().load(Database.getUrl()+"/"+user.getPhoto()).transform(new CircleTransform()).into(thumbs);

        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new HomeAnggota();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.content_ketua, fragment, "Home")
                .commit();
    }
    private void dialogCheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AnggotaActivity.this, new String[]{android.Manifest.permission.CAMERA}, 50);
            Toast.makeText(this, "Aktifkan Permission Camera", Toast.LENGTH_SHORT).show();
        }
    }


    public void findId(Integer id) {
        switch (id) {
            case 0:
                navigationView.setCheckedItem(R.id.nav_profile);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
            case 1:
                navigationView.setCheckedItem(R.id.nav_balita);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
            case 2:
                navigationView.setCheckedItem(R.id.nav_ibu);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
            case 3:
                navigationView.setCheckedItem(R.id.nav_cal);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
        }
    }

    public void subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("posyandu%"+String.valueOf(user.getPosyandu_id()));
        FirebaseMessaging.getInstance().subscribeToTopic("anggota%"+String.valueOf(user.getPosyandu_id()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
            {
                super.onBackPressed();
                return;
            } else {
                Log.d("BackStack", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
                if(getSupportFragmentManager().getBackStackEntryCount() > 0) {

                    getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getApplicationContext(), "Tekan back 2 kali untuk keluar", Toast.LENGTH_SHORT).show();
                }
            }

            mBackPressed = System.currentTimeMillis();
        }
    }

    public void hiddentabs() {
        tabLayout = findViewById(R.id.tab_ketua);
        tabLayout.setVisibility(View.GONE);
        toolbar.setSubtitle(null);
        toolbar.setTitle("Posyandu "+user.getPosyandu());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.anggota, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Logout logout = new Logout(getApplicationContext());
            Intent intent = logout.out();
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.actionQR) {
            Intent intent = new Intent(getApplicationContext(), scanQRPeserta.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        hiddentabs();
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putString("from", "1");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_balita:
                fragment = new ListBalita();
                break;
            case R.id.nav_ibu:
                fragment = new ListIbu();
                break;
            case R.id.nav_profile:
                fragment = new ProfileUser();
                break;
            case R.id.nav_cal:
                fragment = new Reminder();
                break;
            case R.id.nav_guide:
                fragment = new Guide();
                break;
            case R.id.nav_about_app:
                fragment = new About();
                break;
            case R.id.nav_logout:
                Logout logout = new Logout(getApplicationContext());
                Intent intent = logout.out();
                startActivity(intent);
                finish();
                break;
        }

        if(fragment != null){
            (fragment).setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_ketua, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("posyandu%"+user.getPosyandu_id());
        FirebaseMessaging.getInstance().unsubscribeFromTopic("anggota%"+String.valueOf(user.getPosyandu_id()));
        super.onDestroy();

    }
}
