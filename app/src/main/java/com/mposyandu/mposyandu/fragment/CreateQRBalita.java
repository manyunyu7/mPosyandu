package com.mposyandu.mposyandu.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.data.UserModel;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class CreateQRBalita extends Fragment {
    UserModel user;
    BalitaModel balita;
    View isi;
    String nama,posyandu,id;
    String idQR;
    String setterQR="";

    ImageView showQR;
    TextView replacement,idMentee,idMain;

    public CreateQRBalita() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isi = inflater.inflate(R.layout.fragment_create_qr_balita, container, false);
        Bundle bundle = getArguments();
        user = bundle.getParcelable("user");
        balita = bundle.getParcelable("balita");
        nama=balita.getNama();
        posyandu=balita.getPosyandu();
        showQR=isi.findViewById(R.id.show_qr);
        replacement=isi.findViewById(R.id.reptext);
        idMentee=isi.findViewById(R.id.id_mentee);
        idMain=isi.findViewById(R.id.id_mentee_main);
        idMain.setText(nama);
        idMentee.setText("Posyandu :\n"+posyandu);

        isi.findViewById(R.id.loading_scan).setVisibility(View.GONE);

        setterQR=balita.getId_qrcode();

        Log.d("CREATE_QR","setterQR: "+balita.getNama());

        QRGEncoder qre = new QRGEncoder(setterQR, null, QRGContents.Type.TEXT, 500);
        Bitmap qrbm = qre.getBitmap();
        replacement.setVisibility(View.INVISIBLE);
        showQR.setImageBitmap(qrbm);

        return isi;
    }
}