package etorg.service;

import java.util.List;

import etorg.domain.Order;

/**
 * This interface defines an order database service.
 * 
 * @author Hans Reier Sigmond
 *
 */
public interface OrderService {
	/**
	 * Create order.
	 * 
	 * @param order			The order to create.
	 */
	public void createOrder(Order order);
	
	/**
	 * Read order with products given the database id.
	 * 
	 * @param orderId		The database id of the order to read.
	 * @return				Order.
	 */
	public Order readOrder(long orderId);
	
	/**
	 * Update order.
	 * 
	 * @param order			The order to update.
	 */
	public void updateOrder(Order order);
	
	/**
	 * Save order (insert or update).
	 * 
	 * @param order			The order to update.
	 */
	public void saveOrder(Order order);
	
	/**
	 * 
	 * Read orders given the user id. The product list is not read.
	 * 
	 * @param userId		The user id of the orders.
	 * @return				Order List.
	 */
	public List<Order> readOrders(long userId);
	
	/**
	 * 
	 * Delete orders given a list of orders.
	 * 
	 * @param removeList	The user id of the orders.
	 * 
	 */
	public void deleteOrders(List<Order> removeList);
}
