package com.littlestore.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.littlestore.entity.Customer;
import com.littlestore.service.CustomerService;

@Component
public class CustomerFormValidator implements Validator {

	@Autowired
	private CustomerService customerService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Customer.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Customer customer = (Customer) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty", "This field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty", "This field is required.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty", "This field is required.");
		if (customer.getEmail().length() < 6 || customer.getEmail().length() > 50)
			errors.rejectValue("email", "Size.customerForm.email", "Please use between 6 and 50 characters.");
		if (customerService.findByEmail(customer.getEmail()) != null)
			errors.rejectValue("email", "Duplicate.customerForm.email", "Email is already registered. Please try another or log in if it is yours.");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
		if (customer.getPassword().length() < 8 || customer.getPassword().length() > 32)
			errors.rejectValue("password", "Size.customerForm.password", "Password must be at least 8 characters.");
		if (!customer.getPasswordConfirm().equals(customer.getPassword()))
			errors.rejectValue("passwordConfirm", "Diff.customerForm.passwordConfirm", "Passwords must match.");
		
		if (customer.getPhone().length() > 16)
			errors.rejectValue("phone", "Size.customerForm.phone", "Phone number must be less than 16 characters (including dashes and parentheses).");
	}
}
