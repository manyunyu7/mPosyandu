package com.mposyandu.mposyandu.retrofitModel;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalitaModelPost implements Serializable, Parcelable
{

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("ibu_id")
    @Expose
    private String ibuId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("tanggal_lahir")
    @Expose
    private String tanggalLahir;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("ayah")
    @Expose
    private String ayah;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("encodedphoto")
    @Expose
    private String encodedphoto;
    @SerializedName("posyandu_id")
    @Expose
    private String posyanduId;
    @SerializedName("creator_id")
    @Expose
    private String creatorId;
    @SerializedName("berat")
    @Expose
    private String berat;
    @SerializedName("tinggi")
    @Expose
    private String tinggi;
    @SerializedName("lingkar_kepala")
    @Expose
    private String lingkarKepala;
    public final static Parcelable.Creator<BalitaModelPost> CREATOR = new Creator<BalitaModelPost>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BalitaModelPost createFromParcel(Parcel in) {
            return new BalitaModelPost(in);
        }

        public BalitaModelPost[] newArray(int size) {
            return (new BalitaModelPost[size]);
        }

    }
            ;
    private final static long serialVersionUID = -4906912134209830676L;

    protected BalitaModelPost(Parcel in) {
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.nama = ((String) in.readValue((String.class.getClassLoader())));
        this.ibuId = ((String) in.readValue((String.class.getClassLoader())));
        this.gender = ((String) in.readValue((String.class.getClassLoader())));
        this.tanggalLahir = ((String) in.readValue((String.class.getClassLoader())));
        this.alamat = ((String) in.readValue((String.class.getClassLoader())));
        this.ayah = ((String) in.readValue((String.class.getClassLoader())));
        this.photo = ((String) in.readValue((String.class.getClassLoader())));
        this.encodedphoto = ((String) in.readValue((String.class.getClassLoader())));
        this.posyanduId = ((String) in.readValue((String.class.getClassLoader())));
        this.creatorId = ((String) in.readValue((String.class.getClassLoader())));
        this.berat = ((String) in.readValue((String.class.getClassLoader())));
        this.tinggi = ((String) in.readValue((String.class.getClassLoader())));
        this.lingkarKepala = ((String) in.readValue((String.class.getClassLoader())));
    }

    public BalitaModelPost() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIbuId() {
        return ibuId;
    }

    public void setIbuId(String ibuId) {
        this.ibuId = ibuId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getAyah() {
        return ayah;
    }

    public void setAyah(String ayah) {
        this.ayah = ayah;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEncodedphoto() {
        return encodedphoto;
    }

    public void setEncodedphoto(String encodedphoto) {
        this.encodedphoto = encodedphoto;
    }

    public String getPosyanduId() {
        return posyanduId;
    }

    public void setPosyanduId(String posyanduId) {
        this.posyanduId = posyanduId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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

    public String getLingkarKepala() {
        return lingkarKepala;
    }

    public void setLingkarKepala(String lingkarKepala) {
        this.lingkarKepala = lingkarKepala;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(email);
        dest.writeValue(nama);
        dest.writeValue(ibuId);
        dest.writeValue(gender);
        dest.writeValue(tanggalLahir);
        dest.writeValue(alamat);
        dest.writeValue(ayah);
        dest.writeValue(photo);
        dest.writeValue(encodedphoto);
        dest.writeValue(posyanduId);
        dest.writeValue(creatorId);
        dest.writeValue(berat);
        dest.writeValue(tinggi);
        dest.writeValue(lingkarKepala);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "BalitaModelPost{" +
                "email='" + email + '\'' +
                ", nama='" + nama + '\'' +
                ", ibuId='" + ibuId + '\'' +
                ", gender='" + gender + '\'' +
                ", tanggalLahir='" + tanggalLahir + '\'' +
                ", alamat='" + alamat + '\'' +
                ", ayah='" + ayah + '\'' +
                ", photo='" + photo + '\'' +
                ", encodedphoto='" + encodedphoto + '\'' +
                ", posyanduId='" + posyanduId + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", berat='" + berat + '\'' +
                ", tinggi='" + tinggi + '\'' +
                ", lingkarKepala='" + lingkarKepala + '\'' +
                '}';
    }
}