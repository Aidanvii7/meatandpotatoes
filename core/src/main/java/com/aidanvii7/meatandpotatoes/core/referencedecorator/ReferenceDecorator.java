package com.aidanvii7.meatandpotatoes.core.referencedecorator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by aidan.mcwilliams on 14/04/2016.
 * <p>Simple wrapper class intended to guard against null referents.</p>
 * <p>A strong reference is held to a stub object.</p>
 * <p>Intended use case is for listener type objects with void methods.</p>
 */
public final class ReferenceDecorator<T> {

    private final T stub;
    private final Reference<T> reference;

    //region public API

    /***
     * @param reference Throws {@link NullPointerException} if null.
     * @param stub      the fallback object of the same type as referent that is strongly referenced.
     *                  {@link #get()} will return this if the reference contains null. Throws {@link NullPointerException} if null.
     */
    public ReferenceDecorator(@lombok.NonNull @NonNull Reference<T> reference, @lombok.NonNull @NonNull T stub) {
        this.stub = stub;
        this.reference = reference;
    }

    public static <T> ReferenceDecorator<T> createWeak(@NonNull T referent, @NonNull T stub) {
        return new ReferenceDecorator<>(new WeakReference<>(referent), stub);
    }


    /**
     * Gets the referent of the reference object, or the stub if the referent has been garbage collected.
     *
     * @return the referent of the reference object, or the stub if the referent has been garbage collected.
     */
    @NonNull
    public T get() {
        T referent = reference.get();
        if (referent != null) return referent;
        return stub;
    }

    /**
     * Gets the referent of the reference object, or the stub if the referent has been garbage collected.
     *
     * @param isStub out param for determining whether the returned referent was the stub or not.
     * @return the referent of the reference object, or the stub if the referent has been garbage collected.
     */
    @NonNull
    public T get(@NonNull AtomicBoolean isStub) {
        T referent = reference.get();
        if (referent != null) {
            isStub.set(false);
            return referent;
        } else {
            isStub.set(true);
            return stub;
        }
    }

    /**
     * Gets the referent of the reference object.
     *
     * @return the referent to which reference refers, or {@code null} if the
     * object has been cleared.
     */
    @Nullable
    public T getUnsafe() {
        return reference.get();
    }

    //endregion
}
