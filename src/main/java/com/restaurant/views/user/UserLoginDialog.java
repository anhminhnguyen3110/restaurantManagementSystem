package com.restaurant.views.user;

import com.restaurant.controllers.UserController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.user.LoginUserDto;
import com.restaurant.events.ErrorEvent;
import com.restaurant.models.User;
import com.restaurant.pubsub.ErrorPubSubService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class UserLoginDialog extends JDialog {
    private final UserController userController = Injector.getInstance().getInstance(UserController.class);
    private final Consumer<User> onLoginSuccess;
    private final JTextField txtUsername = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final JButton btnLogin = new JButton("Login");

    public UserLoginDialog(Frame owner, Consumer<User> onLoginSuccess) {
        super(owner, "Login", true);
        this.onLoginSuccess = onLoginSuccess;
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        form.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        form.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        form.add(btnLogin, gbc);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        btnLogin.addActionListener(e -> onLogin());
        getRootPane().setDefaultButton(btnLogin);
        pack();
        setLocationRelativeTo(owner);
    }

    private void onLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();
        LoginUserDto dto = new LoginUserDto();
        dto.setUsername(u);
        dto.setPassword(p);
        User user = userController.login(dto);
        if (user != null) {
            onLoginSuccess.accept(user);
            dispose();
        } else {
            ErrorPubSubService.getInstance().publish(new ErrorEvent("Login failed: invalid credentials"));
            txtPassword.setText("");
        }
    }
}
