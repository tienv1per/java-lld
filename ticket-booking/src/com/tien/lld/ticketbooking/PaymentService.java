package com.tien.lld.ticketbooking;

import com.tien.lld.ticketbooking.entities.User;
import com.tien.lld.ticketbooking.enums.PaymentStatus;

public final class PaymentService {
    private static final String PAYMENT_FAIL_USER_ID = "payment-fail-user";

    public PaymentStatus pay(User user, double amount) {
        if (user == null || amount <= 0) {
            return PaymentStatus.FAILED;
        }
        if (PAYMENT_FAIL_USER_ID.equals(user.getId())) {
            return PaymentStatus.FAILED;
        }
        return PaymentStatus.SUCCESS;
    }
}

