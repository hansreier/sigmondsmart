package etorg.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * This class defines an order.
 * 
 * @author Hans Reier Sigmond
 *
 */

// The table cannot be named order due to conflict with SQL order reserved word
@Entity
@Table(name = "cart")
public class Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 894494500716349070L;

	/**
	 * Constructor, create order and an empty product list.
	 */
	public Order() {
		setChangedDate(new Date());
		setProductList(new ArrayList<Product>());
		price = 0.0;
		status = Status.CART;
		orderJavaId = UUID.randomUUID().toString();
	}

	/**
	 * Constructor, create order with user.
	 */
	public Order(User user) {
		this();
		customer = user;
	}

	/**
	 * Include log4J
	 */
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * Order id (need not be universally unique) Is also not assigned a value
	 * before DB storage
	 */
	private long orderId;

	/**
	 * Selected in list.
	 */
	@Transient
	private boolean selected;
	/**
	 * Order id that is unique (casted from Java UUID)
	 */
	private String orderJavaId;

	private User customer;

	/**
	 * The enum defining status of the order.
	 */
	public enum Status {
		CART, DEFINED, PAID, SENT, RECEIVED;

		public String getName() {
			return name();
		}
	}

	/**
	 * The total price of the order.
	 */
	private double price;

	/**
	 * An optional string comment.
	 */
	private String comment;

	/**
	 * The status of the order.
	 */
	private Status status;

	/**
	 * The order date
	 */
	private Date changedDate;

	/**
	 * The list of products belonging to the order.
	 */

	private List<Product> productList;

	// getters and setters

	@Id
	@GeneratedValue
	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	@Transient
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Column(length = 40)
	public String getOrderJavaId() {
		return orderJavaId;
	}

	public void setOrderJavaId(String orderJavaId) {
		this.orderJavaId = orderJavaId;
	}

	@ManyToOne
	@JoinColumn(nullable = false)
	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	@Column
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Column
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Return true if the order is locked (non editable).
	 * 
	 * @return locked.
	 */
	@Transient
	public boolean getLocked() {
		return !((status == Status.CART) || (status == Status.DEFINED));
	}

	/**
	 * Return true if no produts is ordered
	 * 
	 * @return no products.
	 */
	@Transient
	public boolean getNoProducts() {
		return productList.isEmpty();
	}

	/**
	 * Return true if the order is in status defined (orderable).
	 * 
	 * @return defined.
	 */

	@Transient
	public boolean getDefined() {
		return (status == Status.DEFINED);
	}

	@Column
	public Date getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}

	// Note, hibernate annotation (not JPA) must be used else cascade operations
	// will not work
	@OneToMany(mappedBy = "cart", orphanRemoval = true)
	@Cascade({ CascadeType.SAVE_UPDATE })
	// @OrderBy("orderId")
	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	/**
	 * In this (premininary) implementation the Java unique id (UUID converted
	 * to string) is used. Contents is not checked (not required). (Use this Id
	 * or the database generated ID?).
	 * 
	 * @param order
	 *            Order to compare with.
	 * @return True if the id is identical, false if not.
	 */

	@Transient
	public boolean equals(Order order) {
		return (this.getOrderJavaId().equals(order.getOrderJavaId()));

	}

	/**
	 * Trace orders with product list, used for testing. Note: This function can
	 * sometimes be problematic when called outside of a transaction, because of
	 * Hibernate lazy loading.
	 */
	public void trace() {
		log.trace("------TRACE: order/cart with products------");

		log.trace("Order id: " + this.orderId + " javaId: " + this.orderJavaId + " price: " + this.getPrice()
				+ " changed date: " + this.changedDate + " comment: " + this.comment);
		if (!productList.isEmpty()) {
			for (Product product : productList) {
				log.trace("Product name: " + product.getName() + " id: " + product.getProductId() + " typeId: "
						+ product.getProductTypeId() + " count: " + product.getCount());
				if (product.getCart() == null)
					log.trace("Note: Cart not defined for product");
				else if (product.getCart().getOrderId() != orderId)
					log.trace("Note: OrderId not set correctly for cart: " + product.getCart().getOrderId());
			}
		} else
			log.trace("No products, product list table is null");
		if (customer == null)
			System.out.println("Note: Customer not defined for order!");
		else
			log.trace("User id: " + customer.getUserId() + " name: " + customer.getUserName());

		log.trace("-------------------------------------------");
	}

}
