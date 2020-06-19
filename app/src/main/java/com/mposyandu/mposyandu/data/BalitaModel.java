package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalitaModel implements Parcelable {

    private Integer id;
    private String ibu;
    private String posyandu;
    private String petugas;
    private Integer active;

    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("tanggal_lahir")
    @Expose
    private String tanggal_lahir;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("ibu_id")
    @Expose
    private Integer ibu_id;
    @SerializedName("ayah")
    @Expose
    private String ayah;
    @SerializedName("posyandu_id")
    @Expose
    private Integer posyandu_id;
    @SerializedName("creator_id")
    @Expose
    private Integer creator_id;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("encodedphoto")
    @Expose
    private String encodedphoto;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("berat")
    @Expose
    private String berat;
    @SerializedName("tinggi")
    @Expose
    private String tinggi;
    @SerializedName("lingkar_kepala")
    @Expose
    private String lingkar_kepala;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(Integer creator_id) {
        this.creator_id = creator_id;
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

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setTanggal_lahir(String tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Integer getIbu_id() {
        return ibu_id;
    }

    public void setIbu_id(Integer ibu_id) {
        this.ibu_id = ibu_id;
    }

    public String getIbu() {
        return ibu;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public String getAyah() {
        return ayah;
    }

    public void setAyah(String ayah) {
        this.ayah = ayah;
    }

    public Integer getPosyandu_id() {
        return posyandu_id;
    }

    public void setPosyandu_id(Integer posyandu_id) {
        this.posyandu_id = posyandu_id;
    }

    public String getPosyandu() {
        return posyandu;
    }

    public void setPosyandu(String posyandu) {
        this.posyandu = posyandu;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getEncodedphoto() {
        return encodedphoto;
    }

    public void setEncodedphoto(String encodedphoto) {
        this.encodedphoto = encodedphoto;
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

    public BalitaModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.nama);
        dest.writeString(this.tanggal_lahir);
        dest.writeString(this.alamat);
        dest.writeValue(this.ibu_id);
        dest.writeString(this.ibu);
        dest.writeString(this.ayah);
        dest.writeValue(this.posyandu_id);
        dest.writeString(this.posyandu);
        dest.writeValue(this.creator_id);
        dest.writeString(this.petugas);
        dest.writeString(this.gender);
        dest.writeValue(this.active);
        dest.writeString(this.photo);
        dest.writeString(this.encodedphoto);
        dest.writeString(this.email);
        dest.writeString(this.berat);
        dest.writeString(this.tinggi);
        dest.writeString(this.lingkar_kepala);
    }

    protected BalitaModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.nama = in.readString();
        this.tanggal_lahir = in.readString();
        this.alamat = in.readString();
        this.ibu_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.ibu = in.readString();
        this.ayah = in.readString();
        this.posyandu_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.posyandu = in.readString();
        this.creator_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.petugas = in.readString();
        this.gender = in.readString();
        this.active = (Integer) in.readValue(Integer.class.getClassLoader());
        this.photo = in.readString();
        this.encodedphoto = in.readString();
        this.email = in.readString();
        this.berat = in.readString();
        this.tinggi = in.readString();
        this.lingkar_kepala = in.readString();
    }

    public static final Creator<BalitaModel> CREATOR = new Creator<BalitaModel>() {
        @Override
        public BalitaModel createFromParcel(Parcel source) {
            return new BalitaModel(source);
        }

        @Override
        public BalitaModel[] newArray(int size) {
            return new BalitaModel[size];
        }
    };
}
