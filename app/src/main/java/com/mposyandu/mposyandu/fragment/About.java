package com.mposyandu.mposyandu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mposyandu.mposyandu.R;

public class About extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, container, false);

        TextView email = view.findViewById(R.id.email_link);
        TextView website = view.findViewById(R.id.website_link);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getString(R.string.contact_email), null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Help Mposyandu");
                startActivity(Intent.createChooser(intent, "Pilih klien email:"));
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+getString(R.string.website_link)));
                startActivity(intent);
            }
        });

        return view;
    }
}
