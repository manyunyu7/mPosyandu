package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class KondisiModel implements Parcelable {
    private Integer id;
    private Integer balita_id;
    private Double berat;
    private String tanggal_input;
    private Double tinggi;
    private Double lingkar_kepala;
    private Integer petugas_id;
    private String petugas;
    private String usia;

    public String getUsia() {
        return usia;
    }

    public void setUsia(String usia) {
        this.usia = usia;
    }

    public String getPetugas() {
        return petugas;
    }

    public void setPetugas(String petugas) {
        this.petugas = petugas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBalita_id() {
        return balita_id;
    }

    public void setBalita_id(Integer balita_id) {
        this.balita_id = balita_id;
    }

    public Double getBerat() {
        return berat;
    }

    public void setBerat(Double berat) {
        this.berat = berat;
    }

    public String getTanggal_input() {
        return tanggal_input;
    }

    public void setTanggal_input(String tanggal_input) {
        this.tanggal_input = tanggal_input;
    }

    public Double getTinggi() {
        return tinggi;
    }

    public void setTinggi(Double tinggi) {
        this.tinggi = tinggi;
    }

    public Double getLingkar_kepala() {
        return lingkar_kepala;
    }

    public void setLingkar_kepala(Double lingkar_kepala) {
        this.lingkar_kepala = lingkar_kepala;
    }

    public Integer getPetugas_id() {
        return petugas_id;
    }

    public void setPetugas_id(Integer petugas_id) {
        this.petugas_id = petugas_id;
    }

    public KondisiModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.balita_id);
        dest.writeValue(this.berat);
        dest.writeString(this.tanggal_input);
        dest.writeValue(this.tinggi);
        dest.writeValue(this.lingkar_kepala);
        dest.writeValue(this.petugas_id);
        dest.writeString(this.petugas);
        dest.writeString(this.usia);
    }

    protected KondisiModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.balita_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.berat = (Double) in.readValue(Double.class.getClassLoader());
        this.tanggal_input = in.readString();
        this.tinggi = (Double) in.readValue(Double.class.getClassLoader());
        this.lingkar_kepala = (Double) in.readValue(Double.class.getClassLoader());
        this.petugas_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.petugas = in.readString();
        this.usia = in.readString();
    }

    public static final Creator<KondisiModel> CREATOR = new Creator<KondisiModel>() {
        @Override
        public KondisiModel createFromParcel(Parcel source) {
            return new KondisiModel(source);
        }

        @Override
        public KondisiModel[] newArray(int size) {
            return new KondisiModel[size];
        }
    };
}
