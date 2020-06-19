package com.mposyandu.mposyandu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    private Integer id;
    private String email;
    private String nama;
    private String telepon;
    private Integer role_id;
    private Integer posyandu_id;
    private String photo;
    private String alamat;
    private Integer creator_id;
    private String posyandu;
    private Integer active;
    private String token;
    private String firebase_token;
    private Integer verified;
    private String verification_code;

    public Integer getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(Integer creator_id) {
        this.creator_id = creator_id;
    }

    public Integer getId() {
        return id;
    }

    public String getPosyandu() {
        return posyandu;
    }

    public void setPosyandu(String posyandu) {
        this.posyandu = posyandu;
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

    public Integer getVerified() {
        return verified;
    }

    public void setVerified(Integer verified) {
        this.verified = verified;
    }

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }

    public UserModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.email);
        dest.writeString(this.nama);
        dest.writeString(this.telepon);
        dest.writeValue(this.role_id);
        dest.writeValue(this.posyandu_id);
        dest.writeString(this.photo);
        dest.writeString(this.alamat);
        dest.writeValue(this.creator_id);
        dest.writeString(this.posyandu);
        dest.writeValue(this.active);
        dest.writeString(this.token);
        dest.writeString(this.firebase_token);
        dest.writeValue(this.verified);
        dest.writeString(this.verification_code);
    }

    protected UserModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.email = in.readString();
        this.nama = in.readString();
        this.telepon = in.readString();
        this.role_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.posyandu_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.photo = in.readString();
        this.alamat = in.readString();
        this.creator_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.posyandu = in.readString();
        this.active = (Integer) in.readValue(Integer.class.getClassLoader());
        this.token = in.readString();
        this.firebase_token = in.readString();
        this.verified = (Integer) in.readValue(Integer.class.getClassLoader());
        this.verification_code = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}
