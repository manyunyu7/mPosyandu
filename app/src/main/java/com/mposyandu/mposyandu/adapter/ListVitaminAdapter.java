package com.mposyandu.mposyandu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.data.VitaminModel;
import com.mposyandu.mposyandu.data.VitaminOpsiModel;
import com.mposyandu.mposyandu.fragment.ListVitamin;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.MonthConverter;

import java.util.List;

public class ListVitaminAdapter extends RecyclerView.Adapter<ListVitaminAdapter.vitaminViewHolder> {
    private List<VitaminModel> baby_vitamin;
    private List<VitaminOpsiModel> opsi;
    private UserModel user;
    private String waktulast = null;
    private ListVitamin listVitamin;
    private Context context;
    private PopupMenu popup;


    public ListVitaminAdapter(List<VitaminModel> vitamin, List<VitaminOpsiModel> opsi, ListVitamin listVitamin, Context context , UserModel user) {
        this.baby_vitamin = vitamin;
        this.opsi = opsi;
        this.user = user;
        this.listVitamin = listVitamin;
        this.context = context;
    }

    //merah
    class vitaminViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView nama;
    TextView deskripsi;
    TextView tanggal;
    TextView tanggal2;
    LinearLayout ly;
    LinearLayout ly2;
    TextView petugas;
    TextView petugas2;
    Button edit;
    Button delete;
        public vitaminViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            nama = itemView.findViewById(R.id.ketua_list_vitamin_nama);
            deskripsi = itemView.findViewById(R.id.ketua_list_vitamin_deskripsi);
            tanggal = itemView.findViewById(R.id.ketua_list_vitamin_tanggal);
            tanggal2 = itemView.findViewById(R.id.ketua_list_vitamin_tanggal2);
            ly = itemView.findViewById(R.id.ketua_list_vitamin_tanggal_layout);
            ly2 = itemView.findViewById(R.id.ketua_list_vitamin_petugas_layout);
            petugas = itemView.findViewById(R.id.ketua_list_vitamin_petugas);
            petugas2 = itemView.findViewById(R.id.ketua_list_vitamin_petugas2);
            delete = itemView.findViewById(R.id.ketua_list_vitamin_delete);
            edit = itemView.findViewById(R.id.ketua_list_vitamin_edit);
        }
    }


    @NonNull
    @Override
    public vitaminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_vitamin, parent, false);
        return new vitaminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final vitaminViewHolder holder, int position) {
    VitaminOpsiModel o = opsi.get(position);

        if(user.getRole_id() == 4) {
            holder.delete.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);

        }
        else {
            holder.delete.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setEnabled(false);
            holder.edit.setEnabled(false);
        }

        holder.tanggal.setText("-");
        holder.petugas.setText("-");
        holder.tanggal2.setText("-");
        holder.petugas2.setText("-");

    if(o.getNama().equals("Biru")) {
        holder.ly.setVisibility(View.GONE);
        holder.ly2.setVisibility(View.GONE);
        if(baby_vitamin.size() > 0) {
            if(baby_vitamin.get(0).getVitamin_id() == 1) {
                final int finalPosition = position;
                String waktu = baby_vitamin.get(finalPosition).getTanggal_input();
                String[] item = waktu.split("-");
                MonthConverter mc = new MonthConverter(Integer.parseInt(item[1]) - 1);
                String newWaktu = item[2] + "-" + mc.getMonth() + "-" + item[0];
                holder.tanggal.setText(newWaktu);
                holder.petugas.setText(baby_vitamin.get(finalPosition).getPetugas());
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String waktu = baby_vitamin.get(finalPosition).getTanggal_input();
                        String[] item = waktu.split("-");
                        MonthConverter mc = new MonthConverter(Integer.parseInt(item[1])-1);

                        if(mc.getMonth().equals("Februari")) {
                            (listVitamin) .edit(finalPosition, "02");
                        }
                        else {
                            (listVitamin) .edit(finalPosition, "08");
                        }
                    }
                });
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String waktu = baby_vitamin.get(finalPosition).getTanggal_input();
                        String[] item = waktu.split("-");
                        MonthConverter mc = new MonthConverter(Integer.parseInt(item[1])-1);
                        String o = "";
                        if (mc.getMonth().equals("Februari")) {
                            o = "02";
                        }
                        if (mc.getMonth().equals("Agustus")) {
                            o = "08";
                        }

                        listVitamin.delete(finalPosition ,o);
                    }
                });
            }
        }


    }
    else {
        holder.ly.setVisibility(View.VISIBLE);
        holder.ly2.setVisibility(View.VISIBLE);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(context, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.list_menu, popup.getMenu());
                Menu menu = popup.getMenu();
                MenuItem item = menu.findItem(R.id.action_1);
                MenuItem item2 = menu.findItem(R.id.action_2);

                if (holder.tanggal.getText() == "-") {
                    item.setVisible(false);

                }
                if (holder.tanggal2.getText() == "-") {
                    item2.setVisible(false);

                }

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().toString().equals("Februari")) {
                            listVitamin.edit(holder.getAdapterPosition() ,"02");

                        }
                        if(item.getTitle().toString().equals("Agustus")) {
                            listVitamin.edit(holder.getAdapterPosition() ,"08");

                        }

                        return true;
                    }
                });
                popup.show();
            }
        });
        final int finalPosition1 = position;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(context, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.list_menu, popup.getMenu());
                Menu menu = popup.getMenu();
                MenuItem item = menu.findItem(R.id.action_1);
                MenuItem item2 = menu.findItem(R.id.action_2);

                if (holder.tanggal.getText() == "-") {
                    item.setVisible(false);

                }
                if (holder.tanggal2.getText() == "-") {
                    item2.setVisible(false);

                }


                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().toString().equals("Februari")) {
                            listVitamin.delete(holder.getAdapterPosition() ,"02");
                        }
                        if(item.getTitle().toString().equals("Agustus")) {
                            listVitamin.delete(holder.getAdapterPosition() ,"08");
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });

    }

        for(Integer i=0; i<baby_vitamin.size(); i++) {
            VitaminModel v2 = baby_vitamin.get(i);
            if(o.getId().equals(v2.getVitamin_id())) {
                position++;
                String waktu = v2.getTanggal_input();
                String[] item = waktu.split("-");
                MonthConverter mc = new MonthConverter(Integer.parseInt(item[1]) - 1);
                String newWaktu = item[2] + "-" + mc.getMonth() + "-" + item[0];

                if (item[1].equals("02")) {
                    holder.tanggal.setText(newWaktu);
                    holder.petugas.setText(v2.getPetugas());
                    holder.delete.setEnabled(true);
                    holder.edit.setEnabled(true);
                }
                if (item[1].equals("08")) {
                    holder.tanggal2.setText(newWaktu);
                    holder.petugas2.setText(v2.getPetugas());
                    holder.delete.setEnabled(true);
                    holder.edit.setEnabled(true);
                }
            }
        }

        holder.deskripsi.setText(String.format("Usia %s bulan , %s", o.getUsia(), o.getDeskripsi()));
        holder.nama.setText(String.format("Vitamin A Kapsul %s", o.getNama()));
    }


    @Override
    public int getItemCount() {
        return opsi.size();
    }

}
