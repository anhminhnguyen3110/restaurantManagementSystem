package com.restaurant.views.user;

import com.restaurant.controllers.UserController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.UpdateUserDto;
import com.restaurant.models.User;
import com.restaurant.constants.UserRole;
import com.restaurant.utils.validators.UserInputValidator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserFormDialog extends JDialog {
    private final JTextField txtUsername = new JTextField(15);
    private final JTextField txtName = new JTextField(15);
    private final JTextField txtEmail = new JTextField(15);
    private final JPasswordField txtPwd = new JPasswordField(15);
    private final JCheckBox chkChangePwd = new JCheckBox("Change Password");
    private final JComboBox<UserRole> cmbRole = new JComboBox<>();
    private final JCheckBox chkActive = new JCheckBox("Active");
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");
    private final UserController userController;
    private final User existing;
    private final Runnable onSaved;

    public UserFormDialog(Frame owner, User existing, Runnable onSaved) {
        super(owner, existing == null ? "New User" : "Edit User", true);
        this.userController = Injector.getInstance().getInstance(UserController.class);
        this.existing = existing;
        this.onSaved = onSaved;

        if (existing == null) {
            for (UserRole r : UserRole.values()) {
                if (r != UserRole.OWNER) {
                    cmbRole.addItem(r);
                }
            }
        } else {
            for (UserRole r : UserRole.values()) {
                cmbRole.addItem(r);
            }
        }

        JPanel form;
        if (existing != null) {
            form = new JPanel(new GridLayout(7, 2, 5, 5));
            form.add(new JLabel("Username:")); form.add(txtUsername);
            form.add(new JLabel("Name:")); form.add(txtName);
            form.add(new JLabel("Email:")); form.add(txtEmail);
            form.add(new JLabel("Change Password:")); form.add(chkChangePwd);
            form.add(new JLabel("Password:")); form.add(txtPwd);
            form.add(new JLabel("Role:")); form.add(cmbRole);
            form.add(new JLabel("Active:")); form.add(chkActive);
        } else {
            form = new JPanel(new GridLayout(6, 2, 5, 5));
            form.add(new JLabel("Username:")); form.add(txtUsername);
            form.add(new JLabel("Name:")); form.add(txtName);
            form.add(new JLabel("Email:")); form.add(txtEmail);
            form.add(new JLabel("Password:")); form.add(txtPwd);
            form.add(new JLabel("Role:")); form.add(cmbRole);
            form.add(new JLabel("Active:")); form.add(chkActive);
        }

        JPanel buttons = new JPanel();
        buttons.add(btnSave); buttons.add(btnCancel);
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        if (existing != null) {
            txtUsername.setText(existing.getUsername());
            txtUsername.setEditable(false);
            txtName.setText(existing.getName());
            txtEmail.setText(existing.getEmail());
            cmbRole.setSelectedItem(existing.getRole());
            chkActive.setSelected(existing.isActive());
            chkChangePwd.setSelected(false);
            txtPwd.setEnabled(false);
            chkChangePwd.addActionListener(e -> txtPwd.setEnabled(chkChangePwd.isSelected()));
        } else {
            chkChangePwd.setVisible(false);
            txtPwd.setEnabled(true);
            chkActive.setSelected(true);
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void onSave() {
        String username = txtUsername.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String pwd = new String(txtPwd.getPassword()).trim();
        UserRole role = (UserRole) cmbRole.getSelectedItem();
        boolean active = chkActive.isSelected();

        if (existing == null) {
            CreateUserDto dto = new CreateUserDto();
            dto.setUsername(username);
            dto.setName(name);
            dto.setPassword(pwd);
            dto.setEmail(email);
            dto.setRole(role);
            List<String> errors = UserInputValidator.validate(dto);
            if (pwd.length() < 8) errors.add("• Password must be at least 8 characters.");
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            userController.createUser(dto);
        } else {
            UpdateUserDto dto = new UpdateUserDto();
            dto.setId(existing.getId());
            dto.setName(name);
            if (chkChangePwd.isSelected()) {
                if (pwd.length() < 8) {
                    JOptionPane.showMessageDialog(this, "• Password must be at least 8 characters.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dto.setPassword(pwd);
            }
            dto.setEmail(email);
            dto.setRole(role);
            dto.setActive(active);
            List<String> errors = UserInputValidator.validate(dto);
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            userController.updateUser(dto);
        }
        onSaved.run();
        dispose();
    }
}
