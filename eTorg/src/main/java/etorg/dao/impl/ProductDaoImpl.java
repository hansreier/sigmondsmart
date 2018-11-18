package etorg.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import etorg.dao.ProductDao;
import etorg.domain.Product;


// @Repository injects Hibernate sessionfactory 
// and enables component scanning and exeption translation
@Repository
@Transactional
public class ProductDaoImpl implements ProductDao {
	
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
		return (Product) sessionFactory.getCurrentSession().get(Product.class,productId);
	}	
	
	@SuppressWarnings("unchecked")
	public List<Product> readProducts(long orderId) {
		return (List<Product>) sessionFactory.getCurrentSession().createCriteria(Product.class)
			.add( Restrictions.eq("cart.orderId", orderId))
			.list();
	}
	
	public void updateProduct(Product product) {
			sessionFactory.getCurrentSession().update(product);
	}

}
