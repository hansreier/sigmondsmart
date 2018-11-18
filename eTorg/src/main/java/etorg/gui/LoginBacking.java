package etorg.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import etorg.domain.Order;
import etorg.domain.User;
import etorg.service.OrderService;
import etorg.service.UserService;


/**
 * The login backing bean.
 * 
 * The beans handles user login and administration.
 * A user can be a private customer or a company/organization.
 * A user is in this version equivalent to a customer.
 * 
 * This backing bean can be used in session, request or view scope.
 * Currently the Spring container is used and a  view scope. 
 * The view scope is a special JSF scope, that is defined as a Spring custom scope.
 * 
 * @author Hans Reier Sigmond
 */
// @ManagedBean
// @SessionScoped

@Component
@Scope("request")

public class LoginBacking extends Backing implements Serializable {
	
	/**
	 * The user data.
	 */
	private User user;
	
	/**
	 * The verify password string
	 */
	private String verifyPassword;
	
	/**
	 * The verify password string
	 */
	private String newPassword;
	
	private boolean newUser;
	
	/**
	 * The data access objects
	 */
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public LoginBacking() {
		user = new User();
	}
	
	@PostConstruct
	public void read() {
		User currentUser= getBackingBean(OrderBacking.class).getOrder().getCustomer();
		if (currentUser != null ) { 
			user = currentUser;
		}	
	}
	
	//getters and setters
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}

	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	/**
	 * Try to login and read the user by user name (user identifier as entered by the user.
	 * In this version there is no password.
	 * 
	 * HRS 10.10.2010 This code should possibly be rewritten (write a new method in the service layer)
	 * to allow for login and reading data within one transaction.
	 */
	public String login() {
		
		String result = "customer?faces-redirect=true";
		log.info("trying to login user: "+user.getUserName());
		OrderBacking orderBacking = getBackingBean(OrderBacking.class);
		Order order = orderBacking.getOrder();

		if (order.getCustomer() != null && order.getCustomer().isLoggedIn()) {
			displayMessage("alreadyLoggedIn", FacesMessage.SEVERITY_ERROR);
			return null;
		}
		
		// Check if the user is a new user
		if (orderBacking.isNewUser()) {
			log.info("new user!");
			// Check if the password is correctly verified
			if (!user.getStoredPassword().equals(verifyPassword)) {
				displayMessage("passwordNotVerified", FacesMessage.SEVERITY_ERROR);
				return null;
			}
			order.setStatus(Order.Status.DEFINED);
			user.setOrders(new ArrayList<Order>());
			user.getOrders().add(order);
			order.setCustomer(user);
			Date oldDate = order.getChangedDate();
			Date newDate = new Date();
			user.setChangedDate(newDate);
			order.setChangedDate(newDate);
			     
			// Try to create a new user.
			// The order is saved in the same operation by cascading.
			// Database exceptions can only be caught in this layer (due to the default config of Spring AOP used)	
			try {
				userService.createUser(user);
				orderBacking.setChanged(false);
			} 
			catch (RuntimeException e) { 
				displayDaoError(e);
				order.setStatus(Order.Status.CART);
				order.setChangedDate(oldDate);
				order.setCustomer(null);
				return null;
			}
			
			// The new user is logged in.
			user.setLoggedIn(true);
			return result;
		} else
		{
			log.info("existing user!");
			User loggedInUser;
			// Try to login and read the user.
			// Database exceptions can only be caught in this layer
			// (due to the default config of Spring AOP used).	
			try {
				loggedInUser = userService.readUser(user.getUserName());
			} 
			catch (RuntimeException e) { 
				displayDaoError(e);
				return null;
			}
			
			if (loggedInUser == null) {
				displayMessage("unknownUser", FacesMessage.SEVERITY_ERROR, user.getUserName());
				return null;
			}
			log.info("user read: " + loggedInUser.getUserName());
			
			//check if the password is correct
			//if no password is stored in db, accept anything (this should really not happen)
			if (loggedInUser.getStoredPassword() != null) {
				if (!loggedInUser.getStoredPassword().equals(user.getStoredPassword())) {
					displayMessage("passwordIsWrong", FacesMessage.SEVERITY_ERROR);
					// Return "login" will redirect the page, null returns to current
					return null;
				}
			}	
			// The user is logged in.
			user = loggedInUser;
			order.setCustomer(user);
		
			//Add existing cart to orders, except if the cart is empty.
			if (order.getProductList().size() > 0) {
				order.setStatus(Order.Status.DEFINED);
				Date oldDate = order.getChangedDate();
				order.setChangedDate(new Date());
				// Save cart as order.
				// The products are saved in same operation by cascading.	
				try {
					orderService.createOrder(order);
					orderBacking.setChanged(false);
				} 
				catch (RuntimeException e) { 
					displayDaoError(e);
					order.setStatus(Order.Status.CART);
					order.setChangedDate(oldDate);
					return null;
				}
			}	
		}
		user.setLoggedIn(true);	//not stored in DB
		log.info("Logged in OK: "+user.getUserName());
		return result;
	}
	
	/**
	 * Go to the change password screen, log in first.
	 */
	public String gotoChangePassword() {
		String result;
		result = login();
		if (result == null) return null;
		OrderBacking orderBacking = getBackingBean(OrderBacking.class);
		orderBacking.getOrder().setCustomer(user);
		return "changePassword?faces-redirect=true";
	}
	
	
	/**
	 * Change the password of a user.
	 */
	public void changePassword() {
		if (!(newPassword.equals(verifyPassword))) {
			displayMessage("passwordNotVerified", FacesMessage.SEVERITY_ERROR);
			return;
		}
	
		Date oldDate= user.getChangedDate();
		user.setChangedDate(new Date());
		user.setStoredPassword(newPassword);
		// Logic to temporary avoid cascading, do not know any other way to do it
		// except to turn it completely off.
		List<Order> tmpOrders = user.getOrders();
		user.setOrders(null);
		
		// Update user with new changed password	
		try {
			userService.updateUser(user);
		} 
		catch (RuntimeException e) { 
			displayDaoError(e);
			user.setChangedDate(oldDate);
			return;
		}
		finally {
			user.setOrders(tmpOrders);
		}
		displayMessage("passwordChanged", FacesMessage.SEVERITY_INFO);
	}
	
	/**
	 * Set the new user flag to true
	 */
	public void setAsNewUser() {
		getBackingBean(OrderBacking.class).setNewUser(true);
	}
}

