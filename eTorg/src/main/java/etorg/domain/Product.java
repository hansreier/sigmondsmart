package etorg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import etorg.domain.ProductType;

/**
 * 
 * This class defines a product.
 *	
 * @author Hans Reier Sigmond
 * 
 * 07092010 Comment: If I am going to make a separate ProductType GUI including an ProductType entity:
 * This inheritance strategy to enable reuse of some attributes will create problems due to hibernate.
 * ProductType can either be a separate table or included in Product (like it is now), not both !?
 *
 */
@Entity
public class Product extends ProductType  {

	private static final long serialVersionUID = 1L;

	private long ProductId;
	
	/**
	 * Selected in list.
	 */
	@Transient
	private boolean selected;
	
	//cannot use order here, generated SQL will conflict with reserved word order
	private Order cart;
	
	/**
	 * No of items.
	 */
	private int count;
	
	/**
	 * 
	 * Constructor.
	 * 
	 * @param name				The name of the product.
	 * @param description		Product description.
	 * @param manufacturer		The manufacturer of the product.
	 * @param country			The country where the product is produced/originated.
	 * @param price				The price of the product.
	 */
	public Product(int productTypeId, String name, String description, String manufacturer,
			String country, double price) {
		super(productTypeId, name, description, manufacturer, country, price);
		selected = false;
		count = 1;
	}
	
	/**
	 * 
	 * Copy constructor.
	 * 
	 * @param order				The order that shall contain the product
	 * @param product			The product instance to copy.
	 */
	public Product (Order order, Product product) {
		super(product.getProductTypeId(), product.getName(),product.getDescription(), 
				product.getManufacturer(), product.getCountry(), product.getPrice());
		selected = product.selected;
		count = product.count;
		cart = product.cart;
		setCart(order);
		// count = 1;
	}
	
	public Product() {
		super();
	}
	
	//getters and setters
	@Id
	@GeneratedValue
	@Column(length=40)
	public long getProductId() {
		return ProductId;
	}

	public void setProductId(long productId) {
		ProductId = productId;
	}

	@ManyToOne
	@JoinColumn(nullable= false)
	public Order getCart() {
		return cart;
	}

	public void setCart(Order cart) {
		this.cart = cart;
	}

	
	@Transient
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Column
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	// calculated field.
	@Transient
	public double getTotal() {
		return getCount()*getPrice();
	}
   
}
