package etorg.dao.impl;

import java.util.List;
import etorg.domain.Order;
import etorg.domain.Order_;
import etorg.domain.Product_;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import etorg.dao.ProductDao;
import etorg.domain.Product;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;


// @Repository injects Hibernate sessionfactory 
// and enables component scanning and exeption translation
@Repository
@Transactional
public class ProductDaoImpl implements ProductDao {

	private static final Logger log = LoggerFactory.getLogger(ProductDaoImpl.class);
	
	@Autowired(required=true)
	private SessionFactory sessionFactory;

//  seems not to be required, if required should be included in interface.	
//	public void setSessionFactory(SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;
//		}

	public void createProduct(Product product) {
			sessionFactory.getCurrentSession().save(product);
	}
	
	public Product readProduct(long productId) {
		return sessionFactory.getCurrentSession().get(Product.class,productId);
	}	

	public List<Product> readProducts(long orderId) {
		log.info("Read products");
		// Using Hibernate sessionFactory and not JPA Entitymanager
		Session session = sessionFactory.getCurrentSession();
		// build query
		// As seen in generated H2 database
		// select * from Product p inner join cart c
		// on p.cart_orderId = c.orderId
		// where c.orderId = orderId:
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		Root<Product> product = cq.from(Product.class);
		Join<Product, Order> order = product.join(Product_.cart);
		cq.select(product);
		cq.where(cb.equal(order.get(Order_.orderId), orderId));
		//execute query
		TypedQuery<Product> typedQuery = session.createQuery(cq);
		List<Product> products = typedQuery.getResultList();
		if (log.isDebugEnabled()) {
			products.forEach(p ->log.debug("Product: {} Count: {} Description: {} ", p.getName(), p.getCount(), p.getDescription()));
		}
		return products;

		//return (List<Product>) sessionFactory.getCurrentSession().createCriteria(Product.class)
		//	.add( Restrictions.eq("cart.orderId", orderId))
		//	.list();
	}
	
	public void updateProduct(Product product) {
			sessionFactory.getCurrentSession().update(product);
	}

}
