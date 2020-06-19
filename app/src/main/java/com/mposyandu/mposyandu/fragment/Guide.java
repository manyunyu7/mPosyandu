package com.mposyandu.mposyandu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mposyandu.mposyandu.R;

public class Guide extends Fragment {
    private TextView a,b,c,d;
    private LinearLayout ly_a, ly_b, ly_c, ly_d;
    private Boolean f;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide, container, false);
        a = view.findViewById(R.id.textview_profile);
        b = view.findViewById(R.id.textview_pertumbuhan);
        c = view.findViewById(R.id.textview_vitamin);
        d = view.findViewById(R.id.textview_imunisasi);
        f = false;
        ly_b =  view.findViewById(R.id.ly_pertumbuhan);
        ly_c =  view.findViewById(R.id.ly_vitamin);
        ly_d =  view.findViewById(R.id.ly_imunisasi);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f) {
                    ly_b.setVisibility(View.VISIBLE);
                    f = false;
                }
                else {
                    ly_b.setVisibility(View.GONE);
                    f = true;
                }
            }
        });

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f) {
                    ly_c.setVisibility(View.VISIBLE);
                    f = false;
                }
                else {
                    ly_c.setVisibility(View.GONE);
                    f = true;
                }
            }
        });

        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f) {
                    ly_d.setVisibility(View.VISIBLE);
                    f = false;
                }
                else {
                    ly_d.setVisibility(View.GONE);
                    f = true;
                }
            }
        });
        return view;
    }
}
