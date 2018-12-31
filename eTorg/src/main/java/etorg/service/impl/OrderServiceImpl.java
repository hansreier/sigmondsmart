package etorg.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import etorg.dao.OrderDao;
import etorg.domain.Order;
import etorg.domain.Product;
import etorg.service.OrderService;

/**
 * 
 * This class implements an order database service.
 * The services are reading av writing order related information to the database.
 * 
 * 
 * @author Hans Reier Sigmond
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;

	/**
	 * Include logback
	 */
	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	/**
	 * Call a function to trace the contents of a user
	 * @param order		The user to trace
	 */
	private void trace(Order order) {
		if (log.isTraceEnabled()) {
			if (order == null) log.trace("No order"); else
				order.trace();	
		}
	}
	
	@Transactional
	public void createOrder(Order order) {
		trace(order);
		orderDao.createOrder(order);
	}
	
	@Transactional (readOnly=true)
	public Order readOrder(long orderId) {
		Order order = orderDao.readOrder(orderId);
		// Force read of products to avoid reading outside transaction (must do something with the products to trigger this)
		if (order !=null) order.getProductList().size();
		//  To explicitly read the products are actually not required
		// if (order !=null) order.setProductList(productDao.readProducts(orderId));
		trace(order);
		return order;
	}

	@Transactional
	public void updateOrder(Order order) {
		if (log.isTraceEnabled()) order.trace();
		orderDao.updateOrder(order);
	}
	
	@Transactional
	public void saveOrder(Order order) {
		// could alternatively used a save function (equally performing code)
		// if the primary key is defined, an update is run in both cases
		trace(order);
		//orderDao.saveOrder(order);
		// If the order is deleted elsewhere, just doing saveOrder creates problems.
		// Either the order is the current session must be emptied
		// or a new instance of the order with the contents stored in session must be created.
		// In this implementation a new instance is created.
		// An alternative is to never allow the same user to be logged in twice
		// This avoids the problem with database and session data beeing out of sync.
		
		if (order.getOrderId() == 0) 
			orderDao.createOrder(order);
		else  {
			if (orderDao.existingOrder(order.getOrderId())) {
				orderDao.updateOrder(order);
			}	
			else {
				// The order is deleted elsewhere.
				// I do not like this implementation, but cannot do it any other way.
				// The problem is that the deleted order is still there in Hibernate cache
				// (Trying to remove it from cache does not help).
				// Here I create a completely new instance of order with products.
				List<Product> pl= new ArrayList<Product>();
				order.setOrderId(0);
				for (Product p: order.getProductList()) {
					p.setProductId(0);
					pl.add(p);
				}
				order.setProductList(pl);
				orderDao.saveOrder(order);
			}	
		}
		
	}

	@Transactional
	public List<Order> readOrders(long userId) {
		log.info("Read orders");
		return orderDao.readOrders(userId);
	}
	
	@Transactional
	public void deleteOrders(List<Order> orders) {
		log.info("Delete orders");
		for (Order order: orders) {
			orderDao.deleteOrder(order);
		}
	}

}
