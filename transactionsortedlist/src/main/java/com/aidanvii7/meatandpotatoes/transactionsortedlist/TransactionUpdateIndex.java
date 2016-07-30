package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
class TransactionUpdateIndex<E> implements Transaction<E>
{
  final int index;
  final E item;

  public TransactionUpdateIndex(int index, E item)
  {
    this.index = index;
    this.item = item;
  }

  @NonNull
  @Override
  public <T> TransactionUpdateIndex<T> transform(@lombok.NonNull @NonNull TransformFunc<? super E, ? extends T> transformFunc)
  {
    return new TransactionUpdateIndex<>(index, transformFunc.transform(item));
  }

  @Override
  public void applyTransaction(@NonNull @lombok.NonNull TransactionSortedList<E> enhancedSortedList) {
    enhancedSortedList.updateItemAt(index, item);
  }
}
