package org.project.order.kafka;

import org.project.order.customer.CustomerResponse;
import org.project.order.order.PaymentMethod;
import org.project.order.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
