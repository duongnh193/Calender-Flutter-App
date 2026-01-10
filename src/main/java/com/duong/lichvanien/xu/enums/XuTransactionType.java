package com.duong.lichvanien.xu.enums;

/**
 * Xu transaction type enumeration.
 */
public enum XuTransactionType {
    /**
     * Deposit xu (from payment).
     */
    DEPOSIT,
    
    /**
     * Purchase content (spend xu).
     */
    PURCHASE,
    
    /**
     * Referral bonus (free xu after 3 referrals).
     */
    REFERRAL_BONUS,
    
    /**
     * Affiliate commission (30% from referrals).
     */
    AFFILIATE_COMMISSION,
    
    /**
     * Withdraw xu (affiliate withdrawal).
     */
    WITHDRAW,
    
    /**
     * Refund xu.
     */
    REFUND
}

