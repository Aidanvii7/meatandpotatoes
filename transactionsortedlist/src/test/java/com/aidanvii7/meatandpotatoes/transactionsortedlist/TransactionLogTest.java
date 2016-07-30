package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.iterate;
import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.skipRemoveCurrent;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.hamcrest.CoreMatchers.instanceOf;

/**
 * Created by Aidan McWilliams on 02/07/2016.
 */
@RunWith(Parameterized.class)
public class TransactionLogTest {

    //region setup

    final List<String> strings;
    TransactionLog<String> transactionLog;

    public TransactionLogTest(List<String> strings) {
        this.strings = strings;
    }

    @Parameterized.Parameters
    public static Iterable<List<String>> getParameters() {

        ArrayList<List<String>> transactionLists = new ArrayList<>();

        appendStringsAsList(transactionLists, "Uncle");
        appendStringsAsList(transactionLists, "Uncle", "Bob");
        appendStringsAsList(transactionLists, "Uncle", "Bob", "Clean");
        appendStringsAsList(transactionLists, "Uncle", "Bob", "Clean", "Code");

        return transactionLists;
    }


    static void appendStringsAsList(ArrayList<List<String>> transactionLists, String... strings) {

        List<String> list = new ArrayList<>();
        for (String string : strings)
            list.add(string);
        transactionLists.add(list);
    }

    @Test
    @Before
    public void beforeEachTest() throws Exception {
        transactionLog = new TransactionLog<>();
        assertFalse(transactionLog.hasTransactions());
        assertEquals(transactionLog.getTransactions().size(), 0);
    }

    @Test
    public void testCopyConstructor() throws Exception {
        transactionLog = TransactionLog.from(strings);
        TransactionLog<String> transactionLogCopy = new TransactionLog<>(transactionLog);
        assertTrue(transactionLogCopy.hasTransactions());

        assertEquals(transactionLogCopy.getTransactions().size(), transactionLog.getTransactions().size());

        for (int i = 0; i < transactionLog.getTransactions().size(); i++)
            assertEquals(transactionLog.getTransactions().get(i), transactionLogCopy.getTransactions().get(i));


    }


    //endregion

    //region tests

    @Test
    public void testAppendTransaction() throws Exception {
        Transaction<String> transaction = new TransactionAdd<>("Uncle Bob");
        transactionLog.appendTransaction(transaction);
        assertTrue(transactionLog.getTransactions().contains(transaction));
    }

    @Test
    public void testFrom() throws Exception {
        if (strings.size() > 0) {

            String transactionString;
            {
                transactionString = strings.get(0);
                transactionLog = TransactionLog.from(transactionString);
            }

            assertEquals(transactionLog.getTransactions().size(), 1);
            assertThat(transactionLog.getTransactions().get(0), instanceOf(TransactionAdd.class));
            assertEquals(((TransactionAdd<String>) transactionLog.getTransactions().get(0)).item, transactionString);

        }
    }

    @Test
    public void testFrom1() throws Exception {
        transactionLog = TransactionLog.from(strings);

        assertEquals(transactionLog.getTransactions().size(), strings.size());

        List<Transaction<String>> transactions = transactionLog.getTransactions();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction<String> transaction = transactions.get(i);
            assertThat(transaction, instanceOf(TransactionAdd.class));
            TransactionAdd<String> transactionAdd1 = (TransactionAdd<String>) transactions.get(i);
            assertEquals(transactionAdd1.item, strings.get(i));
        }

    }

    @Test
    public void testTransform() throws Exception {
        transactionLog = TransactionLog.from(strings);
        TransactionLog<char[]> transformedTransactionLog = transactionLog.transform(String::toCharArray);

        assertEquals(transformedTransactionLog.getTransactions().size(), transactionLog.getTransactions().size());
        for (int i = 0; i < transactionLog.getTransactions().size(); i++) {
            assertThat(transformedTransactionLog.getTransactions().get(i), instanceOf(TransactionAdd.class));
            TransactionAdd<String> transaction = (TransactionAdd<String>) transactionLog.getTransactions().get(i);
            TransactionAdd<char[]> transformedTransaction = (TransactionAdd<char[]>) transformedTransactionLog.getTransactions().get(i);
            assertEquals(String.valueOf(transformedTransaction.item), transaction.item);

        }
    }

    @Test
    public void testHasTransactions() throws Exception {
        assertFalse(transactionLog.hasTransactions());
        transactionLog = TransactionLog.from(strings);
        assertTrue(transactionLog.hasTransactions());
    }

    @Test
    public void testIterator() throws Exception {
        transactionLog = TransactionLog.from(strings);
        final Iterator<String> stringIterator = strings.iterator();
        iterate(transactionLog, entry -> {
            assertEquals(((TransactionAdd<String>) entry).item, stringIterator.next());
        });
    }

    @Test
    public void testIteratorRemove() throws Exception {

        Iterable<String> stringsCopy = new ArrayList<>(strings);
        final AtomicReference<Transaction<String>> previousTransaction = new AtomicReference<>(null);
        transactionLog = TransactionLog.from(stringsCopy);
        iterate(transactionLog, entry -> {
            assertFalse(transactionLog.getTransactions().contains(previousTransaction.get()));
            previousTransaction.set(entry);
            skipRemoveCurrent();
        });

    }

    @Test
    public void testGetTransactions() throws Exception {
        assertNotNull(transactionLog.getTransactions());
        assertEquals(transactionLog.getTransactions().size(), 0);
        transactionLog = TransactionLog.from(strings);
        assertEquals(transactionLog.getTransactions().size(), strings.size());
    }

    //endregion
}