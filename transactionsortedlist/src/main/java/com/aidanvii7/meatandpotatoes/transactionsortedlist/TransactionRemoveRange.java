package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
class TransactionRemoveRange<E> implements Transaction<E> {

    int rangeStart;
    int rangeEnd;

    public TransactionRemoveRange(int rangeStart, int rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    @NonNull
    @Override
    public <T> Transaction<T> transform(@NonNull @lombok.NonNull TransformFunc<? super E, ? extends T> transformFunc) {
        return new TransactionRemoveRange<>(rangeStart, rangeEnd);
    }

    @Override
    public void applyTransaction(@NonNull @lombok.NonNull TransactionSortedList enhancedSortedList) {
        enhancedSortedList.removeRange(rangeStart, rangeEnd);
    }
}
