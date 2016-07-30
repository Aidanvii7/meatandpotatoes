package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;

import com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.cancel;
import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.iterate;

/**
 * Created by aidan.mcwilliams on 16/05/2016.
 * <p>A {@link SortedList} with some extra features.</p>
 * <p>Extra features:</p>
 * <p> 1: Can be converted to a traditional  {@link List} with {@link #toList()} and {@link #subList(int, int)}.</p>
 * <p> 2: modifications to the list can be recorded in a {@link TransactionLog}. See {@link #beginTransactionLogging()} and {@link #endTransactionLogging()}.</p>
 * <p> 3: A {@link TransactionLog} can be applied to another instance of {@link TransactionSortedList} with {@link #applyTransactions(TransactionLog)}.</p>
 * <p>
 * Intended for MVP and MVVM architectures were you want to reflect a source data-set, without direct access to the data-set.
 * For example, an {@link TransactionSortedList} could exist at the model level, sending deltas in the form of a {@link TransactionLog}
 * to the ViewModel or Presenter, which has it's own {@link TransactionSortedList} and updated via the delta object {@link TransactionLog}.
 * This pattern could also be applied from the ViewModel or Presenter layer to the View layer itself.
 * Naturally, This lends itself to multithreaded environments as the {@link android.support.v7.widget.RecyclerView}
 * would not be binding against a data-set that is modified on another thread.
 * </p>
 */
public class TransactionSortedList<E> extends SortedList<E> implements Iterable<E> {

    //region members

    @Getter
    private boolean logging = false;
    private final TransactionLog<E> transactionLog = new TransactionLog<>();
    private final Callback<E> callback;
    private final Class<E> clazz;
    private final ListFunc<E> onAddedListener;
    private final ListFunc<E> onRemovedListener;

    //endregion

    //region public

    public TransactionSortedList(@NonNull @lombok.NonNull Class<E> klass, @NonNull @lombok.NonNull Callback<E> callback, boolean defaultListeners) {
        super(klass, callback);
        this.clazz = klass;
        this.callback = callback;
        onAddedListener = defaultListeners ? this::onItemAdded : null;
        onRemovedListener = defaultListeners ? this::onItemRemoved : null;
    }

    public TransactionSortedList(@NonNull @lombok.NonNull Class<E> klass, @NonNull @lombok.NonNull Callback<E> callback, int initialCapacity, boolean defaultListeners) {
        super(klass, callback, initialCapacity);
        this.clazz = klass;
        this.callback = callback;
        onAddedListener = defaultListeners ? this::onItemAdded : null;
        onRemovedListener = defaultListeners ? this::onItemRemoved : null;
    }

    public TransactionSortedList(@NonNull @lombok.NonNull Class<E> klass, @NonNull @lombok.NonNull Callback<E> callback, @Nullable ListFunc<E> onAddedListener, @Nullable ListFunc<E> onRemovedListener) {
        super(klass, callback);
        this.clazz = klass;
        this.callback = callback;
        this.onAddedListener = onAddedListener;
        this.onRemovedListener = onRemovedListener;
    }

    public TransactionSortedList(@NonNull @lombok.NonNull Class<E> klass, @NonNull @lombok.NonNull Callback<E> callback, int initialCapacity, @Nullable ListFunc<E> onAddedListener, @Nullable ListFunc<E> onRemovedListener) {
        super(klass, callback, initialCapacity);
        this.clazz = klass;
        this.callback = callback;
        this.onAddedListener = onAddedListener;
        this.onRemovedListener = onRemovedListener;
    }

    /**
     * <p>Creates an instance of {@link TransactionLog} of type {@link T} from the source {@link TransactionLog} of type {@link E}.</p>
     *
     * @param Klass               The {@link Class} of type {@link T}
     * @param transformerCallback The {@link SortedList.Callback} to use. Required for sorting.
     * @param func                The {@link TransformFunc} to create a {@link T} from the source {@link E}.
     * @param <T>                 The transformed type.
     * @return A transformed {@link TransactionSortedList} of type {@link T}.
     */
    public <T> TransactionSortedList<T> transform(Class<T> Klass, Callback<T> transformerCallback, TransformFunc<? super E, ? extends T> func) {
        TransactionLog<E> log = asTransactionLog();
        TransactionLog<T> transformedLog = log.transform(func);
        TransactionSortedList<T> transformedList = new TransactionSortedList<T>(Klass, transformerCallback, false);
        transformedList.applyTransactions(transformedLog);
        return transformedList;
    }

    public synchronized List<E> toList() {
        return subList(0, size());
    }

    public List<E> subList(int startPosition, int endPosition) {
        List<E> subList = new ArrayList<>();
        for (int i = startPosition; i < endPosition; i++) subList.add(get(i));
        return subList;
    }

