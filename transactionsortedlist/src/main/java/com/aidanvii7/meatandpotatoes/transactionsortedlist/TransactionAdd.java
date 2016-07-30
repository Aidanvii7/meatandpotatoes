package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
class TransactionAdd<E> implements Transaction<E> {

    final E item;

    public TransactionAdd(E item) {
        this.item = item;
    }

    @NonNull
    @Override
    public <T> TransactionAdd<T> transform(@NonNull @lombok.NonNull TransformFunc<? super E, ? extends T> transformFunc) {
        return new TransactionAdd<>(transformFunc.transform(item));
    }

    @Override
    public void applyTransaction(@NonNull @lombok.NonNull TransactionSortedList<E> enhancedSortedList) {
        enhancedSortedList.add(item);
    }
}
