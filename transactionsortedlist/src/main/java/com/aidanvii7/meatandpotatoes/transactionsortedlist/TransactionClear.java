package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
class TransactionClear implements Transaction {

    @NonNull
    @Override
    public Transaction transform(@NonNull @lombok.NonNull TransformFunc transformFunc) {
        return this;
    }

    @Override
    public void applyTransaction(@NonNull @lombok.NonNull TransactionSortedList enhancedSortedList) {
        enhancedSortedList.clear();
    }
}
