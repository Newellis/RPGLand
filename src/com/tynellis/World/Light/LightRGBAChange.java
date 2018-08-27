package com.tynellis.World.Light;

import java.awt.Color;

public class LightRGBAChange {
    private double R, B, G, A;

    public LightRGBAChange(double R, double B, double G, double A) {
        this.R = R;
        this.B = B;
        this.G = G;
        this.A = A;
    }

    public Color changeColor(Color color) {
        int Red = color.getRed();
        int Green = color.getGreen();
        int Blue = color.getBlue();
        int Alpha = color.getAlpha();

        return new Color((int) (Red + ((255 - Red) * R)), (int) (Green + ((255 - Green) * G)), (int) (Blue + ((255 - Blue) * B)), (int) (Alpha + ((255 - Alpha) * A)));
    }
}
