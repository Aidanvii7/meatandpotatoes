package com.aidanvii7.meatandpotatoes.core.referencedecorator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by aidan.mcwilliams on 14/04/2016.
 */
@RunWith(JUnit4.class)
public class ReferenceDecoratorTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testConstructor() throws Exception {
        exception.expect(NullPointerException.class);
        new ReferenceDecorator<>(new WeakReference<>(null), null);
    }

    @Test
    public void testGet() throws Exception {
        ReferenceDecorator<Object> weakDecorator;
        {
            // create test objects
            Object stubReferent1 = new Object();
            Object stubReferent2 = new Object();

            weakDecorator = ReferenceDecorator.createWeak(stubReferent1, stubReferent2);
            assertNotNull("return value is null before GC", weakDecorator.get());
            assertEquals("return value is not equal to expected", weakDecorator.get(), stubReferent1);

            // wait for the test objects to be garbage collected
            stubReferent1 = null;
            while (weakDecorator.getUnsafe() != null) {
                System.gc();
            }
        }
        assertNotNull("return value is null after GC", weakDecorator.get());
    }

    @Test
    public void testGetIsStub() throws Exception {
        ReferenceDecorator<Object> weakDecorator;
        AtomicBoolean isStub = new AtomicBoolean(false);
        {
            // create test objects
            Object stubReferent1 = new Object();
            Object stubReferent2 = new Object();

            weakDecorator = ReferenceDecorator.createWeak(stubReferent1, stubReferent2);
            assertNotNull("return value is null before GC", weakDecorator.get(isStub));
            assertEquals("return value is not equal to expected", weakDecorator.get(isStub), stubReferent1);
            assertFalse("isStub should be false to indicate that the referent has not been cleared", isStub.get());


            // wait for the test objects to be garbage collected
            stubReferent1 = null;
            while (weakDecorator.getUnsafe() != null) {
                System.gc();
            }
        }
        assertNotNull("return value is null after GC", weakDecorator.get(isStub));
        assertTrue("isStub should be true to indicate that the referent has been cleared", isStub.get());
    }

    @Test
    public void testGetUnsafe() throws Exception {
        ReferenceDecorator<Object> weakDecorator;
        {
            // create test objects
            Object stubReferent1 = new Object();
            Object stubReferent2 = new Object();

            weakDecorator = ReferenceDecorator.createWeak(stubReferent1, stubReferent2);

            // wait for the test objects to be garbage collected
            stubReferent1 = null;
            while (weakDecorator.getUnsafe() != null) {
                System.gc();
            }
        }
        assertEquals("return value is not null after GC", weakDecorator.getUnsafe(), null);
    }
}