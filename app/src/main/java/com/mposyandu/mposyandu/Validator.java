package com.mposyandu.mposyandu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private final String emailvalidator = "([a-zA-Z0-9\\.\\_\\-]*)(\\@)([a-zA-Z0-9\\.\\_\\-]*$)";
    private final String alamatvalidator = "^([a-zA-Z]*\\ [a-zA-Z0-9]*)+(\\-[0-9]*$)";
    private final Pattern alamatpattern = Pattern.compile(alamatvalidator);
    private final Pattern emailpattern = Pattern.compile(emailvalidator);
    private Matcher matcher;

    public boolean Posyandu(String posyandu) {
        return posyandu.length() > 6;
    }

    public boolean Berat(String berat) {
        return berat.length() > 0;
    }

    public boolean Tinggi(String tinggi) {
        return tinggi.length() > 0;
    }

    public boolean Date(String date) {
        return date.length() > 0;
    }



    public boolean Nama(String nama) {
        return nama.length() > 3;
    }

    public boolean Email(String Email) {
        matcher = emailpattern.matcher(Email);
        return matcher.matches();
    }

    public boolean Pass(String pass) {
        return pass.length() > 5;
    }

    public boolean Alamat(String alamat) {
        matcher = alamatpattern.matcher(alamat);
        return matcher.matches();
    }

    public boolean Telepon(String telepon) {
        return telepon.length() >= 11 && telepon.length() <= 13;
    }

    public boolean Imunisasi(String imun) {
        return imun.length() > 2;
    }
}
