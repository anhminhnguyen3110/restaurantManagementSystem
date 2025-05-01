package com.restaurant.views.user;

import com.restaurant.constants.UserRole;
import com.restaurant.controllers.UserController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.user.GetUserDto;
import com.restaurant.models.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class UserListView extends JPanel {
    private static final String[] COLUMNS = {"ID", "Username", "Name", "Email", "Role", "Active"};
    private final UserController userController;
    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField txtName = new JTextField(10);
    private final JTextField txtUsername = new JTextField(10);
    private final JTextField txtEmail = new JTextField(10);
    private final JComboBox<UserRole> cmbRole = new JComboBox<>();
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");
    private final GetUserDto currentDto = new GetUserDto();

    public UserListView() {
        userController = Injector.getInstance().getInstance(UserController.class);
        setLayout(new BorderLayout(10, 10));

        JPanel filters = new JPanel();
        filters.add(new JLabel("Name:"));
        filters.add(txtName);
        filters.add(new JLabel("Username:"));
        filters.add(txtUsername);
        filters.add(new JLabel("Email:"));
        filters.add(txtEmail);
        filters.add(new JLabel("Role:"));
        cmbRole.addItem(null);
        for (UserRole r : UserRole.values()) cmbRole.addItem(r);
        filters.add(cmbRole);
        JButton btnReset = new JButton("Reset");
        filters.add(btnReset);
        JButton btnAdd = new JButton("Add");
        filters.add(btnAdd);
        JButton btnDel = new JButton("Delete");
        filters.add(btnDel);
        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                setBackground(sel ? tbl.getSelectionBackground() : (r % 2 == 0 ? Color.WHITE : new Color(245, 245, 245)));
                return this;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
        add(paging, BorderLayout.SOUTH);

        txtName.getDocument().addDocumentListener(new FilterListener());
        txtUsername.getDocument().addDocumentListener(new FilterListener());
        txtEmail.getDocument().addDocumentListener(new FilterListener());
        cmbRole.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) applyFilters();
        });

        btnReset.addActionListener(e -> {
            resetFilters();
            loadData();
        });
        btnAdd.addActionListener(e -> openForm(null));
        btnDel.addActionListener(e -> deleteSelected());

        btnPrev.addActionListener(e -> {
            if (currentDto.getPage() > 0) {
                currentDto.setPage(currentDto.getPage() - 1);
                loadData();
            }
        });
        btnNext.addActionListener(e -> {
            currentDto.setPage(currentDto.getPage() + 1);
            loadData();
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        resetFilters();
    }

    private void applyFilters() {
        String n = txtName.getText().trim();
        currentDto.setName(n.isEmpty() ? null : n);
        String u = txtUsername.getText().trim();
        currentDto.setUsername(u.isEmpty() ? null : u);
        String em = txtEmail.getText().trim();
        currentDto.setEmail(em.isEmpty() ? null : em);
        currentDto.setRole((UserRole) cmbRole.getSelectedItem());
        currentDto.setPage(0);
        loadData();
    }

    private void resetFilters() {
        txtName.setText("");
        txtUsername.setText("");
        txtEmail.setText("");
        cmbRole.setSelectedIndex(0);
        currentDto.setName(null);
        currentDto.setUsername(null);
        currentDto.setEmail(null);
        currentDto.setRole(null);
        currentDto.setSortBy("id");
        currentDto.setSortDir("desc");
        currentDto.setPage(0);
    }

    public void loadData() {
        model.setRowCount(0);
        List<User> list = userController.findUsers(currentDto);
        for (User u : list) {
            model.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getName(),
                    u.getEmail(),
                    u.getRole(),
                    u.isActive()
            });
        }
        btnPrev.setEnabled(currentDto.getPage() > 0);
        btnNext.setEnabled(list.size() == currentDto.getSize());
    }

    private void openForm(User u) {
        UserFormDialog dlg = new UserFormDialog((Frame) SwingUtilities.getWindowAncestor(this), u, this::loadData);
        dlg.setVisible(true);
    }

    private void editSelected() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(r, 0);
        User u = userController.getUser(id);
        openForm(u);
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete user #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            userController.deleteUser(id);
            loadData();
        }
    }

    private class FilterListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            applyFilters();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            applyFilters();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            applyFilters();
        }
    }
}
