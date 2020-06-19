package com.mposyandu.mposyandu.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.fragment.ListIbuBalita;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.Logout;

public class IbuActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private UserModel user;
    private static final int TIME_INTERVAL = 3000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    public void subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("posyandu%"+String.valueOf(user.getPosyandu_id()));
        FirebaseMessaging.getInstance().subscribeToTopic("ibu%"+String.valueOf(user.getId()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibu);
        toolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        subscribeTopic();

        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Posyandu "+user.getPosyandu());
        setTitle(user.getNama());

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new ListIbuBalita();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.content_ibu, fragment, "Home")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ketua, menu);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
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

    @Override
    protected void onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("ibu%"+String.valueOf(user.getId()));
        FirebaseMessaging.getInstance().unsubscribeFromTopic("posyandu%"+String.valueOf(user.getPosyandu_id()));
        super.onDestroy();

    }
}
