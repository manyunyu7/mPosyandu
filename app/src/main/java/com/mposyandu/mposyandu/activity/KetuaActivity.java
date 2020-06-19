package com.mposyandu.mposyandu.activity;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.fragment.About;
import com.mposyandu.mposyandu.fragment.Guide;
import com.mposyandu.mposyandu.fragment.HomeKetua;
import com.mposyandu.mposyandu.fragment.ListAnggota;
import com.mposyandu.mposyandu.fragment.ListBalita;
import com.mposyandu.mposyandu.fragment.ListIbu;
import com.mposyandu.mposyandu.fragment.ProfileUser;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.fragment.Reminder;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.ConnectivityReceiver;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.tools.Logout;
import com.mposyandu.mposyandu.data.UserModel;
import com.squareup.picasso.Picasso;

public class KetuaActivity extends AppCompatActivity
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
        setContentView(R.layout.activity_ketua);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        FirebaseMessaging.getInstance().subscribeToTopic("posyandu%"+String.valueOf(user.getPosyandu_id()));
        FirebaseMessaging.getInstance().subscribeToTopic("anggota%"+String.valueOf(user.getPosyandu_id()));
        checkConnection();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Posyandu "+user.getPosyandu());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        headerview = navigationView.getHeaderView(0);
        TextView nama = headerview.findViewById(R.id.nav_header_ketua_nama);
        TextView email = headerview.findViewById(R.id.nav_header_ketua_email);
        ImageView thumbs = headerview.findViewById(R.id.nav_header_ketua_thumbs);

        nama.setText(user.getNama());
        email.setText(user.getEmail());

        Picasso.get().load(Database.getUrl()+"/"+user.getPhoto()).transform(new CircleTransform()).into(thumbs);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new HomeKetua();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.content_ketua, fragment, "Home")
                .commit();
    }

    public void findId(Integer id) {
        switch (id) {
            case 0 :
                navigationView.setCheckedItem(R.id.nav_profile);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
            case 1 :
                navigationView.setCheckedItem(R.id.nav_anggota);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
            case 2 :
                navigationView.setCheckedItem(R.id.nav_balita);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
            case 3 :
                navigationView.setCheckedItem(R.id.nav_ibu);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
            case 4 :
                navigationView.setCheckedItem(R.id.nav_cal);
                onNavigationItemSelected(navigationView.getMenu().getItem(id));
                break;
        }
    }

    public void showSnack(boolean isConnected) {
        if (!isConnected) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.fab), "Internet Tidak Terdeteksi", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }

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

                    super.onBackPressed();
                } else {
                    Toast.makeText(getApplicationContext(), "Tekan back 2 kali untuk keluar", Toast.LENGTH_SHORT).show();
                }
            }

            mBackPressed = System.currentTimeMillis();
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ketua, menu);
        MenuItem item = menu.findItem(R.id.action_export);
        item.setVisible(false);
        return true;
    }

    public void hiddentabs() {
        tabLayout = findViewById(R.id.tab_ketua);
        tabLayout.setVisibility(View.GONE);
        toolbar.setSubtitle("Posyandu "+user.getPosyandu());
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        hiddentabs();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putString("from", "1");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        String Tags = null;
        switch (id) {
            case R.id.nav_balita:
                fragment = new ListBalita();
                Tags = "Balita";
                break;
            case R.id.nav_anggota:
                fragment = new ListAnggota();
                Tags = "Anggota";

                break;
            case R.id.nav_ibu:
                fragment = new ListIbu();
                Tags = "Ibu";

                break;
            case R.id.nav_profile:
                fragment = new ProfileUser();
                Tags = "Profile";
                break;
            case R.id.nav_cal:
                fragment = new Reminder();
                Tags = "Calender";
                break;
            case R.id.nav_guide:
                fragment = new Guide();
                Tags = "Guide";
                break;
            case R.id.nav_about_app:
                fragment = new About();
                Tags = "Tentang Aplikasi";
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
                    .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
                    .replace(R.id.content_ketua, fragment, Tags)
                    .addToBackStack(null)
                    .commit();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("posyandu%"+String.valueOf(user.getPosyandu_id()));
        FirebaseMessaging.getInstance().unsubscribeFromTopic("anggota%"+String.valueOf(user.getPosyandu_id()));
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        Controller.getmInstance().setConnectivityListener(new ConnectivityReceiver.ConnectivityReceiverListener() {
            @Override
            public void onNetworkConnectionChanged(boolean isConnected) {
              showSnack(isConnected);
            }
        });
    }
}
