package com.mposyandu.mposyandu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.data.AnggotaModel;
import com.mposyandu.mposyandu.retrofitModel.UserModelPost;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.Database;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListAnggotaAdapterRetrofit extends RecyclerView.Adapter<ListAnggotaAdapterRetrofit.anggotaViewHolder> implements Filterable {
    private List<UserModelPost> anggota;
    private List<UserModelPost> copy;
    private static ClickListener clickListener;
    private Filter fRecords;

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
            Log.d("FIlter", constraint.toString());
            if(constraint.toString().isEmpty()) {
                Log.d("IsEmpty :", "Empty");
                results.values = copy;
                results.count = copy.size();
            }
            else {
                List<UserModelPost> record = new ArrayList<>();

                for (UserModelPost a : anggota) {
                    if (a.getNama().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        Log.d("Nama :", a.getNama());
                        record.add(a);
                    }
                }
                results.values = record;
                results.count = record.size();
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            anggota = (List<UserModelPost>) results.values;
            notifyDataSetChanged();
        }
    }

    public static class anggotaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nama;
        TextView alamat;
        ImageView thumbs;

        anggotaViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nama = itemView.findViewById(R.id.ketua_list_anggota_nama);
            alamat = itemView.findViewById(R.id.ketua_list_anggota_alamat);
            thumbs = itemView.findViewById(R.id.ketua_list_anggota_thumbs);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public ListAnggotaAdapterRetrofit(List<UserModelPost> anggota) {
        this.anggota = anggota;
        this.copy = anggota;
    }

    @Override
    public int getItemCount() {
        return anggota.size();
    }

    @NonNull
    @Override
    public anggotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_anggota, parent, false);
        return new anggotaViewHolder(view);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ListAnggotaAdapterRetrofit.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull anggotaViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.nama.setText(anggota.get(position).getNama());
        holder.alamat.setText(anggota.get(position).getAlamat());
        Picasso.get().load(Database.getUrl()+"/"+anggota.get(position).getPhoto())
                .transform(new CircleTransform())
                .into(holder.thumbs);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull anggotaViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }
    public UserModelPost getItem(int position) {
        return anggota.get(position);
    }

}


