package com.turismo.exceptions;

/**
 * This exception occurs when the expected conditions in a test do not fulfilled
 * 
 * @author Brayan Hernandez
 *
 */
public class TestExpectException extends Exception {

	private static final long serialVersionUID = 1L;

	public TestExpectException(String message) {
		super(message);
	}

	public TestExpectException(Throwable cause) {
		super(cause);
	}

	public TestExpectException(String message, Throwable cause) {
		super(message, cause);
	}

}