    /**
     * <p>Starts transaction logging. Can be retrieved with {@link #getTransactionLogSnapshot()} and {@link #endTransactionLogging()}.</p>
     *
     * @return returns true if recording started, false if already recording.
     */
    public synchronized boolean beginTransactionLogging() {
        if (logging) return false;
        logging = true;
        transactionLog.getTransactions().clear();
        return true;
    }

    /**
     * Stops logging and clears the internal {@link TransactionLog}.
     *
     * @return a snapshot/copy of the {@link TransactionLog} before it was cleared. This may be empty.
     */
    @NonNull
    public synchronized TransactionLog<E> endTransactionLogging() {
        TransactionLog<E> snapshot = getTransactionLogSnapshot();
        transactionLog.getTransactions().clear();
        logging = false;
        return snapshot;
    }

    /**
     * Creates a snapshot/copy of the current {@link TransactionLog} without clearing the internal {@link TransactionLog}.
     *
     * @return a snapshot/copy of the {@link TransactionLog}. This may be empty.
     */
    @NonNull
    public synchronized TransactionLog<E> getTransactionLogSnapshot() {
        return new TransactionLog<E>(transactionLog);
    }

    /**
     * <p>Creates a {@link TransactionLog} that represents the {@link TransactionSortedList}.</p>
     * <p>This will only consist of {@link TransactionAdd} and will not be a history of every {@link Transaction} made on the {@link TransactionSortedList}.</p>
     *
     * @return the {@link TransactionLog}.
     */
    @NonNull
    public synchronized TransactionLog<E> asTransactionLog() {
        // create a temporary list, using a copy of the callback (dont want to trigger erroneous behaviour on Views that may be listening to it!)
        // also dont set add/remove listeners!
        TransactionSortedList<E> temp = new TransactionSortedList<>(clazz, callback.copy(), false);
        int size = size();
        temp.beginTransactionLogging();
        for (int i = 0; i < size; i++)
            temp.add(get(i));
        return temp.endTransactionLogging();
    }

    /**
     * <p>Mutates the {@link TransactionSortedList} with every {@link Transaction} in the {@link TransactionLog}.</p>
     * <p>Take care when using this with index reference transactions created by {@link #removeItemAtIndex(int, boolean)},
     * {@link #removeRange(int, int)} and {@link #updateItemAt(int, Object)} as these should use the same sorting mechanism defined by
     * the current {@link SortedList.Callback}. See {@link SortedList.Callback#compare(Object, Object)}.
     * </p>
     *
     * @param transactionLog
     */
    public boolean applyTransactions(@NonNull @lombok.NonNull TransactionLog<E> transactionLog) {
        for (Transaction<E> transaction : transactionLog.getTransactions())
            transaction.applyTransaction(this);
        return callback.getCalledAndReset();
    }

    public void forEach(@NonNull @lombok.NonNull ListAction<E> action) {
        for (int i = 0; i < size(); i++) action.call(get(i));
    }

    @Nullable
    public synchronized E getExistingForSameItem(@NonNull @lombok.NonNull E t, @Nullable AtomicInteger index) {
        for (int i = 0; i < size(); i++) {
            E cur = get(i);
            if (callback.areItemsTheSame(cur, t)) {
                if (index != null) index.set(i);
                return cur;
            }
        }
        return null;
    }

    public synchronized boolean containsItem(@NonNull @lombok.NonNull E t) {
        for (int i = 0; i < size(); i++) {
            E cur = get(i);
            if (callback.areItemsTheSame(cur, t)) return true;
        }
        return false;
    }

    public synchronized boolean containsItemReverse(@NonNull @lombok.NonNull E t) {
        boolean containsItem = false;
        for (int i = size() - 1; i >= 0; i--) {
            E cur = get(i);
            if (callback.areItemsTheSame(cur, t)) {
                containsItem = true;
                break;
            }

        }
        return containsItem;
    }

    public synchronized boolean containsItemWithSameContents(@NonNull @lombok.NonNull E t) {
        for (int i = 0; i < size(); i++) {
            E cur = get(i);
            if (callback.areItemsTheSame(cur, t) && callback.areContentsTheSame(cur, t))
                return true;
        }
        return false;
    }

    //region list mutators

    @Override
    public int add(E item) {
        int i = super.add(item);
        if (logging && callback.getCalledAndReset())
            transactionLog.appendTransaction(new TransactionAdd<E>(item));

        if (onAddedListener != null && toList().contains(item)) onAddedListener.call(item);
        return i;
    }

    @Override
    public void addAll(E[] items, boolean mayModifyInput) {
        addAll(items); // FIXME ignoring mayModify
    }

    @SafeVarargs
    @Override
    public final void addAll(E... items) {
        List<E> list = new ArrayList<>();
        Collections.addAll(list, items);
        addAll(list);
    }

