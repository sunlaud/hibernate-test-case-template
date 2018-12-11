package org.hibernate.bugs;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.MutabilityPlan;

import java.util.Comparator;

public class DelegatingTypeDescriptor<T> implements JavaTypeDescriptor<T> {
    private final JavaTypeDescriptor<T> delegate;

    public DelegatingTypeDescriptor(JavaTypeDescriptor<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<T> getJavaTypeClass() {
        return delegate.getJavaTypeClass();
    }

    @Override
    public MutabilityPlan<T> getMutabilityPlan() {
        return delegate.getMutabilityPlan();
    }

    @Override
    public Comparator<T> getComparator() {
        return delegate.getComparator();
    }

    @Override
    public int extractHashCode(T value) {
        return delegate.extractHashCode(value);
    }

    @Override
    public boolean areEqual(T one, T another) {
        return delegate.areEqual(one, another);
    }

    @Override
    public String extractLoggableRepresentation(T value) {
        return delegate.extractLoggableRepresentation(value);
    }

    @Override
    public String toString(T value) {
        return delegate.toString(value);
    }

    @Override
    public T fromString(String string) {
        return delegate.fromString(string);
    }

    @Override
    public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
        return delegate.unwrap(value, type, options);
    }

    @Override
    public <X> T wrap(X value, WrapperOptions options) {
        return delegate.wrap(value, options);
    }
}
