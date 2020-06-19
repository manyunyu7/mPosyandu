package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ImunisasiOpsiModel implements Parcelable {
    private Integer id;
    private String nama;

    public Integer getUsia() {
        return usia;
    }

    public void setUsia(Integer usia) {
        this.usia = usia;
    }

    private Integer usia;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public ImunisasiOpsiModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.nama);
        dest.writeValue(this.usia);
    }

    protected ImunisasiOpsiModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.nama = in.readString();
        this.usia = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ImunisasiOpsiModel> CREATOR = new Creator<ImunisasiOpsiModel>() {
        @Override
        public ImunisasiOpsiModel createFromParcel(Parcel source) {
            return new ImunisasiOpsiModel(source);
        }

        @Override
        public ImunisasiOpsiModel[] newArray(int size) {
            return new ImunisasiOpsiModel[size];
        }
    };
}
