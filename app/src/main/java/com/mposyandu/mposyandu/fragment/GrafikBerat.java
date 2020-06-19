package com.mposyandu.mposyandu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.Controller;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.GrafikModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.AlertBuilder;
import com.mposyandu.mposyandu.tools.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mposyandu.mposyandu.Controller.TAG;

public class GrafikBerat extends Fragment {
    private WebView webView;
    private BalitaModel balita;
    private List<GrafikModel> grafik = new ArrayList<>();
    private UserModel user;
    private FloatingActionButton fab;
    private SwipeRefreshLayout sw;
    private static GrafikBerat instance = null;

    public static GrafikBerat getInstance() {
        return instance;
    }

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
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grafik_kms, container, false);
        Bundle bundle = getArguments();
        balita = bundle.getParcelable("balita");
        user = bundle.getParcelable("user");
        fab = getActivity().findViewById(R.id.fab);
        webView = view.findViewById(R.id.grafik_webview);
        sw = view.findViewById(R.id.swipe_kms);
        request();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
            }
        });
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void request() {
        Map<String, Integer> params = new HashMap<>();
        params.put("balita_id", balita.getId());
        params.put("empty", 0);
        String url = Database.getUrl()+"/user/"+user.getId()+"/posyandu/"+user.getPosyandu_id()+"/baby/"+
                balita.getId()+"/condition/fetch";
        Log.d(TAG, "request grafik berat : "+url+" with params " +new JSONObject(params));
        JsonObjectRequest RequestKMS = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Integer error = response.getInt("msg");
                    String js;
                    grafik.clear();
                    switch (error) {
                        case 0:
                            JSONArray obj_arr = response.getJSONArray("data");
                            int i;
                            Toast.makeText(getActivity(), "100%", Toast.LENGTH_SHORT).show();
                            for (i=0; i<obj_arr.length(); i++) {
                                JSONObject obj = obj_arr.getJSONObject(i);
                                GrafikModel gc = new GrafikModel();
                                if(obj.getDouble("berat") == 0.0 ||
                                        obj.getDouble("berat") == 0) {
                                    gc.setBerat(null);
                                }
                                else {
                                    gc.setBerat(obj.getDouble("berat"));
                                }
                                gc.setUsia(obj.getInt("usia"));
                                grafik.add(gc);
                            }
                            if(balita.getGender().equals("L")) {
                                 js = "<html><body><div id=\"container\"></div>" +
                                        "<script src=\"https://code.highcharts.com/highcharts.js\"></script>\n" +
                                        "<script src=\"https://code.highcharts.com/highcharts-more.js\"></script>\n" +
                                        "<script src=\"https://code.highcharts.com/modules/exporting.js\"></script>" +
                                        "<script>\n" +
                                        "    var data_red = [\n" +
                                        "            [0, 2.1, 5.0],\n" +
                                        "            [1, 2.9, 6.6],\n" +
                                        "            [2, 3.8, 8.0],\n" +
                                        "            [3, 4.4, 9.0],\n" +
                                        "            [4, 4.9, 9.7],\n" +
                                        "            [5, 5.3, 10.4],\n" +
                                        "            [6, 5.7, 10.9],\n" +
                                        "            [7, 5.9, 11.4],\n" +
                                        "            [8, 6.2, 11.9],\n" +
                                        "            [9, 6.4, 12.3],\n" +
                                        "            [10, 6.6, 12.7],\n" +
                                        "            [11, 6.8, 13.0],\n" +
                                        "            [12, 6.9, 13.3],\n" +
                                        "            [13, 7.1, 13.7],\n" +
                                        "            [14, 7.2, 14.0],\n" +
                                        "            [15, 7.4, 14.3],\n" +
                                        "            [16, 7.5, 14.6],\n" +
                                        "            [17, 7.7, 14.9],\n" +
                                        "            [18, 7.8, 15.3],\n" +
                                        "            [19, 8.0, 15.6],\n" +
                                        "            [20, 8.1, 15.9],\n" +
                                        "            [21, 8.2, 16.2],\n" +
                                        "            [22, 8.4, 16.5],\n" +
                                        "            [23, 8.5, 16.8],\n" +
                                        "            [24, 8.6, 17.1],\n" +
                                        "            [25, 8.8, 17.5],\n" +
                                        "            [26, 8.9, 17.8],\n" +
                                        "            [27, 9.0, 18.1],\n" +
                                        "            [28, 9.1, 18.4],\n" +
                                        "            [29, 9.2, 18.7],\n" +
                                        "            [30, 9.4, 19.0],\n" +
                                        "            [31, 9.5, 19.3],\n" +
                                        "            [32, 9.6, 19.6],\n" +
                                        "            [33, 9.7, 19.9],\n" +
                                        "            [34, 9.8, 20.2],\n" +
                                        "            [35, 9.9, 20.4],\n" +
                                        "            [36, 10.0, 20.7],\n" +
                                        "            [37, 10.1, 21.0],\n" +
                                        "            [38, 10.2, 21.3],\n" +
                                        "            [39, 10.3, 21.6],\n" +
                                        "            [40, 10.4, 21.9],\n" +
                                        "            [41, 10.5, 22.1],\n" +
                                        "            [42, 10.6, 22.4],\n" +
                                        "            [43, 10.7, 22.7],\n" +
                                        "            [44, 10.8, 23.0],\n" +
                                        "            [45, 10.9, 23.3],\n" +
                                        "            [46, 11.0, 23.6],\n" +
                                        "            [47, 11.1, 23.9],\n" +
                                        "            [48, 11.2, 24.2],\n" +
                                        "            [49, 11.3, 24.5],\n" +
                                        "            [50, 11.4, 24.8],\n" +
                                        "            [51, 11.5, 25.1],\n" +
                                        "            [52, 11.6, 25.4],\n" +
                                        "            [53, 11.7, 25.7],\n" +
                                        "            [54, 11.8, 26.0],\n" +
                                        "            [55, 11.9, 26.3],\n" +
                                        "            [56, 12.0, 26.6],\n" +
                                        "            [57, 12.1, 26.9],\n" +
                                        "            [58, 12.2, 27.2],\n" +
                                        "            [59, 12.3, 27.5],\n" +
                                        "            [60, 12.4, 27.8],\n" +
                                        "        ],\n" +
                                        "        data_orange = [\n" +
                                        "            [0, 2.5, 4.4],\n" +
                                        "            [1, 3.4, 5.8],\n" +
                                        "            [2, 4.3, 7.1],\n" +
                                        "            [3, 5.0, 8.0],\n" +
                                        "            [4, 5.6, 8.7],\n" +
                                        "            [5, 6.0, 9.3],\n" +
                                        "            [6, 6.4, 9.8],\n" +
                                        "            [7, 6.7, 10.3],\n" +
                                        "            [8, 6.9, 10.7],\n" +
                                        "            [9, 7.1, 11.0],\n" +
                                        "            [10, 7.4, 11.4],\n" +
                                        "            [11, 7.6, 11.7],\n" +
                                        "            [12, 7.7, 12.0],\n" +
                                        "            [13, 7.9, 12.3],\n" +
                                        "            [14, 8.1, 12.6],\n" +
                                        "            [15, 8.3, 12.8],\n" +
                                        "            [16, 8.4, 13.1],\n" +
                                        "            [17, 8.6, 13.4],\n" +
                                        "            [18, 8.8, 13.7],\n" +
                                        "            [19, 8.9, 13.9],\n" +
                                        "            [20, 9.1, 14.2],\n" +
                                        "            [21, 9.2, 14.5],\n" +
                                        "            [22, 9.4, 14.7],\n" +
                                        "            [23, 9.5, 15.0],\n" +
                                        "            [24, 9.7, 15.3],\n" +
                                        "            [25, 9.8, 15.5],\n" +
                                        "            [26, 10.0, 15.8],\n" +
                                        "            [27, 10.1, 16.1],\n" +
                                        "            [28, 10.2, 16.3],\n" +
                                        "            [29, 10.4, 16.6],\n" +
                                        "            [30, 10.5, 16.9],\n" +
                                        "            [31, 10.7, 17.1],\n" +
                                        "            [32, 10.8, 17.4],\n" +
                                        "            [33, 10.9, 17.6],\n" +
                                        "            [34, 11.0, 17.8],\n" +
                                        "            [35, 11.2, 18.1],\n" +
                                        "            [36, 11.3, 18.3],\n" +
                                        "            [37, 11.4, 18.6],\n" +
                                        "            [38, 11.5, 18.8],\n" +
                                        "            [39, 11.6, 19.0],\n" +
                                        "            [40, 11.8, 19.3],\n" +
                                        "            [41, 11.9, 19.5],\n" +
                                        "            [42, 12.0, 19.7],\n" +
                                        "            [43, 12.1, 20.0],\n" +
                                        "            [44, 12.2, 20.2],\n" +
                                        "            [45, 12.4, 20.5],\n" +
                                        "            [46, 12.5, 20.7],\n" +
                                        "            [47, 12.6, 20.9],\n" +
                                        "            [48, 12.7, 21.2],\n" +
                                        "            [49, 12.8, 21.4],\n" +
                                        "            [50, 12.9, 21.7],\n" +
                                        "            [51, 13.1, 21.9],\n" +
                                        "            [52, 13.2, 22.2],\n" +
                                        "            [53, 13.3, 22.4],\n" +
                                        "            [54, 13.4, 22.7],\n" +
                                        "            [55, 13.5, 22.9],\n" +
                                        "            [56, 13.6, 23.2],\n" +
                                        "            [57, 13.7, 23.4],\n" +
                                        "            [58, 13.8, 23.7],\n" +
                                        "            [59, 14.0, 23.9],\n" +
                                        "            [60, 14.1, 24.2],\n" +
                                        "        ],\n" +
                                        "        data_yellow = [\n" +
                                        "            [0, 2.9, 3.9],\n" +
                                        "            [1, 3.9, 5.1],\n" +
                                        "            [2, 4.9, 6.3],\n" +
                                        "            [3, 5.7, 7.2],\n" +
                                        "            [4, 6.2, 7.8],\n" +
                                        "            [5, 6.7, 8.4],\n" +
                                        "            [6, 7.1, 8.8],\n" +
                                        "            [7, 7.4, 9.2],\n" +
                                        "            [8, 7.7, 9.6],\n" +
                                        "            [9, 8.0, 9.9],\n" +
                                        "            [10, 8.2, 10.2],\n" +
                                        "            [11, 8.4, 10.5],\n" +
                                        "            [12, 8.6, 10.8],\n" +
                                        "            [13, 8.8, 11.0],\n" +
                                        "            [14, 9.0, 11.3],\n" +
                                        "            [15, 9.2, 11.5],\n" +
                                        "            [16, 9.4, 11.7],\n" +
                                        "            [17, 9.6, 12.0],\n" +
                                        "            [18, 9.8, 12.2],\n" +
                                        "            [19, 10.0, 12.5],\n" +
                                        "            [20, 10.1, 12.7],\n" +
                                        "            [21, 10.3, 12.9],\n" +
                                        "            [22, 10.5, 13.2],\n" +
                                        "            [23, 10.7, 13.4],\n" +
                                        "            [24, 10.8, 13.6],\n" +
                                        "            [25, 11.0, 13.9],\n" +
                                        "            [26, 11.2, 14.1],\n" +
                                        "            [27, 11.3, 14.3],\n" +
                                        "            [28, 11.5, 14.5],\n" +
                                        "            [29, 11.7, 14.8],\n" +
                                        "            [30, 11.8, 15.0],\n" +
                                        "            [31, 12.0, 15.2],\n" +
                                        "            [32, 12.1, 15.4],\n" +
                                        "            [33, 12.3, 15.6],\n" +
                                        "            [34, 12.4, 15.8],\n" +
                                        "            [35, 12.6, 16.0],\n" +
                                        "            [36, 12.7, 16.2],\n" +
                                        "            [37, 12.9, 16.4],\n" +
                                        "            [38, 13.0, 16.6],\n" +
                                        "            [39, 13.1, 16.8],\n" +
                                        "            [40, 13.3, 17.0],\n" +
                                        "            [41, 13.4, 17.2],\n" +
                                        "            [42, 13.6, 17.4],\n" +
                                        "            [43, 13.7, 17.6],\n" +
                                        "            [44, 13.8, 17.8],\n" +
                                        "            [45, 14.0, 18.0],\n" +
                                        "            [46, 14.1, 18.2],\n" +
                                        "            [47, 14.3, 18.4],\n" +
                                        "            [48, 14.4, 18.6],\n" +
                                        "            [49, 14.5, 18.8],\n" +
                                        "            [50, 14.7, 19.0],\n" +
                                        "            [51, 14.8, 19.2],\n" +
                                        "            [52, 15.0, 19.4],\n" +
                                        "            [53, 15.1, 19.6],\n" +
                                        "            [54, 15.2, 19.8],\n" +
                                        "            [55, 15.4, 20.0],\n" +
                                        "            [56, 15.5, 20.2],\n" +
                                        "            [57, 15.6, 20.4],\n" +
                                        "            [58, 15.8, 20.6],\n" +
                                        "            [59, 15.9, 20.8],\n" +
                                        "            [60, 16.0, 21.0],\n" +
                                        "        ]\n" +
                                        "\n" +
                                        "\n" +
                                        "    var data = \n" +
                                        grafik.toString()+
                                        "    ;\n" +
                                        "\n" +
                                        "\n" +
                                        "    var chart = Highcharts.chart('container', {\n" +
                                        "\n" +
                                        "        title: {\n" +
                                        "            text: 'Berat Badan menurut Umur (L)'\n" +
                                        "        },\n" +
                                        "\n" +
                                        "        xAxis: {\n" +
                                        "            type: 'linear',\n" +
                                        "            minRange: 60,\n" +
                                        "            title: {\n" +
                                        "                text: \"Usia\"\n" +
                                        "            },\n" +
                                        "            allowDecimals: false\n" +
                                        "        },\n" +
                                        "\n" +
                                        "        yAxis: {\n" +
                                        "            min: 0,\n" +
                                        "            max: 30,\n" +
                                        "            title: {\n" +
                                        "                text: \"Kilogram\"\n" +
                                        "            }\n" +
                                        "        },\n" +
                                        "\n" +
                                        "        tooltip: {\n" +
                                        "            valueSuffix: ' Kg'\n" +
                                        "        },\n" +
                                        "        series: [{\n" +
                                        "                name: 'Berat Badan',\n" +
                                        "                data: data,\n" +
                                        "                zIndex: 5,\n" +
                                        "                color: 'black',\n" +
                                        "                connectNulls: true,\n" +
                                        "            },\n" +
                                        "\n" +
                                        "            {\n" +
                                        "                name: 'Gizi Buruk',\n" +
                                        "                data: data_red,\n" +
                                        "                type: 'arearange',\n" +
                                        "                lineWidth: 0,\n" +
                                        "                color: '#ffff00',\n" +
                                        "                fillOpacity: 0.9,\n" +
                                        "                zIndex: 0,\n" +
                                        "                marker: {\n" +
                                        "                    enabled: false\n" +
                                        "                },\n" +
                                        "                enableMouseTracking: false\n" +
                                        "            },\n" +
                                        "\n" +
                                        "            {\n" +
                                        "                name: 'Gizi Kurang',\n" +
                                        "                data: data_orange,\n" +
                                        "                type: 'arearange',\n" +
                                        "                lineWidth: 0,\n" +
                                        "                color: 'lime',\n" +
                                        "                fillOpacity: 0.3,\n" +
                                        "                zIndex: 1,\n" +
                                        "                marker: {\n" +
                                        "                    enabled: false\n" +
                                        "                },\n" +
                                        "                enableMouseTracking: false\n" +
                                        "            },\n" +
                                        "\n" +
                                        "            {\n" +
                                        "                name: 'Gizi Baik',\n" +
                                        "                data: data_yellow,\n" +
                                        "                type: 'arearange',\n" +
                                        "                lineWidth: 0,\n" +
                                        "                color: 'green',\n" +
                                        "                fillOpacity: 0.3,\n" +
                                        "                zIndex: 2,\n" +
                                        "                marker: {\n" +
                                        "                    enabled: false\n" +
                                        "                },\n" +
                                        "                enableMouseTracking: false\n" +
                                        "            }\n" +
                                        "        ]\n" +
                                        "    });\n" +
                                        "</script>\n"+
                                        "</body></html>";
                            }
                            else {
                                 js = "<html><body><div id=\"container\"></div>" +
                                        "<script src=\"https://code.highcharts.com/highcharts.js\"></script>\n" +
                                        "<script src=\"https://code.highcharts.com/highcharts-more.js\"></script>\n" +
                                        "<script src=\"https://code.highcharts.com/modules/exporting.js\"></script>" +
                                        "<script>\n" +
                                        "<script src=\"/js/highcharts.js\"></script>\n" +
                                         "<script src=\"/js/highcharts-more.js\"></script>\n" +
                                         "<script src=\"/js/exporting.js\"></script>\n" +
                                         "<script>\n" +
                                         "    var data_red = [\n" +
                                         "            [0, 2.0, 4.8],\n" +
                                         "            [1, 2.7, 6.2],\n" +
                                         "            [2, 3.4, 7.5],\n" +
                                         "            [3, 4.0, 8.5],\n" +
                                         "            [4, 4.4, 9.3],\n" +
                                         "            [5, 4.8, 10.0],\n" +
                                         "            [6, 5.1, 10.6],\n" +
                                         "            [7, 5.3, 11.1],\n" +
                                         "            [8, 5.6, 11.6],\n" +
                                         "            [9, 5.8, 12.0],\n" +
                                         "            [10, 5.9, 12.4],\n" +
                                         "            [11, 6.1, 12.8],\n" +
                                         "            [12, 6.3, 13.1],\n" +
                                         "            [13, 6.4, 13.5],\n" +
                                         "            [14, 6.6, 13.8],\n" +
                                         "            [15, 6.7, 14.1],\n" +
                                         "            [16, 6.9, 14.5],\n" +
                                         "            [17, 7.0, 14.8],\n" +
                                         "            [18, 7.2, 15.1],\n" +
                                         "            [19, 7.3, 15.4],\n" +
                                         "            [20, 7.5, 15.7],\n" +
                                         "            [21, 7.6, 16.0],\n" +
                                         "            [22, 7.8, 16.4],\n" +
                                         "            [23, 7.9, 16.7],\n" +
                                         "            [24, 8.1, 17.0],\n" +
                                         "            [25, 8.2, 17.3],\n" +
                                         "            [26, 8.4, 17.7],\n" +
                                         "            [27, 8.5, 18.0],\n" +
                                         "            [28, 8.6, 18.3],\n" +
                                         "            [29, 8.8, 18.7],\n" +
                                         "            [30, 8.9, 19.0],\n" +
                                         "            [31, 9.0, 19.3],\n" +
                                         "            [32, 9.1, 19.6],\n" +
                                         "            [33, 9.3, 20.0],\n" +
                                         "            [34, 9.4, 20.3],\n" +
                                         "            [35, 9.5, 20.6],\n" +
                                         "            [36, 9.6, 20.9],\n" +
                                         "            [37, 9.7, 21.3],\n" +
                                         "            [38, 9.8, 21.6],\n" +
                                         "            [39, 9.9, 22.0],\n" +
                                         "            [40, 10.1, 22.3],\n" +
                                         "            [41, 10.2, 22.7],\n" +
                                         "            [42, 10.3, 23.0],\n" +
                                         "            [43, 10.4, 23.4],\n" +
                                         "            [44, 10.5, 23.7],\n" +
                                         "            [45, 10.6, 24.1],\n" +
                                         "            [46, 10.7, 24.5],\n" +
                                         "            [47, 10.8, 24.8],\n" +
                                         "            [48, 10.9, 25.2],\n" +
                                         "            [49, 11.0, 25.5],\n" +
                                         "            [50, 11.1, 25.9],\n" +
                                         "            [51, 11.2, 26.3],\n" +
                                         "            [52, 11.3, 26.6],\n" +
                                         "            [53, 11.4, 27.0],\n" +
                                         "            [54, 11.5, 27.4],\n" +
                                         "            [55, 11.6, 27.7],\n" +
                                         "            [56, 11.7, 28.1],\n" +
                                         "            [57, 11.8, 28.5],\n" +
                                         "            [58, 11.9, 28.8],\n" +
                                         "            [59, 12.0, 29.2],\n" +
                                         "            [60, 12.1, 29.5],\n" +
                                         "        ],\n" +
                                         "        data_orange = [\n" +
                                         "            [0, 2.4, 4.2],\n" +
                                         "            [1, 3.2, 5.5],\n" +
                                         "            [2, 3.9, 6.6],\n" +
                                         "            [3, 4.5, 7.5],\n" +
                                         "            [4, 5.0, 8.2],\n" +
                                         "            [5, 5.4, 8.8],\n" +
                                         "            [6, 5.7, 9.3],\n" +
                                         "            [7, 6.0, 9.8],\n" +
                                         "            [8, 6.3, 10.2],\n" +
                                         "            [9, 6.5, 10.5],\n" +
                                         "            [10, 6.7, 10.9],\n" +
                                         "            [11, 6.9, 11.2],\n" +
                                         "            [12, 7.0, 11.5],\n" +
                                         "            [13, 7.2, 11.8],\n" +
                                         "            [14, 7.4, 12.1],\n" +
                                         "            [15, 7.6, 12.4],\n" +
                                         "            [16, 7.7, 12.6],\n" +
                                         "            [17, 7.9, 12.9],\n" +
                                         "            [18, 8.1, 13.2],\n" +
                                         "            [19, 8.2, 13.5],\n" +
                                         "            [20, 8.4, 13.7],\n" +
                                         "            [21, 8.6, 14.0],\n" +
                                         "            [22, 8.7, 14.3],\n" +
                                         "            [23, 8.9, 14.6],\n" +
                                         "            [24, 9.0, 14.8],\n" +
                                         "            [25, 9.2, 15.1],\n" +
                                         "            [26, 9.4, 15.4],\n" +
                                         "            [27, 9.5, 15.7],\n" +
                                         "            [28, 9.7, 16.0],\n" +
                                         "            [29, 9.8, 16.2],\n" +
                                         "            [30, 10.0, 16.5],\n" +
                                         "            [31, 10.1, 16.8],\n" +
                                         "            [32, 10.3, 17.1],\n" +
                                         "            [33, 10.4, 17.3],\n" +
                                         "            [34, 10.5, 17.6],\n" +
                                         "            [35, 10.7, 17.9],\n" +
                                         "            [36, 10.8, 18.1],\n" +
                                         "            [37, 10.9, 18.4],\n" +
                                         "            [38, 11.1, 18.7],\n" +
                                         "            [39, 11.2, 19.0],\n" +
                                         "            [40, 11.3, 19.2],\n" +
                                         "            [41, 11.5, 19.5],\n" +
                                         "            [42, 11.6, 19.8],\n" +
                                         "            [43, 11.7, 20.1],\n" +
                                         "            [44, 11.8, 20.4],\n" +
                                         "            [45, 12.0, 20.7],\n" +
                                         "            [46, 12.1, 20.9],\n" +
                                         "            [47, 12.2, 21.2],\n" +
                                         "            [48, 12.3, 21.5],\n" +
                                         "            [49, 12.4, 21.8],\n" +
                                         "            [50, 12.6, 22.1],\n" +
                                         "            [51, 12.7, 22.4],\n" +
                                         "            [52, 12.8, 22.6],\n" +
                                         "            [53, 12.9, 22.9],\n" +
                                         "            [54, 13.0, 23.2],\n" +
                                         "            [55, 13.2, 23.5],\n" +
                                         "            [56, 13.3, 23.8],\n" +
                                         "            [57, 13.4, 24.1],\n" +
                                         "            [58, 13.5, 24.4],\n" +
                                         "            [59, 13.6, 24.6],\n" +
                                         "            [60, 13.7, 24.9],\n" +
                                         "        ],\n" +
                                         "        data_yellow = [\n" +
                                         "            [0, 2.8, 3.7],\n" +
                                         "            [1, 3.6, 4.8],\n" +
                                         "            [2, 4.5, 5.8],\n" +
                                         "            [3, 5.2, 6.6],\n" +
                                         "            [4, 5.7, 7.3],\n" +
                                         "            [5, 6.1, 7.8],\n" +
                                         "            [6, 6.5, 8.2],\n" +
                                         "            [7, 6.8, 8.6],\n" +
                                         "            [8, 7.0, 9.0],\n" +
                                         "            [9, 7.3, 9.3],\n" +
                                         "            [10, 7.5, 9.6],\n" +
                                         "            [11, 7.7, 9.9],\n" +
                                         "            [12, 7.9, 10.1],\n" +
                                         "            [13, 8.1, 10.4],\n" +
                                         "            [14, 8.3, 10.6],\n" +
                                         "            [15, 8.5, 10.9],\n" +
                                         "            [16, 8.7, 11.1],\n" +
                                         "            [17, 8.9, 11.4],\n" +
                                         "            [18, 9.1, 11.6],\n" +
                                         "            [19, 9.2, 11.8],\n" +
                                         "            [20, 9.4, 12.1],\n" +
                                         "            [21, 9.6, 12.3],\n" +
                                         "            [22, 9.8, 12.5],\n" +
                                         "            [23, 10.0, 12.8],\n" +
                                         "            [24, 10.2, 13.0],\n" +
                                         "            [25, 10.3, 13.3],\n" +
                                         "            [26, 10.5, 13.5],\n" +
                                         "            [27, 10.7, 13.7],\n" +
                                         "            [28, 10.9, 14.0],\n" +
                                         "            [29, 11.1, 14.2],\n" +
                                         "            [30, 11.2, 14.4],\n" +
                                         "            [31, 11.4, 14.7],\n" +
                                         "            [32, 11.6, 14.9],\n" +
                                         "            [33, 11.7, 15.1],\n" +
                                         "            [34, 11.9, 15.4],\n" +
                                         "            [35, 12.0, 15.6],\n" +
                                         "            [36, 12.2, 15.8],\n" +
                                         "            [37, 12.4, 16.0],\n" +
                                         "            [38, 12.5, 16.3],\n" +
                                         "            [39, 12.7, 16.5],\n" +
                                         "            [40, 12.8, 16.7],\n" +
                                         "            [41, 13.0, 16.9],\n" +
                                         "            [42, 13.1, 17.2],\n" +
                                         "            [43, 13.3, 17.4],\n" +
                                         "            [44, 13.4, 17.6],\n" +
                                         "            [45, 13.6, 17.8],\n" +
                                         "            [46, 13.7, 18.1],\n" +
                                         "            [47, 13.9, 18.3],\n" +
                                         "            [48, 14.0, 18.5],\n" +
                                         "            [49, 14.2, 18.8],\n" +
                                         "            [50, 14.3, 19.0],\n" +
                                         "            [51, 14.5, 19.2],\n" +
                                         "            [52, 14.6, 19.4],\n" +
                                         "            [53, 14.8, 19.7],\n" +
                                         "            [54, 14.9, 19.9],\n" +
                                         "            [55, 15.1, 20.1],\n" +
                                         "            [56, 15.2, 20.3],\n" +
                                         "            [57, 15.3, 20.6],\n" +
                                         "            [58, 15.5, 20.8],\n" +
                                         "            [59, 15.6, 21.0],\n" +
                                         "            [60, 15.8, 21.2],\n" +
                                         "        ]\n"+
                                        "\n" +
                                        "\n" +
                                        "    var data = \n" +
                                        grafik.toString() +
                                        "    ;\n" +
                                        "\n" +
                                        "\n" +
                                        "    var chart = Highcharts.chart('container', {\n" +
                                        "\n" +
                                        "        title: {\n" +
                                        "            text: 'Berat Badan menurut Umur (P)'\n" +
                                        "        },\n" +
                                        "\n" +
                                        "        xAxis: {\n" +
                                        "            type: 'linear',\n" +
                                        "            minRange: 60,\n" +
                                        "            title: {\n" +
                                        "                text: \"Usia\"\n" +
                                        "            },\n" +
                                        "            allowDecimals: false\n" +
                                        "        },\n" +
                                        "\n" +
                                        "        yAxis: {\n" +
                                        "            min: 0,\n" +
                                        "            max: 30,\n" +
                                        "            title: {\n" +
                                        "                text: \"Kilogram\"\n" +
                                        "            }\n" +
                                        "        },\n" +
                                        "\n" +
                                        "        tooltip: {\n" +
                                        "            valueSuffix: ' Kg'\n" +
                                        "        },\n" +
                                        "        series: [{\n" +
                                        "                name: 'Berat Badan',\n" +
                                        "                data: data,\n" +
                                        "                zIndex: 5,\n" +
                                        "                color: 'black',\n" +
                                        "                connectNulls: true,\n" +
                                        "            },\n" +
                                        "\n" +
                                        "            {\n" +
                                        "                name: 'Gizi Buruk',\n" +
                                        "                data: data_red,\n" +
                                        "                type: 'arearange',\n" +
                                        "                lineWidth: 0,\n" +
                                        "                color: '#ffff00',\n" +
                                        "                fillOpacity: 0.9,\n" +
                                        "                zIndex: 0,\n" +
                                        "                marker: {\n" +
                                        "                    enabled: false\n" +
                                        "                },\n" +
                                        "                enableMouseTracking: false\n" +
                                        "            },\n" +
                                        "\n" +
                                        "            {\n" +
                                        "                name: 'Gizi Kurang',\n" +
                                        "                data: data_orange,\n" +
                                        "                type: 'arearange',\n" +
                                        "                lineWidth: 0,\n" +
                                        "                color: 'lime',\n" +
                                        "                fillOpacity: 0.3,\n" +
                                        "                zIndex: 1,\n" +
                                        "                marker: {\n" +
                                        "                    enabled: false\n" +
                                        "                },\n" +
                                        "                enableMouseTracking: false\n" +
                                        "            },\n" +
                                        "\n" +
                                        "            {\n" +
                                        "                name: 'Gizi Baik',\n" +
                                        "                data: data_yellow,\n" +
                                        "                type: 'arearange',\n" +
                                        "                lineWidth: 0,\n" +
                                        "                color: 'green',\n" +
                                        "                fillOpacity: 0.3,\n" +
                                        "                zIndex: 2,\n" +
                                        "                marker: {\n" +
                                        "                    enabled: false\n" +
                                        "                },\n" +
                                        "                enableMouseTracking: false\n" +
                                        "            }\n" +
                                        "        ]\n" +
                                        "    });\n" +
                                        "</script>\n" +
                                        "</body></html>";
                            }
                            webView.loadDataWithBaseURL(null, js, "text/html", "utf-8", null);
                            break;
                        case 1:
                            AlertBuilder builder = new AlertBuilder("Data Kosong", "Error", getActivity());
                            builder.show();
                            break;
                    }
                    sw.setRefreshing(false);

                }
                catch (JSONException e) {
                    e.getStackTrace();
                    AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan saat Memparsing data",
                            "Error", getActivity());
                    builder.show();
                    Log.e(TAG, "onResponse: error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                AlertBuilder builder = new AlertBuilder("Terjadi Kesalahan pada Koneksi",
                        "Error", getActivity());
                builder.show();
            }
        });
        Controller.getmInstance().addToRequestQueue(RequestKMS, "KONDISI");
    }
}
