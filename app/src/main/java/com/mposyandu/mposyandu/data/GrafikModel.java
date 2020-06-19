package com.mposyandu.mposyandu.data;

public class GrafikModel {
    private Integer usia;
    private Double berat;

    public Integer getUsia() {
        return usia;
    }

    public void setUsia(Integer usia) {
        this.usia = usia;
    }

    public Double getBerat() {
        return berat;
    }

    public void setBerat(Double berat) {
        this.berat = berat;
    }

    @Override
    public String toString() {
        return "["+usia+","+berat+"]";
    }
}
