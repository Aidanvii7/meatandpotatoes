package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
class TransactionRemoveAt<E> implements Transaction<E> {

    final int location;

    public TransactionRemoveAt(int location) {
        this.location = location;
    }


    @NonNull
    @Override
    public <T> TransactionRemoveAt<T> transform(@NonNull @lombok.NonNull TransformFunc<? super E, ? extends T> transformFunc) {
        return new TransactionRemoveAt<>(location);
    }

    @Override
    public void applyTransaction(@NonNull @lombok.NonNull TransactionSortedList enhancedSortedList) {
        enhancedSortedList.removeItemAt(location);
    }
}
