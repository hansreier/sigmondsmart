package etorg.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import etorg.dao.ProductDao;
import etorg.domain.Product;
import etorg.service.ProductService;

/**
 * 
 * The class implements a product database service.
 * 
 * @author Hans Reier Sigmond
 *
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private ProductDao productDao;
	
	@Transactional
	public void createProduct(Product product) {
		productDao.createProduct(product);
	}

	@Transactional (readOnly=true)
	public Product readProduct(long productId) {
		return productDao.readProduct(productId);
	}

	@Transactional
	public void updateProduct(Product product) {
		productDao.updateProduct(product);
	}

	@Transactional
	public List<Product> readProducts(long orderId) {
		return productDao.readProducts(orderId);
	}

}
