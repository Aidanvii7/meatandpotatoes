package com.aidanvii7.meatandpotatoes.core.iteratorhelper;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;

import lombok.Getter;

/**
 * Created by aidan.mcwilliams on 27/04/2016.
 * <p>Helper class for iterating an {@link Iterable} with the ability to {@link #skip()}, {@link #skipRemoveCurrent()} and {@link #cancel()} the iteration.</p>
 * <p>See {@link #iterate(Iterable, IterableFunction)} for usage.</p>
 */
public final class IterableHelper<E> {

    private IterableHelper() {
    }

    //region public API

    /**
     * Iterates an {@link Iterable} calling an {@link IterableFunction} on each entry.
     *
     * @param iterable         the {@link Iterable} to operate on.
     * @param iterableFunction the {@link IterableFunction} to call on each instance of {@link E} in the {@link Iterable}.
     * @param <E>
     */
    public static <E> void iterate(@NonNull @lombok.NonNull Iterable<E> iterable, @NonNull @lombok.NonNull IterableFunction<E> iterableFunction) {
        Iterator<E> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            E entry = iterator.next();
            try {
                iterableFunction.next(entry);
            } catch (BreakIterationException e) {
                if (e.getMode().equals(BreakIterationException.Mode.CANCEL)) {
                    break;
                } else if (e.getMode().equals(BreakIterationException.Mode.CANCEL_REMOVE_CURRENT)) {
                    iterator.remove();
                    break;
                } else if (e.getMode().equals(BreakIterationException.Mode.SKIP_REMOVE_CURRENT)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Call this within an {@link IterableFunction#next(Object)} call to skip to the next entry.
     *
     * @throws BreakIterationException
     */
    public static void skip() throws BreakIterationException {
        throw new BreakIterationException(BreakIterationException.Mode.SKIP);
    }

    /**
     * Call this within an {@link IterableFunction#next(Object)} call to skip to the next entry.
     *
     * @throws BreakIterationException
     */
    public static void skipRemoveCurrent() throws BreakIterationException {
        throw new BreakIterationException(BreakIterationException.Mode.SKIP_REMOVE_CURRENT);
    }

    /**
     * Call this within an {@link IterableFunction#next(Object)} call to cancel the iteration.
     *
     * @throws BreakIterationException
     */
    public static void cancel() throws BreakIterationException {
        throw new BreakIterationException(BreakIterationException.Mode.CANCEL);
    }

    /**
     * Call this within an {@link IterableFunction#next(Object)} call to cancel the iteration.
     *
     * @throws BreakIterationException
     */
    public static void cancelRemoveCurrent() throws BreakIterationException {
        throw new BreakIterationException(BreakIterationException.Mode.CANCEL_REMOVE_CURRENT);
    }

    /**
     * Represents a function that is called on each entry in the {@link Collection} given to {@link #iterate(Iterable, IterableFunction)}.
     *
     * @param <E> the entry type.
     */
    public interface IterableFunction<E> {
        /**
         * @param entry the current entry in the collection.
         */
        void next(E entry) throws BreakIterationException;
    }

    //endregion


    public static final class BreakIterationException extends Exception {

        @Getter
        final Mode mode;

        private BreakIterationException(@NonNull Mode mode) {
            this.mode = mode;
        }

        private enum Mode {
            CANCEL, CANCEL_REMOVE_CURRENT, SKIP, SKIP_REMOVE_CURRENT
        }
    }

}
