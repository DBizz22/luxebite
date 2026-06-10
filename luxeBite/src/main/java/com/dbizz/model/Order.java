package com.dbizz.model;

import java.time.LocalDateTime;
import com.dbizz.util.IDCheck;
import com.dbizz.util.PhoneNoUtil;
import com.dbizz.util.TimeUtils;

public record Order(int id, int userId, PaymentStatus status, LocalDateTime createdAt) implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        return id == other.id &&
                userId == other.userId &&
                status == other.status;
    }
}
