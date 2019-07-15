package com.test.utilities;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.stereotype.Service;

import com.test.exception.BusinessException;

@Service
public class ValidatorUtilities<T> {

	
	public void validate(T t) throws BusinessException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> violations = validator.validate(t);
		
		for (ConstraintViolation<T> violation : violations) {		    
		    throw new BusinessException(violation.getMessage());
		}
	}
}
