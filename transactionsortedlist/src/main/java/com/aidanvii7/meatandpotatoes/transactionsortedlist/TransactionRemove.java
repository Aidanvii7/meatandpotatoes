package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
class TransactionRemove<E> implements Transaction<E> {
    final E item;

    public TransactionRemove(E item) {
        this.item = item;
    }

    @NonNull
    @Override
    public <T> TransactionRemove<T> transform(@lombok.NonNull @NonNull TransformFunc<? super E, ? extends T> transformFunc) {
        return new TransactionRemove<>(transformFunc.transform(item));
    }

    @Override
    public void applyTransaction(@NonNull @lombok.NonNull TransactionSortedList<E> enhancedSortedList) {
        enhancedSortedList.remove(item);
    }
}
