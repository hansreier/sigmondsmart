package etorg.dao.impl;

import java.util.List;

import etorg.domain.Order_;
import etorg.domain.User;
import etorg.domain.User_;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import etorg.dao.OrderDao;
import etorg.domain.Order;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * @author Hans Reier Sigmond Order database interface
 */

// @Repository injects Hibernate sessionfactory
// and enables component scanning and exeption translation
@Repository
public class OrderDaoImpl implements OrderDao {

	private static final Logger log = LoggerFactory.getLogger(OrderDaoImpl.class);

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

	public List<Order> readOrders(long userId) {
		log.info("Read orders");
		//Using Hibernate sessionFactory and not JPA Entitymanager
		Session session = sessionFactory.getCurrentSession();
		//build query
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Order> cq = cb.createQuery(Order.class);
		Root<Order> order = cq.from(Order.class);
		Join<Order, User> user = order.join(Order_.customer);
		cq.select(order);
		cq.where(cb.equal(user.get(User_.userId), userId));
		//execute query
		TypedQuery<Order> typedQuery = session.createQuery(cq);
		List<Order> orders = typedQuery.getResultList();
		if (log.isDebugEnabled()) {
			orders.forEach(num ->log.debug("Order: " + num + num.getOrderId()));
		}
		return orders;

	//	return (List<Order>) sessionFactory.getCurrentSession().createCriteria(Order.class)
	//			.add(Restrictions.eq("customer.userId", userId)).list();
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
