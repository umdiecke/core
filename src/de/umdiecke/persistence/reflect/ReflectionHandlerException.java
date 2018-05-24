/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.persistence.reflect;

/**
 * Exceptionbehandlung des ReflectionHandlers.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:33:11
 *
 */
public class ReflectionHandlerException extends RuntimeException {

	private static final long serialVersionUID = -1310735235312003996L;

	private final String methodName;
	private final Class<?>[] argTypes;
	private final Object[] parameters;

	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 *
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	public ReflectionHandlerException(final String message) {
		super(message);
		this.methodName = null;
		this.argTypes = null;
		this.parameters = null;
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 *
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @param methodName
	 *            the name of the invoked method
	 * @param argTypes
	 *            the types of the invoked method parameters
	 * @param parameters
	 *            the parameters of the invoked method
	 */
	public ReflectionHandlerException(final String message, final String methodName, final Class<?>[] argTypes, final Object[] parameters) {
		super(message);
		this.methodName = methodName;
		this.argTypes = argTypes;
		this.parameters = parameters;
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public ReflectionHandlerException(final String message, final Throwable cause) {
		super(message, cause);

		this.methodName = null;
		this.argTypes = null;
		this.parameters = null;
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @param methodName
	 *            the name of the invoked method
	 * @param argTypes
	 *            the types of the invoked method parameters
	 * @param parameters
	 *            the parameters of the invoked method
	 */
	public ReflectionHandlerException(final String message, final Throwable cause, final String methodName, final Class<?>[] argTypes, final Object[] parameters) {
		super(message, cause);

		this.methodName = methodName;
		this.argTypes = argTypes;
		this.parameters = parameters;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return this.methodName;
	}

	/**
	 * @return the argTypes
	 */
	public Class<?>[] getArgTypes() {
		return this.argTypes;
	}

	/**
	 * @return the parameters
	 */
	public Object[] getParameters() {
		return this.parameters;
	}
}