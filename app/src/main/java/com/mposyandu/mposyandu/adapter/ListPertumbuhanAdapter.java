package com.mposyandu.mposyandu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mposyandu.mposyandu.data.KondisiModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.fragment.ListPertumbuhan;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.MonthConverter;

import java.util.ArrayList;
import java.util.List;

public class ListPertumbuhanAdapter extends RecyclerView.Adapter<ListPertumbuhanAdapter.kondisiViewHolder> implements Filterable {
    private List<KondisiModel> kondisi;
    private List<KondisiModel> copy;
    private Filter fRecords;
    private ListPertumbuhan listPertumbuhan;
    private Context mContext;
    private UserModel user;


    @Override
    public Filter getFilter() {
        if(fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    private class RecordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint.toString().isEmpty()) {
                results.values = copy;
                results.count = copy.size();
            }
            else {
                List<KondisiModel> record = new ArrayList<>();
                for(KondisiModel k: kondisi) {
                    String waktu = k.getTanggal_input();
                    String[] item = waktu.split("-");
                    MonthConverter mc = new MonthConverter(Integer.parseInt(item[1])-1);
                    String newWaktu = item[2]+"-"+mc.getMonth()+"-"+item[0];
                    if (newWaktu.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            k.getPetugas().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        record.add(k);
                    }
                }
                results.values = record;
                results.count = record.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            kondisi = (List<KondisiModel>) results.values;
            notifyDataSetChanged();
        }
    }

    public static class kondisiViewHolder extends RecyclerView.ViewHolder {
        View mview;
        TextView petugas;
        TextView berat;
        TextView tinggi;
        TextView tanggal;
        TextView lingkar_kepala;
        TextView no;
        Button edit;
        Button delete;


         kondisiViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
            petugas = itemView.findViewById(R.id.ketua_list_kondisi_nama);
            tinggi = itemView.findViewById(R.id.ketua_list_kondisi_tinggi);
            berat = itemView.findViewById(R.id.ketua_list_kondisi_berat);
            lingkar_kepala = itemView.findViewById(R.id.ketua_list_kondisi_lingkar_kepala);
            no = itemView.findViewById(R.id.ketua_list_kondisi_no);
            tanggal = itemView.findViewById(R.id.ketua_list_kondisi_tanggal);
            edit = itemView.findViewById(R.id.ketua_list_kondisi_edit);
            delete = itemView.findViewById(R.id.ketua_list_kondisi_delete);
        }
    }

    public ListPertumbuhanAdapter(List<KondisiModel> kondisi, UserModel user, ListPertumbuhan context, Context mContext) {
        this.kondisi = kondisi;
        this.copy = kondisi;
        this.listPertumbuhan = context;
        this.mContext = mContext;
        this.user = user;
    }

    @NonNull
    @Override
    public kondisiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_pertumbuhan, parent, false);
        return new kondisiViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final kondisiViewHolder holder, final int position) {
        KondisiModel k = kondisi.get(position);
        String berat = k.getBerat()+ " Kg";
        String tinggi = k.getTinggi()+ " Cm";
        String lingkar = k.getLingkar_kepala()+" Cm";
        String petugas = k.getPetugas();
        String usia = k.getUsia()+" Bln";
        if(k.getBerat() == null) {
            berat = "-";
            petugas = "-";
            holder.delete.setEnabled(false);
            holder.edit.setEnabled(false);
        }
        else {
            holder.delete.setEnabled(true);
            holder.edit.setEnabled(true);
        }
        if (k.getTinggi() == null) {
            tinggi = "-";
        }

        if(k.getLingkar_kepala() == null) {
            lingkar = "-";
        }
        Integer usia_parse = Integer.parseInt(k.getUsia());
        if(usia_parse >= 12) {
            int years = usia_parse / 12; // 1
            int remainingMonths = usia_parse % 12;
            usia = years+" Thn "+remainingMonths+" Bln";
        }
        String waktu = k.getTanggal_input();
        String[] item = waktu.split("-");
        MonthConverter mc = new MonthConverter(Integer.parseInt(item[1])-1);
        String newWaktu = item[2]+"-"+mc.getMonth()+"-"+item[0];
        holder.berat.setText(berat);
        holder.tinggi.setText(tinggi);
        holder.lingkar_kepala.setText(lingkar);
        holder.petugas.setText(petugas);
        holder.tanggal.setText(newWaktu);
        holder.no.setText(usia);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPertumbuhan.edit(holder.getAdapterPosition());
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPertumbuhan.delete(holder.getAdapterPosition());
            }
        });
        if(user.getRole_id() == 4) {
            holder.delete.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }
        else {
            holder.delete.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public int getItemCount() {
       return kondisi.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull kondisiViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public KondisiModel getItem(int position) {
        return kondisi.get(position);
    }
}
