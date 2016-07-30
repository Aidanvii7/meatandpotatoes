package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
public interface Transaction<E>
{
  @NonNull
  <T> Transaction<T> transform(@NonNull @lombok.NonNull TransformFunc<? super E, ? extends T> transformFunc);

  void applyTransaction(@NonNull @lombok.NonNull TransactionSortedList<E> enhancedSortedList);
}
