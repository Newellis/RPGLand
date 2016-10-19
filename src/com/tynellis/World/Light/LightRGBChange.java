package com.tynellis.World.Light;

import java.awt.Color;

public class LightRGBChange {
    private double R, B, G;

    public LightRGBChange(double R, double B, double G) {
        this.R = R;
        this.B = B;
        this.G = G;
    }

    public Color changeColor(Color color) {
        int Red = color.getRed();
        int Green = color.getGreen();
        int Blue = color.getBlue();

        return new Color((int) (Red + ((255 - Red) * R)), (int) (Green + ((255 - Green) * G)), (int) (Blue + ((255 - Blue) * B)));
    }
}
