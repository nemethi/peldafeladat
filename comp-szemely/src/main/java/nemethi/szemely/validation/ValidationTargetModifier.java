package nemethi.szemely.validation;

import nemethi.validation.Validator;

public interface ValidationTargetModifier<T> extends Validator<T> {

    T getModifiedTarget();
}
