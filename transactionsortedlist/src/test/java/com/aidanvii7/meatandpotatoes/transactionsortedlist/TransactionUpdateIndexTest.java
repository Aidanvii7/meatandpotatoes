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
public class TransactionUpdateIndexTest extends TransactionBaseTest<Integer, String> {

    public TransactionUpdateIndexTest(Parameter<Integer, String> parameter) {
        super(parameter);
    }

    @Parameterized.Parameters
    public static Iterable<Parameter<Integer, String>> getParameters() {
        ArrayList<Parameter<Integer, String>> parameters = new ArrayList<>();

        parameters.add(new Parameter<>(Object::toString, new TransactionUpdateIndex<>(2, 4)));
        parameters.add(new Parameter<>(source -> null, new TransactionUpdateIndex<>(1, 5)));
        parameters.add(new Parameter<>(null, new TransactionUpdateIndex<>(0, 10)));

        return parameters;
    }

    @Override
    protected void verifyTransformedType(Transaction<String> transformedTransaction) {
        assertTrue(transformedTransaction instanceof TransactionUpdateIndex);
    }

    @Override
    protected void verifyTransactionApplied(TransactionSortedList<Integer> enhancedSortedList, Transaction<Integer> transaction) {
        TransactionUpdateIndex<Integer> transactionUpdateIndex = (TransactionUpdateIndex<Integer>) transaction;
        verify(enhancedSortedList).updateItemAt(transactionUpdateIndex.index, transactionUpdateIndex.item);
        verifyNoMoreInteractions(enhancedSortedList);
    }

}