package com.mposyandu.mposyandu.fragment;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.adapter.ListAnggotaAdapter;
import com.mposyandu.mposyandu.adapter.ListAnggotaAdapterRetrofit;
import com.mposyandu.mposyandu.data.AnggotaModel;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.retrofitModel.UserModelPost;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.data.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListChooseIbu extends Fragment {
    private ListAnggotaAdapterRetrofit adapter;
    private UserModel user;
    private RecyclerView rv;
    private SwipeRefreshLayout sw;
    private LinearLayout empty;
    private LinearLayout load;
    private FloatingActionButton fab;
    private List<UserModelPost> anggota = new ArrayList<>();
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.ketua, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem item2 = menu.findItem(R.id.action_export);
        item2.setVisible(false);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Cari Anggota....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                adapter.getFilter().filter(newText);

                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        View view = inflater.inflate(R.layout.list_anggota, container, false);
        fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        findViewById(view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        rv.setLayoutManager(layoutManager);
        request();
        adapter = new ListAnggotaAdapterRetrofit(anggota);
        adapter.setOnItemClickListener(new ListAnggotaAdapterRetrofit.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d("LOGS", "onItemClick: "+ adapter.getItem(position).toString());
                Bundle bundle = new Bundle();
                bundle.putParcelable("posyandu", adapter.getItem(position));
                bundle.putParcelable("user", user);
                if(getActivity() != null){
                    bundle.putString("options", "CREATE_BABY_FROM_EXISTING_MOTHER");
                }
                Fragment fragment = new TambahBalita();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_ketua, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
        rv.setAdapter(adapter);
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
            }
        });
        if(anggota.isEmpty())
        {
            rv.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
            load.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void findViewById(View view) {
        sw = view.findViewById(R.id.swipe_list_anggota);
        rv = view.findViewById(R.id.list_anggota);
        empty = view.findViewById(R.id.list_anggota_empty);
        load = view.findViewById(R.id.list_anggota_loading);
    }

    public void request() {
        Map<String, Integer> params = new HashMap<>();
        params.put("posyandu_id", user.getPosyandu_id());
        params.put("role_id", 4);
        String Url = Database.getUrl()+"/user/"+user.getId()+"/fetch";
        JsonObjectRequest RequestIbu = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0:
                            anggota.clear();
                            JSONArray obj_arr = response.getJSONArray("data");
                            for (int i = 0; i < obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                Log.d("AAA", obj.getString("id"));
                                if(containsId(anggota, obj.getInt("id"))){
                                    Log.d("LOGS", "onResponse: has same id "+ obj.getInt("id"));
                                }else{
                                    Log.d("LOGS", "onResponse: no mathes id");
                                    UserModelPost anggotaList = new UserModelPost();
                                    anggotaList.setId(obj.getInt("id"));
                                    anggotaList.setEmail(obj.getString("email"));
                                    anggotaList.setPassword(obj.getString("password"));
                                    anggotaList.setNama(obj.getString("nama"));
                                    anggotaList.setTelepon(obj.getString("telepon"));
                                    anggotaList.setRoleId(obj.getString("role_id"));
                                    anggotaList.setPosyanduId(obj.getString("posyandu_id"));
                                    anggotaList.setPhoto(obj.getString("photo"));
                                    anggotaList.setAlamat(obj.getString("alamat"));
                                    anggotaList.setActive(obj.getInt("active"));
                                    anggotaList.setToken(obj.getString("token"));
                                    anggotaList.setFirebaseToken(obj.getString("firebase_token"));
                                    anggota.add(anggotaList);
                                }

                            }
                            rv.setVisibility(View.VISIBLE);
                            empty.setVisibility(View.GONE);
                            load.setVisibility(View.GONE);
                            break;
                        case 1 :
                            rv.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                            load.setVisibility(View.GONE);
                            break;
                    }
                    adapter.notifyDataSetChanged();
                    sw.setRefreshing(false);
                }
                catch (JSONException e) {
                    e.getStackTrace();
                }
            }
        }, error -> {
            error.getStackTrace();
            AlertBuilder builder = new AlertBuilder("Terdapat Error Saat Mengambil Data", "Error", getActivity());
            builder.show();
        });
        Controller.getmInstance().addToRequestQueue(RequestIbu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rv.setAdapter(null);
        adapter.notifyDataSetChanged();
        fab.show();
    }

    public boolean containsId(final List<UserModelPost> list, final Integer id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().anyMatch(o -> o.getId().equals(id));
        }else{
            return false;
        }
    }
}
