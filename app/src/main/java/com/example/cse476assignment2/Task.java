package com.example.cse476assignment2;

import android.graphics.Bitmap;

import java.io.Serializable;
// Simple tasks class to handle tasks
public class Task implements Serializable {
    private String name;
    private int points;
    private boolean photoRequired;
    private transient Bitmap imageBitmap;

    public Task(String name, int points, boolean photoRequired) {
        this.name = name;
        this.points = points;
        this.photoRequired = photoRequired;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public boolean getPhotoRequired() { return photoRequired; }

    public Bitmap getImageBitmap() { return imageBitmap; }

    public void setImageBitmap(Bitmap imageBitmap) { this.imageBitmap = imageBitmap; }
}