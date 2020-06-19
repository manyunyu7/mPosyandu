package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class VitaminOpsiModel implements Parcelable {
    private String nama;
    private Integer id;
    private String deskripsi;
    private String usia;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getUsia() {
        return usia;
    }

    public void setUsia(String usia) {
        this.usia = usia;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nama);
        dest.writeValue(this.id);
        dest.writeString(this.deskripsi);
        dest.writeString(this.usia);
    }

    public VitaminOpsiModel() {
    }

    protected VitaminOpsiModel(Parcel in) {
        this.nama = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.deskripsi = in.readString();
        this.usia = in.readString();
    }

    public static final Parcelable.Creator<VitaminOpsiModel> CREATOR = new Parcelable.Creator<VitaminOpsiModel>() {
        @Override
        public VitaminOpsiModel createFromParcel(Parcel source) {
            return new VitaminOpsiModel(source);
        }

        @Override
        public VitaminOpsiModel[] newArray(int size) {
            return new VitaminOpsiModel[size];
        }
    };
}
