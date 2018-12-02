package etorg.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import etorg.dao.OrderDao;
import etorg.domain.Order;

/**
 * @author Hans Reier Sigmond Order database interface
 */

// @Repository injects Hibernate sessionfactory
// and enables component scanning and exeption translation
@Repository
public class OrderDaoImpl implements OrderDao {

	@Autowired(required = true)
	private SessionFactory sessionFactory;

	// seems not to be required, if required should be included in interface.
	// public void setSessionFactory(SessionFactory sessionFactory) {
	// this.sessionFactory = sessionFactory;
	// }

	public void createOrder(Order order) {
		sessionFactory.getCurrentSession().save(order);
	}

	public Order readOrder(long orderId) {
		return (Order) sessionFactory.getCurrentSession().get(Order.class, orderId);
	}

	public boolean existingOrder(long orderId) {
		boolean existing;
		Session session = sessionFactory.getCurrentSession();
		Order order = (Order) session.get(Order.class, orderId);
		existing = (order != null);
		session.evict(order); // remove read order from hibernate cache
		return existing;
	}

	@SuppressWarnings("unchecked")
	public List<Order> readOrders(long userId) {
		return (List<Order>) sessionFactory.getCurrentSession().createCriteria(Order.class)
				.add(Restrictions.eq("customer.userId", userId)).list();
	}

	// @Query("SELECT * FROM Order o WHERE o.status = ?1");
	// public List<Order> readOrders(long userId);

	public void updateOrder(Order order) {
		sessionFactory.getCurrentSession().update(order);
	}

	public void saveOrder(Order order) {
		sessionFactory.getCurrentSession().saveOrUpdate(order);
	}

	public void deleteOrder(Order order) {
		sessionFactory.getCurrentSession().delete(order);
	}

}
