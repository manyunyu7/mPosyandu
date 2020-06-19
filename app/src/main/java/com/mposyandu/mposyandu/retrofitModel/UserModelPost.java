package com.mposyandu.mposyandu.retrofitModel;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModelPost implements Serializable, Parcelable
{
    private Integer id;

    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("role_id")
    @Expose
    private String roleId;
    @SerializedName("telepon")
    @Expose
    private String telepon;
    @SerializedName("creator_id")
    @Expose
    private String creatorId;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("posyandu_id")
    @Expose
    private String posyanduId;
    @SerializedName("encodedphoto")
    @Expose
    private String encodedphoto;
    @SerializedName("firebase_token")
    @Expose
    private String firebaseToken;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("active")
    @Expose
    private Integer active;


    public final static Parcelable.Creator<UserModelPost> CREATOR = new Creator<UserModelPost>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UserModelPost createFromParcel(Parcel in) {
            return new UserModelPost(in);
        }

        public UserModelPost[] newArray(int size) {
            return (new UserModelPost[size]);
        }

    };

    private final static long serialVersionUID = -1947204812112656020L;

    protected UserModelPost(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.password = ((String) in.readValue((String.class.getClassLoader())));
        this.nama = ((String) in.readValue((String.class.getClassLoader())));
        this.roleId = ((String) in.readValue((String.class.getClassLoader())));
        this.telepon = ((String) in.readValue((String.class.getClassLoader())));
        this.creatorId = ((String) in.readValue((String.class.getClassLoader())));
        this.photo = ((String) in.readValue((String.class.getClassLoader())));
        this.posyanduId = ((String) in.readValue((String.class.getClassLoader())));
        this.encodedphoto = ((String) in.readValue((String.class.getClassLoader())));
        this.firebaseToken = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.alamat = ((String) in.readValue((String.class.getClassLoader())));
        this.token = ((String) in.readValue((String.class.getClassLoader())));
        this.active = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public UserModelPost() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPosyanduId() {
        return posyanduId;
    }

    public void setPosyanduId(String posyanduId) {
        this.posyanduId = posyanduId;
    }

    public String getEncodedphoto() {
        return encodedphoto;
    }

    public void setEncodedphoto(String encodedphoto) {
        this.encodedphoto = encodedphoto;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(password);
        dest.writeValue(nama);
        dest.writeValue(roleId);
        dest.writeValue(telepon);
        dest.writeValue(creatorId);
        dest.writeValue(photo);
        dest.writeValue(posyanduId);
        dest.writeValue(encodedphoto);
        dest.writeValue(firebaseToken);
        dest.writeValue(email);
        dest.writeValue(alamat);
        dest.writeValue(token);
        dest.writeString(String.valueOf(active));
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "UserModelPost{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", nama='" + nama + '\'' +
                ", roleId='" + roleId + '\'' +
                ", telepon='" + telepon + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", photo='" + photo + '\'' +
                ", posyanduId='" + posyanduId + '\'' +
                ", encodedphoto='" + encodedphoto + '\'' +
                ", firebaseToken='" + firebaseToken + '\'' +
                ", email='" + email + '\'' +
                ", alamat='" + alamat + '\'' +
                ", token='" + token + '\'' +
                ", active=" + active +
                '}';
    }
}