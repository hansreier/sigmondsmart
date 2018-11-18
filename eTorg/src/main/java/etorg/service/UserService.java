package etorg.service;

import etorg.domain.Order;
import etorg.domain.User;

/**
 *  This interface defines a user database service.
 * 
 * @author Hans Reier Sigmond
 *
 */
public interface UserService {
	/**
	 * Create the user.
	 * 
	 * @param user			The user object.
	 */
	public void createUser(User user);
	
	/**
	 * Read the user. This method is ment to be used for login.
	 * 
	 * @param userName		The unique name (id) as given by the user.
	 * @return				The user.
	 */
	public User readUser(String userName);
	
	/**
	 * Cascade read the user with orders (The product lists are not read)
	 * 
	 * @param userId		The unique user id.
	 * @return				The user.
	 */
	public User readUser(long userId);
	
	/**
	 * Cascade read the user with orders
	 * The product list of the current order is read
	 * 
	 * @param userId		The unique user id.
	 * @return				The user (including new current order id)
	 */
	public User readUser(long userId, Order order);
	
	/**
	 * 
	 * Update the user.
	 * 
	 * @param user			The user object.
	 */
	public void updateUser(User user);
}
