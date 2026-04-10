package com.andi.carikopi.feature.payment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andi.carikopi.feature.coffeeshop.CoffeeShop;
import com.andi.carikopi.feature.order.Order;
import com.andi.carikopi.feature.payment.dto.XenditPaymentRequest;
import com.xendit.Xendit;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;
import com.xendit.model.QRCode;

@Service
public class PaymentService {

    @Autowired private XenditPaymentRepository xenditPaymentRepository;

    public XenditPayment createPayment(Order order, XenditPaymentRequest request) {
        XenditPayment payment = xenditPaymentRepository.findByOrderId(order.getId())
                .orElse(new XenditPayment());
        
        payment.setOrder(order);
        payment.setExternalId(request.getExternalId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod("QRIS");
        payment.setStatus("UNPAID");
        payment.setDescription(request.getDescription());
        
        return xenditPaymentRepository.save(payment);
    }

    public String createInvoice(CoffeeShop shop, Order order) {
        
        Xendit.apiKey = shop.getXenditApiKey();

        Map<String, Object> params = new HashMap<>();
        String externalId = "INV-" + shop.getId() + "-" + System.currentTimeMillis();
        params.put("external_id", externalId);
        params.put("amount", order.getTotalPrice());
        String description = "Pesanan Antrean #" + order.getQueueNumber() + " di " + shop.getNamaToko();
        params.put("description", description);
        params.put("success_redirect_url", "https://fifoti.com/success");

        XenditPaymentRequest request = XenditPaymentRequest.builder()
                .externalId(externalId)
                .amount(order.getTotalPrice())
                .description(description)
                .build();
        createPayment(order, request);
        
        try {
            Invoice invoice = Invoice.create(params);
            return invoice.getInvoiceUrl();
        } catch (XenditException e) {
            throw new RuntimeException("Failed to create Xendit invoice: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> createQRCode(CoffeeShop shop, Order order) {
        Xendit.apiKey = shop.getXenditApiKey();

        String externalId = "QR-" + shop.getId() + "-" + order.getId();
        String description = "Pesanan Antrean #" + order.getQueueNumber() + " di " + shop.getNamaToko();

        XenditPaymentRequest request = XenditPaymentRequest.builder()
                .externalId(externalId)
                .amount(order.getTotalPrice())
                .description(description)
                .build();
        createPayment(order, request);

        try {
            QRCode qrCode = QRCode.createQRCode(
                externalId,
                QRCode.QRCodeType.DYNAMIC,
                "IDR",
                order.getTotalPrice()
            );
            // return QR string and id
            Map<String, Object> response = new HashMap<>();
            response.put("qr_string", qrCode.getQrString());
            response.put("id", qrCode.getId());
            return response;
        } catch (XenditException e) {
            throw new RuntimeException("Failed to create Xendit QR Code: " + e.getMessage(), e);
        }
    }
}
