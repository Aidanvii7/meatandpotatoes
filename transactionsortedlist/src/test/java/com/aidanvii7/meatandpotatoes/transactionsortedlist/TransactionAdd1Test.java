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
public class TransactionAdd1Test extends TransactionBaseTest<Integer, String> {

    public TransactionAdd1Test(Parameter<Integer, String> parameter) {
        super(parameter);
    }

    @Parameterized.Parameters
    public static Iterable<Parameter<Integer, String>> getParameters() {
        ArrayList<Parameter<Integer, String>> parameters = new ArrayList<>();

        TransactionAdd<Integer> transaction = new TransactionAdd<>(1);
        parameters.add(new Parameter<>(Object::toString, transaction));
        parameters.add(new Parameter<>(source -> null, transaction));
        parameters.add(new Parameter<>(null, transaction));

        return parameters;
    }

    @Override
    protected void verifyTransformedType(Transaction<String> transformedTransaction) {
        assertTrue(transformedTransaction instanceof TransactionAdd);
    }

    @Override
    protected void verifyTransactionApplied(TransactionSortedList<Integer> enhancedSortedList, Transaction<Integer> transaction) {
        verify(enhancedSortedList).add(((TransactionAdd<Integer>) transaction).item);
        verifyNoMoreInteractions(enhancedSortedList);
    }

}