package etorg.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import etorg.dao.ProductDao;
import etorg.dao.UserDao;
import etorg.domain.Order;
import etorg.domain.User;
import etorg.service.UserService;

/**
 * This class implements a user database service.
 * 
 * @author Hans Reier Sigmond
 *
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ProductDao productDao;
	
	/**
	 * Include logback
	 */
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	/**
	 * Call a function to trace the contents of a user
	 * @param user		The user to trace
	 */
	private void trace(User user) {
		if (log.isTraceEnabled()) {
			if (user == null) log.trace("No user"); else
				user.trace();	
		}
	}
	
	@Transactional
	public void createUser(User user) {
		trace(user);
		userDao.createUser(user);
	}

	@Transactional (readOnly=true)
	public User readUser(String userName) {
		log.info("readUser");
		User user = userDao.readUser(userName);
		log.info("readUser completed");
		trace(user);
		return user;
	}
	
	@Transactional (readOnly=true)
	public User readUser(long userId) {
		User user = userDao.readUser(userId);
		// force read of orders to avoid reading outside transaction (must do something with the orders to trigger this)
		if (user !=null) user.getOrders().size();
		//  To explicitly read the orders are actually not required
		//	if (user !=null) user.setOrders(orderDao.readOrders(user.getUserId()));
		trace(user);
		return user;
	}
	
	@Transactional (readOnly=true)
	public User readUser(long userId, Order currentOrder) {
		log.info("read user with orders");
		User user = userDao.readUser(userId);
		// force read of orders to avoid reading outside transaction (must do something with the orders to trigger this)
		if (user !=null) {
			user.setCurrentOrder(null);
			for (Order o: user.getOrders()) {
				if (o.getOrderJavaId().equals(currentOrder.getOrderJavaId()))  {
					user.setCurrentOrder(o);
					o.setProductList(productDao.readProducts(o.getOrderId()));
					log.debug("current order is updated from DB: "+ currentOrder.getOrderJavaId());
				}
			}
		}
		trace(user);
		return user;
	}

	@Transactional
	public void updateUser(User user) {
		trace(user);
		userDao.updateUser(user);
	}

	

}
