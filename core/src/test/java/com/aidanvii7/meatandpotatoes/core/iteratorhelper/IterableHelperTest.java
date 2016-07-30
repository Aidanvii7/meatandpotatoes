package com.aidanvii7.meatandpotatoes.core.iteratorhelper;

import com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.cancel;
import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.iterate;
import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.skip;
import static com.aidanvii7.meatandpotatoes.core.iteratorhelper.IterableHelper.skipRemoveCurrent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by aidan.mcwilliams on 27/04/2016.
 */
@RunWith(Parameterized.class)
public class IterableHelperTest {

    //region setup

    private final TestParameter parameter;

    private static class TestParameter {
        private final Collection<TestEntry> input;
        private final int expectedSize;

        private TestParameter(Collection<TestEntry> input, int expectedSize) {
            this.input = new ArrayList<>(input);
            this.expectedSize = expectedSize;
        }
    }

    private static class TestEntry {
        private final boolean val;

        public TestEntry(boolean val) {
            this.val = val;
        }
    }


    public IterableHelperTest(TestParameter parameter) {
        this.parameter = parameter;
    }

    @Parameterized.Parameters
    public static Collection<TestParameter> parameters() {
        Collection<TestParameter> parameters = new ArrayList<>();
        Collection<TestEntry> input = new ArrayList<>();
        addTestParam(parameters, input, 3, 0);
        addTestParam(parameters, input, 2, 1);
        addTestParam(parameters, input, 1, 10);
        return parameters;
    }

    private static void addTestParam(Collection<TestParameter> parameters, Collection<TestEntry> input, int inCountValid, int inCountInvalid) {
        for (int i = 0; i < inCountValid; i++)
            input.add(new TestEntry(false));
        for (int i = 0; i < inCountInvalid; i++)
            input.add(new TestEntry(true));

        parameters.add(new TestParameter(input, inCountValid));
        input.clear();
    }

    //endregion

    //region tests

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<IterableHelper> constructor = IterableHelper.class.getDeclaredConstructor();
        assertEquals(Modifier.isPrivate(constructor.getModifiers()), true);
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testIterate() throws Exception {

        AtomicReference<TestEntry> lastTestEntryRef = new AtomicReference<>(new TestEntry(false));

        iterate(parameter.input, entry -> {
            assertNotEquals("Last entry matches the current entry.", entry, lastTestEntryRef.get());
            lastTestEntryRef.set(entry);
            if (entry.val) skipRemoveCurrent();
        });
        int endSize = parameter.input.size();

        assertEquals("end size does not match expected size", endSize, parameter.expectedSize);
    }

    @Test
    public void testIterateSkip() throws Exception {

        AtomicInteger skippedCount = new AtomicInteger(0);

        int expectedSkippedCount = 0;
        for (TestEntry testEntry : parameter.input)
            if (testEntry.val) expectedSkippedCount++;

        AtomicReference<TestEntry> lastTestEntryRef = new AtomicReference<>(new TestEntry(false));
        iterate(parameter.input, entry -> {
            assertNotEquals("Last entry matches the current entry.", entry, lastTestEntryRef.get());

            if (entry.val) {
                skippedCount.incrementAndGet();
                lastTestEntryRef.set(entry);
                skip();
            }
            lastTestEntryRef.set(entry);
        });
        assertEquals("TODO", skippedCount.get(), expectedSkippedCount);
    }

    @Test
    public void testIterateCancel() throws Exception {

        if (shouldRunTest()) {

            AtomicReference<TestEntry> lastTestEntryRef = new AtomicReference<>(new TestEntry(false));

            AtomicInteger iterCount = new AtomicInteger(0);
            iterate(parameter.input, entry -> {
                assertNotEquals("Last entry matches the current entry.", entry, lastTestEntryRef.get());
                lastTestEntryRef.set(entry);
                if (entry.val) cancel();
                iterCount.incrementAndGet();
            });
            assertNotEquals("TODO", iterCount.get(), parameter.input.size());
        }
    }

    private boolean shouldRunTest() {
        boolean runTest = false;
        for (TestEntry e : parameter.input) {
            if (e.val) {
                runTest = true;
                break;
            }
        }
        return runTest;
    }

    //endregion
}