package com.aidanvii7.meatandpotatoes.transactionsortedlist;

/**
 * Created by aidan.mcwilliams on 17/05/2016.
 */
public interface TransformFunc<FROM, TO>
{
  TO transform(FROM source);
}