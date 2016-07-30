package com.aidanvii7.meatandpotatoes.transactionsortedlist;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
public interface SortedListItem<S extends SortedListItem>
{
  long getSortingID();

  boolean areContentsTheSame(S other);

  boolean areItemsTheSame(S other);

  void initOnAdd();

  void cleanupOnRemove();

  boolean supportsStringSort();

  String getSortingString();
}
