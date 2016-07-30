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
public class TransactionClearTest extends TransactionBaseTest<Integer, String> {

    public TransactionClearTest(Parameter<Integer, String> parameter) {
        super(parameter);
    }

    @Parameterized.Parameters
    public static Iterable<Parameter<Integer, String>> getParameters() {
        ArrayList<Parameter<Integer, String>> parameters = new ArrayList<>();

        parameters.add(new Parameter<>(null, new TransactionClear()));

        return parameters;
    }

    @Override
    protected void verifyTransformedType(Transaction<String> transformedTransaction) {
        assertTrue(transformedTransaction instanceof TransactionClear);
    }

    @Override
    protected void verifyTransactionApplied(TransactionSortedList<Integer> enhancedSortedList, Transaction<Integer> transaction) {
        verify(enhancedSortedList).clear();
        verifyNoMoreInteractions(enhancedSortedList);
    }

}