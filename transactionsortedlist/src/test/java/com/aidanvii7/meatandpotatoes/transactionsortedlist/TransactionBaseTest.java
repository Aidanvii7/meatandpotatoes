package com.aidanvii7.meatandpotatoes.transactionsortedlist;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * Created by Aidan McWilliams on 09/07/2016.
 */
@SuppressWarnings("unchecked")
public abstract class TransactionBaseTest<T, F> {

    final Parameter<T, F> parameter;
    final TransactionSortedList<T> enhancedSortedList = mock(TransactionSortedList.class);
    @Rule public ExpectedException expectedException = ExpectedException.none();

    public static class Parameter<T, F> {
        final TransformFunc<T, F> transformFunc;
        final Transaction<T> transaction;

        public Parameter(TransformFunc<T, F> func, Transaction<T> transaction) {
            this.transformFunc = func;
            this.transaction = transaction;
        }
    }

    public TransactionBaseTest(Parameter<T, F> parameter) {
        this.parameter = parameter;

    }

    @Before
    public void setUp() throws Exception {
        reset(enhancedSortedList);
    }

    @Test
    public void testTransform() throws Exception {
        boolean isNultransforFuncNull = parameter.transformFunc == null;
        if (isNultransforFuncNull) expectedException.expect(NullPointerException.class);

        Transaction<F> transformedTransaction = parameter.transaction.transform(parameter.transformFunc);

        if (!isNultransforFuncNull) verifyTransformedType(transformedTransaction);
    }


    @Test
    public void testApplyTransaction() throws Exception {
        parameter.transaction.applyTransaction(enhancedSortedList);

        verifyTransactionApplied(enhancedSortedList, parameter.transaction);
    }


    protected abstract void verifyTransformedType(Transaction<F> transformedTransaction);

    protected abstract void verifyTransactionApplied(TransactionSortedList<T> enhancedSortedList, Transaction<T> transaction);
}