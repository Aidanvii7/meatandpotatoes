package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;

/**
 * Created by Aidan McWilliams on 12/07/2016.
 */
@RunWith(JUnit4.class)
public class TransactionSortedListTest {

    //region setup
    TransactionSortedList<Integer> transactionSortedList;

    public TransactionSortedListTest() {
    }

    @Before
    public void setUp() throws Exception {
        transactionSortedList = new TransactionSortedList<>(Integer.class, new MySortedListCallback(), false);
    }

    //endregion

    //region Tests

    @Test
    public void testTransform() throws Exception {
        // TODO
    }

    @Test
    public void testToList() throws Exception {
        // TODO
    }

    @Test
    public void testSubList() throws Exception {
        // TODO
    }

    @Test
    public void testBeginTransactionLogging() throws Exception {
        // TODO
    }

    @Test
    public void testEndTransactionLogging() throws Exception {
        // TODO
    }

    @Test
    public void testGetTransactionLogSnapshot() throws Exception {
        // TODO
    }

    @Test
    public void testAsTransactionLog() throws Exception {
        // TODO
    }

    @Test
    public void testApplyTransactions() throws Exception {
        // TODO
    }

    @Test
    public void testForEach() throws Exception {
        // TODO
    }

    @Test
    public void testGetExistingForSameItem() throws Exception {
        // TODO
    }

    @Test
    public void testContainsItem() throws Exception {
        // TODO
    }

    @Test
    public void testContainsItemReverse() throws Exception {
        // TODO
    }

    @Test
    public void testContainsItemWithSameContents() throws Exception {
        // TODO
    }

    @Test
    public void testAdd() throws Exception {
        // TODO
    }

    @Test
    public void testAddAll() throws Exception {
        // TODO
    }

    @Test
    public void testAddAll1() throws Exception {
        // TODO
    }

    @Test
    public void testAddAll2() throws Exception {
        // TODO
    }

    @Test
    public void testRemove() throws Exception {
        // TODO
    }

    @Test
    public void testRemoveItemAt() throws Exception {
        // TODO
    }

    @Test
    public void testRemoveRange() throws Exception {
        // TODO
    }

    @Test
    public void testUpdateItemAt() throws Exception {
        // TODO
    }

    @Test
    public void testClear() throws Exception {
        // TODO
    }

    @Test
    public void testIterator() throws Exception {
        // TODO
    }

    @Test
    public void testIsLogging() throws Exception {
        // TODO
    }

    //endregion

    public static class MySortedListCallback extends TransactionSortedList.Callback<Integer> {
        @NonNull
        @Override
        public TransactionSortedList.Callback<Integer> copy() {
            return null;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return 0;
        }

        @Override
        public boolean areContentsTheSame(Integer oldItem, Integer newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(Integer item1, Integer item2) {
            return false;
        }
    }
}