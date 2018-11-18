package etorg.service;

import java.util.List;

import etorg.domain.Product;

/**
 * 
 * This interface defines a database product service.
 * 
 * @author Hans Reier Sigmond
 *
 */
public interface ProductService {
	
	/**
	 * Create a product in the database.
	 * 
	 * @param product		The product object.
	 */
	public void createProduct(Product product);
	
	/**
	 * 
	 * Read a product in the database.
	 * 
	 * @param productId		The product database identifier
	 * @return				The product object.
	 */
	public Product readProduct(long productId);
	
	/**
	 * 
	 * Update a product in the database.
	 * 
	 * @param product		The product object.
	 */
	public void updateProduct(Product product);
	
	/**
	 * Read a list of products contained in an order.
	 * 
	 * @param orderId		The database generated order id.
	 * @return				A list of products.
	 */
	public List<Product> readProducts(long orderId);
}
