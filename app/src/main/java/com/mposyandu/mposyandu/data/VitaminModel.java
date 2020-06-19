package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class VitaminModel implements Parcelable {
    private Integer balita_id;
    private Integer id;
    private Integer vitamin_id;
    private Integer petugas_id;
    private Integer posyandu_id;
    private String petugas;
    private String tanggal_input;

    public Integer getBalita_id() {
        return balita_id;
    }

    public void setBalita_id(Integer balita_id) {
        this.balita_id = balita_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVitamin_id() {
        return vitamin_id;
    }

    public void setVitamin_id(Integer vitamin_id) {
        this.vitamin_id = vitamin_id;
    }

    public Integer getPetugas_id() {
        return petugas_id;
    }

    public void setPetugas_id(Integer petugas_id) {
        this.petugas_id = petugas_id;
    }

    public Integer getPosyandu_id() {
        return posyandu_id;
    }

    public void setPosyandu_id(Integer posyandu_id) {
        this.posyandu_id = posyandu_id;
    }

    public String getPetugas() {
        return petugas;
    }

    public void setPetugas(String petugas) {
        this.petugas = petugas;
    }

    public String getTanggal_input() {
        return tanggal_input;
    }

    public void setTanggal_input(String tanggal_input) {
        this.tanggal_input = tanggal_input;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.balita_id);
        dest.writeValue(this.id);
        dest.writeValue(this.vitamin_id);
        dest.writeValue(this.petugas_id);
        dest.writeValue(this.posyandu_id);
        dest.writeString(this.petugas);
        dest.writeString(this.tanggal_input);
    }

    public VitaminModel() {
    }

    protected VitaminModel(Parcel in) {
        this.balita_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.vitamin_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.petugas_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.posyandu_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.petugas = in.readString();
        this.tanggal_input = in.readString();
    }

    public static final Creator<VitaminModel> CREATOR = new Creator<VitaminModel>() {
        @Override
        public VitaminModel createFromParcel(Parcel source) {
            return new VitaminModel(source);
        }

        @Override
        public VitaminModel[] newArray(int size) {
            return new VitaminModel[size];
        }
    };
}
