package org.hibernate.bugs;

import org.hibernate.type.descriptor.java.JavaTypeDescriptor;

import java.util.concurrent.atomic.AtomicInteger;

public class AlwaysUniqueHashTypeDescriptor<T> extends DelegatingTypeDescriptor<T> {
    private final AtomicInteger hash = new AtomicInteger(Integer.MIN_VALUE);

    public AlwaysUniqueHashTypeDescriptor(JavaTypeDescriptor<T> delegate) {
        super(delegate);
    }

    @Override
    public int extractHashCode(T value) {
        return hash.incrementAndGet();
    }
}
