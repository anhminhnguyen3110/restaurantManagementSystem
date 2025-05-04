package com.restaurant.views;

import com.restaurant.events.ErrorEvent;
import com.restaurant.models.User;
import com.restaurant.pubsub.ErrorPubSubService;
import com.restaurant.views.user.UserLoginDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainView extends JFrame {
    private static MainView currentInstance;

    private MainView() {
        super("Restaurant Admin");
        initLookAndFeel();
        ErrorPubSubService.getInstance().subscribe(ErrorEvent.class, event ->
                SwingUtilities.invokeLater(() -> {
                    if (currentInstance != null) {
                        JOptionPane.showMessageDialog(null, event.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                })
        );
    }

    public static void launch() {
        if (currentInstance != null) {
            currentInstance.dispose();
        }
        currentInstance = new MainView();
        SwingUtilities.invokeLater(() -> currentInstance.setVisible(true));
        SwingUtilities.invokeLater(() -> {
            UserLoginDialog login = new UserLoginDialog(
                    currentInstance,
                    user -> SwingUtilities.invokeLater(() -> currentInstance.showDashboard(user))
            );
            login.setVisible(true);
        });
    }

    private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private void showDashboard(User user) {
        getContentPane().removeAll();
        JTabbedPane tabs = new JTabbedPane();
        List<ViewTab> tabsForRole = ListViewFactory.getTabsForRole(user.getRole());
        for (ViewTab vt : tabsForRole) {
            tabs.addTab(vt.title(), vt.view());
        }
        tabs.addTab("My Profile", createProfilePanel(user));
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c instanceof LoadableView lv) {
                lv.loadData();
            }
        });
        getContentPane().add(tabs, BorderLayout.CENTER);
        revalidate();
        repaint();
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        String rn = user.getRole().toString().charAt(0) + user.getRole().toString().substring(1).toLowerCase();
        setTitle(rn + " Dashboard");
    }

    private JPanel createProfilePanel(User user) {
        JPanel detailTab = new JPanel(new GridBagLayout());
        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.insets = new Insets(10, 10, 10, 10);
        dgbc.anchor = GridBagConstraints.CENTER;
        JPanel smallPanel = new JPanel();
        smallPanel.setLayout(new BoxLayout(smallPanel, BoxLayout.Y_AXIS));
        smallPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("My Profile"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        smallPanel.setMaximumSize(new Dimension(300, 180));
        JPanel info = new JPanel(new GridLayout(4, 2, 5, 5));
        info.add(new JLabel("Username:"));
        info.add(new JLabel(user.getUsername()));
        info.add(new JLabel("Name:"));
        info.add(new JLabel(user.getName()));
        info.add(new JLabel("Email:"));
        info.add(new JLabel(user.getEmail()));
        info.add(new JLabel("Role:"));
        info.add(new JLabel(user.getRole().toString()));
        smallPanel.add(info);
        smallPanel.add(Box.createVerticalStrut(10));
        JButton btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            dispose();
            launch();
        });
        smallPanel.add(btnLogout);
        detailTab.add(smallPanel, dgbc);
        return detailTab;
    }
}