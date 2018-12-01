package etorg.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
//import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class defines a user in the eTorg system. A user in this version is also
 * customer. JPA annotations are used in this version, with some hibernate
 * extensions.
 * 
 * @author Hans Reier Sigmond
 * 
 *         User is mapped to customer because user is reserved word in Postgres.
 *
 *
 * @Entity
 * @Table(name = Database.tableName) public class Database implements
 *             Serializable { public static final String tableName =
 *             "TABLE_1";//this variable you can reference in other portions of
 *             your code. Of course you cannot change it. ............... }
 */

@Entity
// @Table(name="customer")
public class User implements Serializable {
	//public final String tableName = "customer";
	private static final long serialVersionUID = 1L;

	/**
	 * Include logback
	 */
	private static final Logger log = LoggerFactory.getLogger(User.class);

	private long userId;

	private String userName;

	/**
	 * User id/key
	 */
	private String userJavaId;

	/**
	 * The users first name
	 */
	private String firstName;

	/**
	 * The users last name
	 */
	private String lastName;

	/**
	 * Eventually enter company or organization of user
	 */
	private String company;

	/**
	 * The user email
	 */
	private String email;

	/**
	 * The users primary phone.
	 */
	private String phone1;

	/**
	 * The users secondary phone.
	 */
	private String phone2;

	/**
	 * The user address, e.g. Tor Jonssons vei 4d.
	 */
	private String address;

	/**
	 * Eventually enter mailbox, e.g. PB 2334 Skøyen.
	 */
	private String mailbox;

	/**
	 * The zipcode, e.g. 2390.
	 */
	private String zipCode;

	/**
	 * location: Town or village or location connected with zipCode, e.g. Moelv.
	 */
	private String location;

	/**
	 * The changed date of the user information
	 */
	private Date changedDate;

	private String storedPassword;

	/**
	 * The users (customers) orders (previous and current).
	 */
	private List<Order> orders;

	/**
	 * Is the user logged in or not.
	 */
	@Transient
	private boolean loggedIn;

	/**
	 * Current user
	 */
	@Transient
	private Order currentOrder;

	/**
	 * User constructor.
	 */
	public User() {
		loggedIn = false;
		setChangedDate(new Date());
		orders = new ArrayList<Order>();
		userJavaId = UUID.randomUUID().toString();
	}

	// Getters and setters

	@Id
	@GeneratedValue
	@Column
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@NaturalId
	@Column(nullable = false, length = 20)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(length = 40)
	public String getUserJavaId() {
		return userJavaId;
	}

	public void setUserJavaId(String userJavaId) {
		this.userJavaId = userJavaId;
	}

	@Column(length = 80)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(length = 80)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(length = 80)
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Column(length = 50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(length = 50)
	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	@Column(length = 50)
	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	@Column
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(length = 80)
	public String getMailbox() {
		return mailbox;
	}

	public void setMailbox(String mailbox) {
		this.mailbox = mailbox;
	}

	@Column(length = 10)
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Column(length = 80)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column
	public Date getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}

	@Column(length = 10)
	public String getStoredPassword() {
		return storedPassword;
	}

	public void setStoredPassword(String storedPassword) {
		this.storedPassword = storedPassword;
	}

	// Note, hibernate annotation (not JPA) must be used else cascade operations
	// will not work
	@OneToMany(mappedBy = "customer")
	@Cascade({ CascadeType.SAVE_UPDATE })
	@OrderBy("orderId")
	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	@Transient
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	@Transient
	public Order getCurrentOrder() {
		return currentOrder;
	}

	public void setCurrentOrder(Order currentOrder) {
		this.currentOrder = currentOrder;
	}

	/**
	 * Trace user with orders and products (used for debugging) Note: Crashes
	 * sometime due to hibernate lazy loading (call should be done inside a
	 * transaction) Comment: 07102010: Seems also to be able to crash even
	 * inside a transaction when calling update twice (change password) Comment
	 * 13102010: The trace function in itself triggers a really not required sql
	 * select products. This is the reason for crashing when outside of a
	 * transaction. This is because cascading and lazy loading is turned on (do
	 * not need to read before required)
	 */
	public void trace() {
		log.trace("------TRACE: User with orders------");
		log.trace("User id: " + this.userId + " javaId: " + this.userJavaId + " Id: " + this.userName + " Name: "
				+ this.firstName + " " + this.lastName);
		if (orders != null) {
			for (Order order : orders) {
				log.trace("Order: " + order.getOrderId() + " " + order.getOrderJavaId() + " Price: " + order.getPrice()
						+ " Status: " + order.getStatus() + " Desc: " + order.getComment());
				// Triggers database read (due to lazy loading)
				List<Product> products = order.getProductList();
				if (!products.isEmpty()) {
					for (Product product : products) {
						log.trace("Product name: " + product.getName() + " id: " + product.getProductId() + " typeId: "
								+ product.getProductTypeId() + " count: " + product.getCount());
						if (product.getCart() == null)
							log.trace("Note: Cart not defined for product");
						else if ((product.getCart().getOrderId() != order.getOrderId()))
							log.trace("Note: OrderId not set correctly for cart: " + product.getCart().getOrderId());
					}
				}
				if (order.getCustomer() == null)
					log.trace("Note: Customer not defined for order!");
				else if ((order.getCustomer().getUserId() != this.userId))
					log.trace("Note: UserId not set correctly for order: " + order.getCustomer().getUserId());
			}
		} else
			log.trace("No orders, orders table is null");
		log.trace("-----------------------------------");
	}

}
