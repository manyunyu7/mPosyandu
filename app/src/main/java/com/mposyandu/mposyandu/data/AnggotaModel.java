package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnggotaModel implements Parcelable {

    private Integer id;
    private Integer active;

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("telepon")
    @Expose
    private String telepon;
    @SerializedName("role_id")
    @Expose
    private Integer role_id;
    @SerializedName("posyandu_id")
    @Expose
    private Integer posyandu_id;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("encodedphoto")
    @Expose
    private String encodedphoto;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("creator_id")
    @Expose
    private Integer creator_id;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("firebase_token")
    @Expose
    private String firebase_token;

    public Integer getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(Integer creator_id) {
        this.creator_id = creator_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public Integer getPosyandu_id() {
        return posyandu_id;
    }

    public void setPosyandu_id(Integer posyandu_id) {
        this.posyandu_id = posyandu_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirebase_token() {
        return firebase_token;
    }

    public void setFirebase_token(String firebase_token) {
        this.firebase_token = firebase_token;
    }

    public String getEncodedphoto() {
        return encodedphoto;
    }

    public void setEncodedphoto(String encodedphoto) {
        this.encodedphoto = encodedphoto;
    }

    public AnggotaModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.nama);
        dest.writeString(this.telepon);
        dest.writeValue(this.role_id);
        dest.writeValue(this.posyandu_id);
        dest.writeString(this.photo);
        dest.writeString(this.encodedphoto);
        dest.writeString(this.alamat);
        dest.writeValue(this.creator_id);
        dest.writeValue(this.active);
        dest.writeString(this.token);
        dest.writeString(this.firebase_token);
    }

    protected AnggotaModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.email = in.readString();
        this.password = in.readString();
        this.nama = in.readString();
        this.telepon = in.readString();
        this.role_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.posyandu_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.photo = in.readString();
        this.encodedphoto = in.readString();
        this.alamat = in.readString();
        this.creator_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.active = (Integer) in.readValue(Integer.class.getClassLoader());
        this.token = in.readString();
        this.firebase_token = in.readString();
    }

    public static final Creator<AnggotaModel> CREATOR = new Creator<AnggotaModel>() {
        @Override
        public AnggotaModel createFromParcel(Parcel source) {
            return new AnggotaModel(source);
        }

        @Override
        public AnggotaModel[] newArray(int size) {
            return new AnggotaModel[size];
        }
    };

    @Override
    public String toString() {
        return "AnggotaModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nama='" + nama + '\'' +
                ", telepon='" + telepon + '\'' +
                ", role_id=" + role_id +
                ", posyandu_id=" + posyandu_id +
                ", photo='" + photo + '\'' +
                ", encodedphoto='" + encodedphoto + '\'' +
                ", alamat='" + alamat + '\'' +
                ", creator_id=" + creator_id +
                ", active=" + active +
                ", token='" + token + '\'' +
                ", firebase_token='" + firebase_token + '\'' +
                '}';
    }
}
