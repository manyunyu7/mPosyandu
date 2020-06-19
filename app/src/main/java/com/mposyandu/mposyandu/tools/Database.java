package com.mposyandu.mposyandu.tools;

public class Database {
//    private static final String url = "https://mposyandu.id";

    //Link Sementara Buat Database
    public static final String baseURL="  https://sibima.badanmentoring.org/";
    private static final String url = baseURL+"mposyandu";

    //link sementara untuk API
    public static final String getQRBalita=baseURL+"new_API/getQRBalita.php";
    public static final String scanQRBalita=baseURL+"new_API/scanQR.php";

    public static final String urlAPI = "https://sibima.badanmentoring.org/mposyandu/API_new/";

    private static final String notif = "https://fcm.googleapis.com/fcm/send";

    public static String getNotif() {
        return notif;
    }
    public static String getUrl() {
        return url;
    }

}
