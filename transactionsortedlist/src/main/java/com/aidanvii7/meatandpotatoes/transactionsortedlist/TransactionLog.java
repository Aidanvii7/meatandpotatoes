package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 * <p>Container of {@link Transaction}s created by a {@link TransactionSortedList}.</p>
 */
public class TransactionLog<E> implements Iterable<Transaction<E>> {
    //region members

    @Getter(AccessLevel.PACKAGE)
    private final List<Transaction<E>> transactions;

    //endregion

    //region package private

    TransactionLog() {
        transactions = new ArrayList<>();
    }

    /**
     * Copy constructor.
     *
     * @param original The source {@link TransactionLog} to copy.
     */
    TransactionLog(@NonNull @lombok.NonNull TransactionLog<E> original) {
        transactions = new ArrayList<>(original.transactions);
    }

    /**
     * <p>Adds a new {@link Transaction} to the end of the {@link TransactionLog}.</p>
     *
     * @param transaction
     */
    TransactionLog<E> appendTransaction(@NonNull @lombok.NonNull Transaction<E> transaction) {
        transactions.add(transaction);
        return this;
    }

    //endregion

    //region public

    public static <E> TransactionLog<E> from(@NonNull @lombok.NonNull E t) {
        return new TransactionLog<E>().appendTransaction(new TransactionAdd<>(t));
    }

    public static <E> TransactionLog<E> from(@NonNull @lombok.NonNull Iterable<E> iterable) {
        TransactionLog<E> log = new TransactionLog<>();
        for (E e : iterable) log.appendTransaction(new TransactionAdd<>(e));
        return log;
    }


    /**
     * <p>Creates a new {@link TransactionLog} of type {@link T}.</p>
     *
     * @param transformFunc the {@link TransformFunc} to convert a {@link E} to a {@link T}.
     * @param <T>           the Generic transformed type.
     * @return The transformed {@link TransactionLog} of type {@link T}.
     */
    public <T> TransactionLog<T> transform(@NonNull @lombok.NonNull TransformFunc<? super E, ? extends T> transformFunc) {
        TransactionLog<T> transformed = new TransactionLog<>();
        for (Transaction<? extends E> transaction : transactions)
            transformed.appendTransaction(transaction.transform(transformFunc));
        return transformed;
    }

    /**
     * @return true if the {@link TransactionLog} is not empty.
     */
    public boolean hasTransactions() {
        return !transactions.isEmpty();
    }

    @Override
    public Iterator<Transaction<E>> iterator() {
        return new TransactionLogIterator<>(this);
    }

    //endregion

    public static class TransactionLogIterator<E> implements Iterator<Transaction<E>> {
        private final TransactionLog<E> log;
        private int cur = 0;
        private boolean canRemove = false;

        public TransactionLogIterator(TransactionLog<E> log) {
            this.log = log;
        }

        @Override
        public boolean hasNext() {
            return cur < log.transactions.size();
        }

        @Override
        public Transaction<E> next() {
            canRemove = true;
            return log.transactions.get(cur++);
        }

        @Override
        public void remove() {
            if (canRemove) log.transactions.remove(cur - 1);
            canRemove = false;
        }
    }
}
