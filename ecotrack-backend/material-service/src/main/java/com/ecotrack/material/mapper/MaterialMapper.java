package com.ecotrack.material.mapper;

import com.ecotrack.material.dto.response.*;
import com.ecotrack.material.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public final class MaterialMapper {

    private MaterialMapper() {
    }

    public static MaterialCategoryResponse toResponse(MaterialCategory category) {
        return MaterialCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.isActive())
                .build();
    }

    public static MaterialResponse toResponse(Material material, List<String> imageUrls) {
        return MaterialResponse.builder()
                .id(material.getId())
                .recyclerId(material.getRecyclerId())
                .categoryId(material.getCategory().getId())
                .categoryName(material.getCategory().getName())
                .materialName(material.getMaterialName())
                .description(material.getDescription())
                .purity(material.getPurity())
                .quantity(material.getQuantity())
                .unit(material.getUnit())
                .pricePerUnit(material.getPricePerUnit())
                .warehouseLocation(material.getWarehouseLocation())
                .availabilityStatus(material.getAvailabilityStatus())
                .imageUrls(imageUrls)
                .createdAt(material.getCreatedAt())
                .build();
    }

    public static OrderItemResponse toResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .materialId(item.getMaterialId())
                .materialName(item.getMaterialName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public static PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .transactionNumber(payment.getTransactionNumber())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paidAmount(payment.getPaidAmount())
                .paymentDate(payment.getPaymentDate())
                .remarks(payment.getRemarks())
                .build();
    }

    public static OrderResponse toResponse(Order order, List<OrderItem> items, Payment payment) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .industryId(order.getIndustryId())
                .recyclerId(order.getRecyclerId())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .items(items.stream().map(MaterialMapper::toResponse).collect(Collectors.toList()))
                .payment(payment != null ? toResponse(payment) : null)
                .orderDate(order.getOrderDate())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public static EcoPointsWalletResponse toResponse(EcoPointsWallet wallet) {
        return EcoPointsWalletResponse.builder()
                .citizenId(wallet.getCitizenId())
                .currentBalance(wallet.getCurrentBalance())
                .totalEarned(wallet.getTotalEarned())
                .totalRedeemed(wallet.getTotalRedeemed())
                .build();
    }

    public static EcoPointTransactionResponse toResponse(EcoPointTransaction transaction) {
        return EcoPointTransactionResponse.builder()
                .points(transaction.getPoints())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}
