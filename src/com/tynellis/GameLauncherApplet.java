package com.tynellis;

import java.applet.Applet;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLauncherApplet extends Applet implements ActionListener {

    public void init() {
        Button button = new Button("Launch Game");
        this.add(button);
        button.addActionListener(this);
    }

    public void destroy() {
        if (GameComponent.active != null) {
            GameComponent.active.Quit();
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        GameComponent.main(new String[0]);
    }
}
