package etorg.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * This class defines the product inventory with products to select from.
 * 
 * @author Hans Reier Sigmond
 *
 */
public class ProductInventory implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Include log4J
	 */
	protected Log log = LogFactory.getLog(this.getClass());
	/**
	 * Define a list of all products.
	 */
	// private DataModel<Product> productList;
	private List<Product> productList;

	/**
	 * Product inventory constructor.
	 */
	public ProductInventory() {
		// productList = new ListDataModel<Product>(getPopulatedProductList());
		productList = new ArrayList<Product>(getPopulatedProductList());
	}

	// getters and setters/
	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	/**
	 * Return populated list of products. Instead of calling DB as we should do.
	 * 
	 * @return List of products.
	 */
	private List<Product> getPopulatedProductList() {
		log.debug("Populating product list");
		List<Product> productList = new ArrayList<Product>();
		productList.add(new Product(1, "First Price bananer", "banan", "Kiwi",
				"Costa Rica", 3.00));
		productList.add(new Product(2, "Dole bananer", "banan", "Dole",
				"Kongo", 3.80));
		productList.add(new Product(3, "Fairtrade bananer", "banan",
				"Fairtrade", "Sør Afrika", 4.20));
		productList.add(new Product(4, "Gravensten", "eple", "Bama", "Norge",
				3.60));
		productList.add(new Product(5, "Pink Lady", "eple", "", "New Zealand",
				6.00));
		productList
				.add(new Product(6, "Pink Lady", "eple", "", "Spania", 6.00));
		productList.add(new Product(7, "Lett Gulost", "gulost", "Tine",
				"Norge", 3.70));
		productList.add(new Product(8, "Ekte Geitost", "brunost", "Tine",
				"Norge", 3.70));
		productList.add(new Product(9, "Gudbrandsdalsost", "brunost", "Tine",
				"Norge", 3.70));
		productList.add(new Product(10, "Lettere Gudbrandsdalsost", "brunost",
				"Tine", "Norge", 3.60));
		productList.add(new Product(11, "Edamer", "gulost", "Tine",
				"Nederland", 3.80));
		return productList;
	}
}
