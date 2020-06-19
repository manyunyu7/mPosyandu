package com.mposyandu.mposyandu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.Database;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**

 */
public class ListBalitaAdapter extends RecyclerView.Adapter<ListBalitaAdapter.balitaViewHolder> implements Filterable {

    private List<BalitaModel> balita;
    private List<BalitaModel> copy;
    private static ClickListener clickListener;
    private Filter fRecords;
    private Integer diff;

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
                List<BalitaModel> record = new ArrayList<>();
                for (BalitaModel b : balita) {
                    if(b.getNama().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            b.getIbu().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            b.getAlamat().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        record.add(b);
                    }
                }
                results.values = record;
                results.count = record.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            balita = (List<BalitaModel>) results.values;
            notifyDataSetChanged();
        }
    }

    public static class balitaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;
        TextView nama;
        ImageView thumbs;
        TextView ibu;

        balitaViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mView = itemView;
            nama = itemView.findViewById(R.id.ketua_list_balita_nama);
            thumbs = itemView.findViewById(R.id.ketua_list_balita_thumbs);
            ibu = itemView.findViewById(R.id.ketua_list_balita_ibu);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public ListBalitaAdapter(List<BalitaModel> balita) {
        this.balita = balita;
        this.copy = balita;
    }

    @Override
    public int getItemCount() {
        return balita.size();
    }

    @NonNull
    @Override
    public balitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_balita, parent, false);
        return new balitaViewHolder(view);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ListBalitaAdapter.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull final balitaViewHolder holder,final int position) {
        holder.nama.setText(balita.get(position).getNama());

        holder.ibu.setText("Ibu "+ balita.get(position).getIbu());
        if (balita.get(position).getPhoto().equals("0")) {
            if (balita.get(position).getGender().equals("L")) {
                Picasso.get().load(R.drawable.boy)
                        .into(holder.thumbs);
            }
            else if (balita.get(position).getGender().equals("P")) {
                Picasso.get().load(R.drawable.girl)
                        .into(holder.thumbs);
            }
        }
        else {
            Picasso.get().load(Database.getUrl()+"/"+balita.get(position).getPhoto())
                    .transform(new CircleTransform())
                    .into(holder.thumbs);
        }



    }

    @Override
    public void onViewAttachedToWindow(@NonNull balitaViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public BalitaModel getItem(int position) {
        return balita.get(position);
    }
}
