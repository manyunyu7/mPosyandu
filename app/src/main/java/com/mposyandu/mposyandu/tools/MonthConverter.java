package com.mposyandu.mposyandu.tools;

public class MonthConverter {
    private Integer month;
    private String[] Month = new String[] {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September","Oktober", "November", "Desember"};
    public MonthConverter(Integer month) {
        this.month = month;
    }

    public String getMonth() {
        return Month[month];
    }
}
