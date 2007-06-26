package org.cubictest.export.exceptions;

import org.cubictest.common.exception.CubicException;

/**
 * Exception indicating that test has failed (had assertion errors).
 * 
 * @author Christian Schwarz
 *
 */
public class TestFailedException extends CubicException {


	public TestFailedException(String message) {
		super(message);
	}

	public TestFailedException(String message, Exception cause) {
		super(cause, message);
	}

	private static final long serialVersionUID = 1L;

}
