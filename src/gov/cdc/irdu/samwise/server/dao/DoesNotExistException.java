/**
 * 
 */
package gov.cdc.irdu.samwise.server.dao;

/**
 *
 * @author Joel M. Rives
 * Feb 25, 2011
 */
public class DoesNotExistException extends RuntimeException {
	
	private static final long serialVersionUID = -8017454681854437169L;

	public DoesNotExistException(String objectName) {
		super("The object " + objectName + " does not exists in persistant storage");
	}
	
}
