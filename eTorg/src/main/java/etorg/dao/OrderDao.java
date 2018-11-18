package etorg.dao;

import java.util.List;

import etorg.domain.Order;

/**
 * 
 * The order database interface.
 * 
 * @author Hans Reier Sigmond.
 *
 */
public interface OrderDao {
	
	/**
	 * Create an order in the database
	 * 
	 * @param order		The order to create.
	 */
	public void createOrder(Order order);
	
	/**
	 * Check if an order exists in the database.
	 * Please use this function and not readorder.
	 * This function do not keep the order in hibernate cache.
	 * 
	 * @param orderId	The order database identifier.
	 * @return			True or false
	 */
	public boolean existingOrder(long orderId);
	
	/**
	 * Read an order from the database.
	 * 
	 * @param orderId	The order database identifier.
	 * @return			The order object.
	 */
	public Order readOrder(long orderId);
	
	/**
	 * 
	 * Read the list of orders belonging to a user.
	 * 
	 * @param userId	The database user identifier.
	 * @return			List of orders.
	 */
	public List<Order> readOrders(long userId);
	
	/**
	 * 
	 * Update an order in the database.
	 * 
	 * @param order		The order to update.
	 */
	public void updateOrder(Order order);
	
	/**
	 * 
	 * Save or update an order in the database.
	 * 
	 * @param order		The order to save or update.
	 */
	public void saveOrder(Order order);
	
	/**
	 * 
	 * Delete an order with products in the database.
	 * 
	 * @param order		The order to delete.
	 */
	public void deleteOrder(Order order);
}
