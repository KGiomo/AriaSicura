package com.example.ariasicuraprogetto;

public class Tips {

    private String title;
    private String text;
    private int icon;

    public Tips(String title, String text, int icon){
        this.title = title;
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
