package com.mposyandu.mposyandu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.adapter.ListIbuBalitaAdapter;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListIbuBalita extends Fragment {
    private List<BalitaModel> balita = new ArrayList<>();
    private RecyclerView rv;
    private UserModel user;
    private SwipeRefreshLayout sw;
    private ListIbuBalitaAdapter adapter;
    private LinearLayout empty;
    private LinearLayout load;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        balita.clear();
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        View view = inflater.inflate(R.layout.list_balita, container, false);
        findViewById(view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        request();
        adapter = new ListIbuBalitaAdapter(balita);
        adapter.setOnItemClickListener(new ListIbuBalitaAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT) .show();
                Bundle bundle = new Bundle();
                bundle.putParcelable("balita", adapter.getItem(position));
                bundle.putParcelable("user", user);
                Fragment fragment = new HomeIbu();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.content_ibu, fragment, "Second")
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });

        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
            }
        });

        if(balita.isEmpty())
        {
            rv.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
            load.setVisibility(View.VISIBLE);
        }

        rv.setAdapter(adapter);
        return view;
    }

    public void findViewById(View view) {
        rv = view.findViewById(R.id.list_balita);
        empty = view.findViewById(R.id.list_balita_empty);
        load = view.findViewById(R.id.list_balita_loading);
        sw = view.findViewById(R.id.swipe_list_balita);
    }

    public void request() {
        Map<String, Integer> params = new HashMap<>();
        params.put("ibu_id", user.getId());
        params.put("active", 1);
        String Url = Database.getUrl() + "/user/" + user.getId() + "/posyandu/"+user.getPosyandu_id()+"/baby/motherfetch";
        JsonObjectRequest RequestBalitaIbu = new JsonObjectRequest(Request.Method.POST, Url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    switch (error) {
                        case 0 :
                            balita.clear();
                            JSONArray obj_arr = response.getJSONArray("data");
                            for (int i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                BalitaModel balitaList = new BalitaModel();
                                balitaList.setId(obj.getInt("id"));
                                balitaList.setNama(obj.getString("nama"));
                                balitaList.setGender(obj.getString("gender"));
                                balitaList.setTanggal_lahir(obj.getString("tanggal_lahir"));
                                balitaList.setPosyandu(obj.getString("posyandu"));
                                balitaList.setPosyandu_id(obj.getInt("posyandu_id"));
                                balitaList.setAyah(obj.getString("ayah"));
                                balitaList.setIbu(obj.getString("ibu"));
                                balitaList.setIbu_id(obj.getInt("ibu_id"));
                                balitaList.setActive(obj.getInt("active"));
                                balitaList.setPetugas(obj.getString("petugas"));
                                balitaList.setCreator_id(obj.getInt("creator_id"));
                                balitaList.setPhoto(obj.getString("photo"));
                                balitaList.setId_qrcode(obj.getString("id_qrcode"));
                                balita.add(balitaList);
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Controller.getmInstance().addToRequestQueue(RequestBalitaIbu);
    }
}
