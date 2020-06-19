package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class JadwalModel implements Parcelable {
    private Integer id;
    private String kegiatan;
    private String tanggal;
    private String waktu;
    private String creator;
    private Integer creator_id;
    private Integer posyandu_id;

    public Integer getPosyandu_id() {
        return posyandu_id;
    }

    public void setPosyandu_id(Integer posyandu_id) {
        this.posyandu_id = posyandu_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(String kegiatan) {
        this.kegiatan = kegiatan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(Integer creator_id) {
        this.creator_id = creator_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.kegiatan);
        dest.writeString(this.tanggal);
        dest.writeString(this.waktu);
        dest.writeString(this.creator);
        dest.writeValue(this.creator_id);
        dest.writeValue(this.posyandu_id);
    }

    public JadwalModel() {
    }

    protected JadwalModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.kegiatan = in.readString();
        this.tanggal = in.readString();
        this.waktu = in.readString();
        this.creator = in.readString();
        this.creator_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.posyandu_id = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<JadwalModel> CREATOR = new Parcelable.Creator<JadwalModel>() {
        @Override
        public JadwalModel createFromParcel(Parcel source) {
            return new JadwalModel(source);
        }

        @Override
        public JadwalModel[] newArray(int size) {
            return new JadwalModel[size];
        }
    };
}
