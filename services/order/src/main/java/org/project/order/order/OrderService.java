package org.project.order.order;

import lombok.RequiredArgsConstructor;
import org.project.order.customer.CustomerClient;
import org.project.order.exception.BusinessException;
import org.project.order.orderline.OrderLineRequest;
import org.project.order.orderline.OrderLineService;
import org.project.order.product.ProductClient;
import org.project.order.product.PurchaseRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;

    public Integer createOrder(OrderRequest request) {
        // check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        // purchase the product --> product-microservice (RestTemplate)
        this.productClient.purchaseProduct(request.products());

        // persist order
        var order = this.repository.save(mapper.toOrder(request));

        // persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        // start payment process

        // send the order confirmation --> notifications-microservice (kafka)
        return null;
    }
}
