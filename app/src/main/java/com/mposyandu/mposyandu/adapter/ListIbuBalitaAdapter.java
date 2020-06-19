package com.mposyandu.mposyandu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mposyandu.mposyandu.data.BalitaModel;
import com.mposyandu.mposyandu.R;
import com.mposyandu.mposyandu.tools.CircleTransform;
import com.mposyandu.mposyandu.tools.Database;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListIbuBalitaAdapter extends RecyclerView.Adapter<ListIbuBalitaAdapter.balitaViewHolder> {
    private List<BalitaModel> balita;
    private static ClickListener clickListener;

        public static class balitaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nama;
        ImageView thumbs;

        balitaViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                nama = itemView.findViewById(R.id.ibu_list_balita_nama);
                thumbs = itemView.findViewById(R.id.ibu_list_balita_thumbs);
            }

            @Override
            public void onClick(View v) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }


    public ListIbuBalitaAdapter(List<BalitaModel> balita) {
        this.balita = balita;
    }

    @Override
    public int getItemCount() {
        return balita.size();
    }

    @NonNull
    @Override
    public balitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_ibu, parent, false);
        return new balitaViewHolder(view);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ListIbuBalitaAdapter.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull balitaViewHolder holder, int position) {
        holder.nama.setText(balita.get(position).getNama());
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

    public BalitaModel getItem(int position) {
        return balita.get(position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull balitaViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }
}
