package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.iterate;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
class TransactionAddAll<E> implements Transaction<E> {

    final Collection<E> items;

    public TransactionAddAll(Collection<E> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public <T> TransactionAddAll<T> transform(@lombok.NonNull @NonNull TransformFunc<? super E, ? extends T> transformFunc) {
        Collection<T> transformed = new ArrayList<>();
        iterate(items, entry -> transformed.add(transformFunc.transform(entry)));
        return new TransactionAddAll<>(transformed);
    }

    @Override
    public void applyTransaction(@NonNull @lombok.NonNull TransactionSortedList<E> enhancedSortedList) {
        enhancedSortedList.addAll(items);
    }
}
