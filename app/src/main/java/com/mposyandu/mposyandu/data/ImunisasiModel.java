package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ImunisasiModel implements Parcelable {

    private Integer id;
    private Integer balita_id;
    private Integer imun_id;
    private Integer petugas_id;
    private String petugas;
    private Integer posyandu_id;
    private String tanggal_input;

    public String getTanggal_input() {
        return tanggal_input;
    }

    public void setTanggal_input(String tanggal_input) {
        this.tanggal_input = tanggal_input;
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

    public Integer getImun_id() {
        return imun_id;
    }

    public void setImun_id(Integer imun_id) {
        this.imun_id = imun_id;
    }

    public Integer getPetugas_id() {
        return petugas_id;
    }

    public void setPetugas_id(Integer petugas_id) {
        this.petugas_id = petugas_id;
    }

    public String getPetugas() {
        return petugas;
    }

    public void setPetugas(String petugas) {
        this.petugas = petugas;
    }

    public Integer getPosyandu_id() {
        return posyandu_id;
    }

    public void setPosyandu_id(Integer posyandu_id) {
        this.posyandu_id = posyandu_id;
    }


    public ImunisasiModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.balita_id);
        dest.writeValue(this.imun_id);
        dest.writeValue(this.petugas_id);
        dest.writeString(this.petugas);
        dest.writeValue(this.posyandu_id);
        dest.writeString(this.tanggal_input);
    }

    protected ImunisasiModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.balita_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.imun_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.petugas_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.petugas = in.readString();
        this.posyandu_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tanggal_input = in.readString();
    }

    public static final Creator<ImunisasiModel> CREATOR = new Creator<ImunisasiModel>() {
        @Override
        public ImunisasiModel createFromParcel(Parcel source) {
            return new ImunisasiModel(source);
        }

        @Override
        public ImunisasiModel[] newArray(int size) {
            return new ImunisasiModel[size];
        }
    };
}
