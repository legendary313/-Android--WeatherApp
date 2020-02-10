package com.example.weatherapp;

public class BgImage {
    private static BgImage instance = new BgImage( );
    private BgImage(){ }

    protected int getImageName() {
        return imageName;
    }

    protected void setImageName(int imageName) {
        this.imageName = imageName;
    }

    private int imageName = R.drawable.bg_gradient;
    public static BgImage getInstance(){
        if(instance == null){
            instance = new BgImage();
        }
        return instance;
    }

}

