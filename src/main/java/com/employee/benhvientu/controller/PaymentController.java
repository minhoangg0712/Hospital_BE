package com.employee.benhvientu.controller;

import com.employee.benhvientu.entity.Order;
import com.employee.benhvientu.entity.Payment;
import com.employee.benhvientu.repository.OrderRepository;
import com.employee.benhvientu.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PayOS payOS;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentController(PayOS payOS, OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.payOS = payOS;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/payos_transfer_handler")
    @Transactional
    public ObjectNode payosTransferHandler(@RequestBody ObjectNode body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);

        try {
            WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);
            Order order = orderRepository.findById(Math.toIntExact(data.getOrderCode()));
            Payment payment = paymentRepository.findByTransactionId(order.getPayosOrderCode());

            order.setStatus("Completed");
            payment.setPaymentStatus("Completed");

            paymentRepository.save(payment);
            orderRepository.save(order);

            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", null);
            WebhookData webhookData = payOS.verifyPaymentWebhookData(webhookBody);
            System.out.println(webhookData);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }
}
