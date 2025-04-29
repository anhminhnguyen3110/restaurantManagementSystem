package com.restaurant.views.payment;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.controllers.OrderController;
import com.restaurant.controllers.PaymentController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.payment.CreatePaymentDto;
import com.restaurant.models.Order;
import com.restaurant.utils.listeners.SimpleDocumentListener;
import com.restaurant.utils.validators.PaymentInputValidator;

import javax.swing.*;
import java.awt.*;

public class PaymentFormDialog extends JDialog {
    private final JComboBox<PaymentMethod> cbMethod = new JComboBox<>(PaymentMethod.values());
    private final JTextField txtPay = new JTextField(10);
    private final JLabel lblChange = new JLabel("Change: 0.00");
    private final JButton btnSave = new JButton("Pay"), btnCancel = new JButton("Cancel");

    private final Order order;
    private final PaymentController paymentController;
    private final Runnable onSaved;

    public PaymentFormDialog(Frame owner, int orderId, Runnable onSaved) {
        super(owner, "Payment for Order #" + orderId, true);
        this.order = Injector.getInstance().getInstance(OrderController.class).getOrder(orderId);
        System.out.println("Order: " + order.getTotalPrice());
        this.onSaved = onSaved;
        this.paymentController = Injector.getInstance().getInstance(PaymentController.class);

        buildUI();
        pack();
        setLocationRelativeTo(owner);

        cbMethod.addActionListener(e -> updateFields());
        txtPay.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                calculateChange();
            }
        });

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        updateFields();
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Method:"));
        form.add(cbMethod);
        form.add(new JLabel("Amount:"));
        form.add(txtPay);
        form.add(new JLabel("Payable Amount:"));
        form.add(new JLabel(String.format("%.2f", order.getTotalPrice())));
        form.add(lblChange);
        form.add(new JLabel());

        JPanel buttons = new JPanel();
        buttons.add(btnSave);
        buttons.add(btnCancel);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void updateFields() {
        boolean isCash = ((PaymentMethod) cbMethod.getSelectedItem()) == PaymentMethod.CASH;
        lblChange.setVisible(isCash);
        calculateChange();
    }

    private void calculateChange() {
        try {
            double total = order.getTotalPrice();
            double pay = Double.parseDouble(txtPay.getText());
            double change = pay - total;
            lblChange.setText(String.format("Change: %.2f", Math.max(0, change)));
        } catch (Exception e) {
            lblChange.setText("Change: 0.00");
        }
    }

    private void onSave() {
        PaymentMethod method = (PaymentMethod) cbMethod.getSelectedItem();
        double pay;
        try {
            pay = Double.parseDouble(txtPay.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var errors = PaymentInputValidator.validate(order.getTotalPrice(), method, pay);
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setOrderId(order.getId());
        dto.setMethod(method);
        dto.setUserPayAmount(pay);
        if (method == PaymentMethod.CASH) {
            dto.setChangeAmount(pay - order.getTotalPrice());
        }
        paymentController.createPayment(dto);

        onSaved.run();
        dispose();
    }
}