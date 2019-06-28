package com.example.light;

public class lightclass{
    private String key, satellite , vis_median ;
    private int year , month , count ;

    public lightclass(String key, String satellite, String vis_median, int year, int month, int count) {
        this.key = key;
        this.satellite = satellite;
        this.vis_median = vis_median;
        this.year = year;
        this.month = month;
        this.count = count;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSatellite(String satellite) {
        this.satellite = satellite;
    }

    public void setVis_median(String vis_median) {
        this.vis_median = vis_median;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public String getSatellite() {
        return satellite;
    }

    public String getVis_median() {
        return vis_median;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getCount() {
        return count;
    }

}

