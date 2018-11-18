package etorg.dao;

/**
 * 
 * The product database interface.
 * 
 * @author Hans Reier Sigmond.
 *
 */
import java.util.List;

import etorg.domain.Product;

public interface ProductDao {
	
	/**
	 * Create a product in the database.
	 * 
	 * @param product		The product instance.
	 */
	public void createProduct(Product product);
	
	/**
	 * Read a product in the database.
	 * 
	 * @param productId		The product database id.
	 * @return				The product object.
	 */
	public Product readProduct(long productId);
	
	/**
	 * Read a list of products given a order.
	 * 
	 * @param orderId		The order database id.
	 * @return				The product.
	 */
	public List<Product> readProducts(long orderId);
	
	/**
	 * Update a product in the database.
	 * 
	 * @param product		The product instance.
	 */
	public void updateProduct(Product product);
}
