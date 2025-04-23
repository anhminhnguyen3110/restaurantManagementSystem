package com.restaurant.views;

import javax.swing.*;
import java.awt.*;

public abstract class BaseApp extends JFrame {
    private final BaseApp prevApp;

    public BaseApp(String title, BaseApp prevApp) {
        super(title);
        this.prevApp = prevApp;
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
    }

    protected void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

    public BaseApp getPrevApp() {
        return prevApp;
    }

    public void goBack() {
        if (prevApp != null) {
            setVisible(false);
            prevApp.setVisible(true);
        }
    }
}