package com.mposyandu.mposyandu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mposyandu.mposyandu.data.ImunisasiModel;
import com.mposyandu.mposyandu.data.ImunisasiOpsiModel;
import com.mposyandu.mposyandu.data.UserModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.fragment.ListImunisasi;
import com.mposyandu.mposyandu.tools.MonthConverter;

import java.util.List;

public class ListImunisasiAdapter extends RecyclerView.Adapter<ListImunisasiAdapter.imunisasiViewHolder> {
    private List<ImunisasiOpsiModel> imunisasi;
    private List<ImunisasiModel> baby_imunisasi;
    private Context mContext;
    private UserModel user;
    private ListImunisasi context;

    public ListImunisasiAdapter(List<ImunisasiOpsiModel> imunisasi, List<ImunisasiModel> baby_imunisasi, UserModel user, ListImunisasi sContext, Context context) {
        this.imunisasi = imunisasi;
        this.baby_imunisasi = baby_imunisasi;
        this.mContext = context;
        this.context = sContext;
        this.user = user;
    }

     public static class imunisasiViewHolder extends RecyclerView.ViewHolder {
         View mView;
         TextView petugas;
         TextView nama;
         TextView tanggal;
         Button edit;
         Button delete;


         imunisasiViewHolder(View itemView) {
             super(itemView);
             mView = itemView;
             petugas = mView.findViewById(R.id.ketua_list_imunisasi_petugas);
             nama = mView.findViewById(R.id.ketua_list_imunisasi_nama);
             tanggal = mView.findViewById(R.id.ketua_list_imunisasi_tanggal);
             edit = itemView.findViewById(R.id.ketua_list_imunisasi_edit);
             delete = itemView.findViewById(R.id.ketua_list_imunisasi_delete);


         }
     }

    @NonNull
    @Override
    public ListImunisasiAdapter.imunisasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_imunisasi, parent, false);
        return new imunisasiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListImunisasiAdapter.imunisasiViewHolder holder, int position) {
        final ImunisasiOpsiModel opsi = imunisasi.get(position);
        holder.petugas.setText("-");
        holder.tanggal.setText("-");
        holder.delete.setEnabled(false);
        holder.edit.setEnabled(false);

        if(user.getRole_id() == 4) {
            holder.delete.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }
        else {
            holder.delete.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
        }

        for(Integer i = 0; i<baby_imunisasi.size(); i++ ) {
            ImunisasiModel imunisasiModel = baby_imunisasi.get(i);
            if(imunisasiModel.getImun_id().equals(opsi.getId())) {
                position++;
                String waktu = imunisasiModel.getTanggal_input();
                String[] item = waktu.split("-");
                MonthConverter mc = new MonthConverter(Integer.parseInt(item[1])-1);
                String newWaktu = item[2]+"-"+mc.getMonth()+"-"+item[0].substring(2,4);
                holder.petugas.setText(imunisasiModel.getPetugas());
                holder.tanggal.setText(newWaktu);
                holder.delete.setEnabled(true);
                holder.edit.setEnabled(true);
            }
        }

            if(position < baby_imunisasi.size()) {


            }


        holder.nama.setText(opsi.getNama());
        holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.delete(holder.getAdapterPosition());
                }
            });
        final int finalPosition1 = position;
        holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.edit(holder.getAdapterPosition());
                }
            });
        }

    @Override
    public void onViewAttachedToWindow(@NonNull imunisasiViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return imunisasi.size();
    }
}
