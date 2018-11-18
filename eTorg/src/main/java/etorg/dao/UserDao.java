package etorg.dao;

import etorg.domain.User;

/**
 * 
 * The user database interface.
 * 
 * @author Hans Reier Sigmond
 *
 */
public interface UserDao {
	
	/**
	 * 
	 * Create a user in the database.
	 * 
	 * @param user			The user object.
	 */
	public void createUser(User user);
	
	/**
	 * 
	 * Read a user in the database given the user name.
	 * 
	 * @param userName		The unique user name (id) defined by the user.
	 * @return				The user object.
	 */
	public User readUser(String userName);
	
	/**
	 * 
	 * Read a user in the database given the database user id.
	 * 
	 * @param userId		The unique user id
	 * @return				The user object.
	 */
	public User readUser(long userId);
	
	/**
	 * Update a user in the database.
	 * 
	 * @param user			The user object.
	 */
	public void updateUser(User user);
}