    @Override
    public void addAll(Collection<E> items) {
        super.addAll(items);
        if (logging && callback.getCalledAndReset()) {
            transactionLog.appendTransaction(new TransactionAddAll<E>(items));
        }

        if (onAddedListener != null) {
            iterate(this, entry -> {
                for (E e : items) {
                    if (e.equals(entry)) {
                        onAddedListener.call(e);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public boolean remove(E item) {
        E existing = getExistingForSameItem(item, null);
        boolean removed = super.remove(item);
        if (onRemovedListener != null) onRemovedListener.call(existing);
        if (logging && callback.getCalledAndReset()) {
            transactionLog.appendTransaction(new TransactionRemove<E>(item));
        }
        return removed;
    }

    @Override
    public E removeItemAt(int index) {
        E removed = super.removeItemAt(index);
        if (logging && callback.getCalledAndReset()) {
            transactionLog.appendTransaction(new TransactionRemoveAt<>(index));
        }
        if (onRemovedListener != null) onRemovedListener.call(removed);
        return removed;
    }

    public void removeRange(int startPosition, int endPosition) {
        int leftToRemove = (endPosition + 1) - startPosition;
        while (leftToRemove > 0) {
            removeItemAt(startPosition);
            leftToRemove--;
        }
        if (logging && callback.getCalledAndReset()) {
            transactionLog.appendTransaction(new TransactionRemoveRange<>(startPosition, endPosition));
        }

    }

    public void removeAll(Collection<E> items) {
        iterate(items, this::remove);
    }

    @Override
    public void updateItemAt(int index, E item) {
        E previous = get(index);
        super.updateItemAt(index, item);
        // TODO messy, requires both to be not null..
        if (onRemovedListener != null && onAddedListener != null) {
            iterate(this, new IterableHelper.IterableFunction<E>() {
                boolean foundPrevious = false;
                boolean foundNew = false;

                @Override
                public void next(E entry) throws IterableHelper.BreakIterationException {
                    if (entry.equals(previous)) {
                        foundPrevious = true;
                        onRemovedListener.call(previous);
                    } else if (entry.equals(item)) {
                        foundNew = true;
                        onAddedListener.call(item);
                    }
                    if (foundNew && foundPrevious) cancel();
                }
            });
        }
        if (logging && callback.getCalledAndReset()) {
            transactionLog.appendTransaction(new TransactionUpdateIndex<E>(index, item));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        super.clear();
        if (logging && callback.getCalledAndReset())
            transactionLog.appendTransaction(new TransactionClear());
    }

    //endregion

    @Override
    public Iterator<E> iterator() {
        return new EnhancedSortedListIterator<>(this);
    }


    //endregion

    //region private

    private void fireAddAll(E[] items) {
        if (onAddedListener != null) {
            iterate(this, entry -> {
                for (E e : items) {
                    if (e.equals(entry)) {
                        onAddedListener.call(e);
                        break;
                    }
                }
            });
        }
    }

    private void onItemAdded(E item) {
        if (item instanceof SortedListItem) ((SortedListItem) item).initOnAdd();
    }

    private void onItemRemoved(E item) {
        if (item instanceof SortedListItem) ((SortedListItem) item).cleanupOnRemove();
    }

    //endregion

    public abstract static class Callback<T2> extends SortedList.Callback<T2> {
        private final AtomicBoolean callbackFired = new AtomicBoolean(false);

        private boolean getCalledAndReset() {
            return callbackFired.getAndSet(false);
        }

        @NonNull
        public abstract Callback<T2> copy();

        @CallSuper
        @Override
        public void onChanged(int position, int count) {
            callbackFired.set(true);
        }

        @CallSuper
        @Override
        public void onRemoved(int position, int count) {
            callbackFired.set(true);
        }

        @CallSuper
        @Override
        public void onInserted(int position, int count) {
            callbackFired.set(true);
        }

        @CallSuper
        @Override
        public void onMoved(int fromPosition, int toPosition) {
            callbackFired.set(true);
        }
    }

    public static class EnhancedSortedListIterator<E> implements Iterator<E> {
        private final TransactionSortedList<E> enhancedSortedList;
        private int currentIndex = 0;
        private boolean canRemove = false;

        public EnhancedSortedListIterator(TransactionSortedList<E> enhancedSortedList) {
            this.enhancedSortedList = enhancedSortedList;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < enhancedSortedList.size();
        }

        @Override
        public E next() {
            canRemove = true;
            return enhancedSortedList.get(currentIndex++);
        }

        @Override
        public void remove() {
            if (canRemove) enhancedSortedList.removeItemAt(currentIndex - 1);
            canRemove = false;
        }
    }

    public interface ListAction<E> {
        void call(E e);
    }
}
