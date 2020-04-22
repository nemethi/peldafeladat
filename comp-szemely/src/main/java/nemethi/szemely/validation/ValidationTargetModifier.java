package nemethi.szemely.validation;

import validation.Validator;

public interface ValidationTargetModifier<T> extends Validator<T> {

    T getModifiedTarget();
}
