package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ExportModel implements Parcelable {
    private String nama;
    private String id;
    private String berat;
    private String tinggi;
    private String lingkar_kepala;
    private String bulan;

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBerat() {
        return berat;
    }

    public void setBerat(String berat) {
        this.berat = berat;
    }

    public String getTinggi() {
        return tinggi;
    }

    public void setTinggi(String tinggi) {
        this.tinggi = tinggi;
    }

    public String getLingkar_kepala() {
        return lingkar_kepala;
    }

    public void setLingkar_kepala(String lingkar_kepala) {
        this.lingkar_kepala = lingkar_kepala;
    }

    public ExportModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nama);
        dest.writeString(this.id);
        dest.writeString(this.berat);
        dest.writeString(this.tinggi);
        dest.writeString(this.lingkar_kepala);
        dest.writeString(this.bulan);
    }

    protected ExportModel(Parcel in) {
        this.nama = in.readString();
        this.id = in.readString();
        this.berat = in.readString();
        this.tinggi = in.readString();
        this.lingkar_kepala = in.readString();
        this.bulan = in.readString();
    }

    public static final Creator<ExportModel> CREATOR = new Creator<ExportModel>() {
        @Override
        public ExportModel createFromParcel(Parcel source) {
            return new ExportModel(source);
        }

        @Override
        public ExportModel[] newArray(int size) {
            return new ExportModel[size];
        }
    };
}
