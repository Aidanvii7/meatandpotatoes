package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by Aidan McWilliams on 09/07/2016.
 */
@RunWith(Parameterized.class)
public class TransactionAddAllCollectionTest extends TransactionBaseTest<Integer, String> {

    public TransactionAddAllCollectionTest(Parameter<Integer, String> parameter) {
        super(parameter);
    }

    @Parameterized.Parameters
    public static Iterable<Parameter<Integer, String>> getParameters() {
        ArrayList<Parameter<Integer, String>> parameters = new ArrayList<>();
        ArrayList<Integer> items = new ArrayList<>();
        items.add(1);
        items.add(2);
        items.add(3);
        items.add(4);
        Transaction<Integer> transaction = new TransactionAddAll<>(items);
        parameters.add(new Parameter<>(Object::toString, transaction));
        parameters.add(new Parameter<>(source -> null, transaction));
        parameters.add(new Parameter<>(null, transaction));

        return parameters;
    }

    @Override
    protected void verifyTransformedType(Transaction<String> transformedTransaction) {
        assertTrue(transformedTransaction instanceof TransactionAddAll);
    }

    @Override
    protected void verifyTransactionApplied(TransactionSortedList<Integer> enhancedSortedList, Transaction<Integer> transaction) {
        verify(enhancedSortedList).addAll(((TransactionAddAll<Integer>) transaction).items);
        verifyNoMoreInteractions(enhancedSortedList);
    }

}