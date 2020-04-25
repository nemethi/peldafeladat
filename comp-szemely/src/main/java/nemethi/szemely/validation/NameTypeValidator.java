package nemethi.szemely.validation;

import nemethi.validation.Validator;

public interface NameTypeValidator<T, V> extends Validator<V> {

    T getNameType();

    void setNameType(T nameType);
}
