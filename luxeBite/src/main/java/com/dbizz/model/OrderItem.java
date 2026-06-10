package com.dbizz.model;

import java.time.LocalDateTime;

import com.dbizz.util.IDCheck;
import com.dbizz.util.NameUtil;
import com.dbizz.util.PhoneNoUtil;

public record OrderItem(int id, int orderId, int productId, double unitPrice, int quantity, LocalDateTime createdAt)
        implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        OrderItem rhs = (OrderItem) obj;
        return id == rhs.id()
                && orderId == rhs.orderId()
                && productId == rhs.productId()
                && unitPrice == rhs.unitPrice()
                && quantity == rhs.quantity();
    }
}
