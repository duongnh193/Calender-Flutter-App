package com.duong.lichvanien.affiliate.enums;

/**
 * Withdrawal request status enumeration.
 */
public enum WithdrawalStatus {
    /**
     * Pending approval.
     */
    PENDING,
    
    /**
     * Approved by admin.
     */
    APPROVED,
    
    /**
     * Rejected by admin.
     */
    REJECTED,
    
    /**
     * Completed (money transferred).
     */
    COMPLETED
}

