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
public class TransactionRemoveItemAtTest extends TransactionBaseTest<Integer, String> {

    public TransactionRemoveItemAtTest(Parameter<Integer, String> parameter) {
        super(parameter);
    }

    @Parameterized.Parameters
    public static Iterable<Parameter<Integer, String>> getParameters() {
        ArrayList<Parameter<Integer, String>> parameters = new ArrayList<>();

        Transaction<Integer> transaction = new TransactionRemoveAt<>(10);
        parameters.add(new Parameter<>(Object::toString, transaction));
        parameters.add(new Parameter<>(source -> null, transaction));
        parameters.add(new Parameter<>(null, transaction));

        return parameters;
    }

    @Override
    protected void verifyTransformedType(Transaction<String> transformedTransaction) {
        assertTrue(transformedTransaction instanceof TransactionRemoveAt);
    }

    @Override
    protected void verifyTransactionApplied(TransactionSortedList<Integer> enhancedSortedList, Transaction<Integer> transaction) {
        verify(enhancedSortedList).removeItemAt(((TransactionRemoveAt<Integer>) transaction).location);
        verifyNoMoreInteractions(enhancedSortedList);
    }

}