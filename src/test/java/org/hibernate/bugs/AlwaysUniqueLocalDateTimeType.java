package org.hibernate.bugs;

import org.hibernate.type.LocalDateTimeType;

public class AlwaysUniqueLocalDateTimeType extends LocalDateTimeType {
    public AlwaysUniqueLocalDateTimeType() {
        setJavaTypeDescriptor(new AlwaysUniqueHashTypeDescriptor<>(getJavaTypeDescriptor()));
    }
}
