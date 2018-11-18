package etorg.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.event.ValueChangeEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import etorg.domain.User;
import etorg.domain.Order;
import etorg.service.OrderService;
import etorg.service.UserService;

/**
 * The customer backing bean.
 * 
 * A customer can be a private customer or a company/organization.
 * 
 * This backing bean can be used in session, request or view scope.
 * Currently the Spring container is used and a  view scope. 
 * The view scope is a special JSF scope, that is defined as a Spring custom scope.
 * 
 * @author Hans Reier Sigmond
 */

// Note 16.09.2010 HRS: 
// JSF 2.0 special scopes like view scope cannot be used unless the database services are wired in separate Spring bean
// or if a custom scope is defined in Spring 3.0.
// This needs some restructuring if it should be done.
// @ManagedBean
// @ViewScoped

 @Component
 @Scope("request")
 // @Scope("view")

public class CustomerBacking extends Backing implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * The customer data.
	 */
	private User customer;
	
	/**
	 * GUI change flag
	 */
	private boolean changed;
	
	/**
	 * The customer data access object
	 */
	@Autowired
	private UserService userService;
	
	/**
	 * The order data access object
	 */
	@Autowired
	private OrderService orderService;
	
	/**
	 * View table belonging to list of orders, required to obtain content of table row
	 * Must be defined transient (not serializable), else generates error message at list in Tomcat
	 */
	private transient HtmlDataTable orderListTable;
	
	private String emailError = "";
	
	/**
	 * Constructor
	 */
	public CustomerBacking() {
		customer = new User();
		changed = false;
	}
	
	/**
	 * Read order with product list from the database.
	 * This has to be called after the constructor using @PostConstruct.
	 * If called in the constructor, the wired beans (like database services) will not be available.
	 */
	@PostConstruct
	public void read() {	
		OrderBacking orderBacking = getBackingBean(OrderBacking.class);
		Order order = orderBacking.getOrder();
		User user = order.getCustomer();
		if (user != null)  {
			if (user.isLoggedIn()) {
				long userId = user.getUserId();
				try {
					customer = userService.readUser(userId, order);
					if (customer.getCurrentOrder() != null)
						orderBacking.setOrder(customer.getCurrentOrder());
				} 
				catch (RuntimeException e) { 
					displayDaoError(e);
					log.error("Error reading user data");
				}
				customer.setLoggedIn(true);
			}
		}
	}
	
	//getters and setters
	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public String getEmailError() {
		return emailError;
	}

	public void setEmailError(String emailError) {
		this.emailError = emailError;
	}
     
	/**
	 * Select an order and show the order/shopping cart.
	 */
	public String selectOrder() {
		if (!customer.isLoggedIn()) return null;
		String result = "order?faces-redirect=true";
		Order order = (Order) getOrderListTable().getRowData();
		order.setCustomer(customer);
		//If another order than the current in cart is selected, refer to the other order.
		OrderBacking orderBacking = getBackingBean(OrderBacking.class);
		//Reading order from db
		//Since the order / shopping cart bean is session scoped, it is OK to do this here.
		if (!order.equals(orderBacking.getOrder())) {
			// If the order is not previously read from db (orderId=0), 
			// it is not stored there and cannot be read 
			// (this should not be possible in the new version)
			if (order.getOrderId() != 0) {
				try {
					order = orderService.readOrder(order.getOrderId());
				} 
				catch (RuntimeException e) { 
					displayDaoError(e);
					return null;
				}
			}
			//Logged in must be set to true (not stored in db).
			order.getCustomer().setLoggedIn(true);
			orderBacking.setOrder(order);
		}	else log.debug("Current order, no need to read");
		save();  //save the customer information in case the user forgot it
		return result;
	}
	
	public HtmlDataTable getOrderListTable() {
		return orderListTable;
	}

	public void setOrderListTable(HtmlDataTable orderListTable) {
		this.orderListTable = orderListTable;
	}

	/**
	 * Save the customer information.
	 * 
	 */
	public void save() {
		if (!customer.isLoggedIn() || (!changed)) return;
		log.debug("saving customer information");
		Date oldDate = customer.getChangedDate();
		customer.setChangedDate(new Date());
		// logic to temporary avoid cascading, do not know any other way to do it
		// except to turn it completely off.
		List<Order> tmpOrders = customer.getOrders();
		customer.setOrders(null);
		// database exceptions can only be caught in this layer (due to the default config of Spring AOP used)	
		try {
			userService.updateUser(customer);
			changed = false;
			getBackingBean(OrderBacking.class).getOrder().setCustomer(customer);
		} 
		catch (RuntimeException e) { 
			customer.setChangedDate(oldDate);
			displayDaoError(e);    
		}
		finally {
			customer.setOrders(tmpOrders);
		}
	}
	
	/**
	 * Delete selected orders using the delete orders button.
	 * Locked orders will not be deleted.
	 */
	public void deleteOrders() {
		if (!customer.isLoggedIn()) return;
		List<Order> removeList = new ArrayList<Order>();
		for (Order order: customer.getOrders()) {
			if (order.isSelected())  {
				if (!order.getLocked())
					removeList.add(order);
				else
					displayMessage("lockedOrderNoDelete", FacesMessage.SEVERITY_WARN, new Long(order.getOrderId()).toString());
			}
		}
		
		try {
			orderService.deleteOrders(removeList);
		} 
		catch (RuntimeException e) { 
			displayDaoError(e);
			return;
		}
		//Clean up the list, not required in request mode
		for (Order order: removeList) {
			customer.getOrders().remove(order);
		}
	}
	
	/**
	 * 
	 * Goto the change password screen
	 * 
	 * @return		Screen to return to
	 */
	public String changePassword() {
		save();
		return "changePassword?faces-redirect=true";
	}
	
	/**
	 * 
	 * Goto the cart (order) screen
	 * 
	 * @return		Screen to return to
	 */

	public String cart() {
		save();
		return "order?faces-redirect=true";
	}
	
	/**
	 * log out
	 */
	public void logout() {
		// Should really include a popup message box here.., to avoid loosing changed data
		customer.setLoggedIn(false);
		log.info("logging out: "+ customer.getUserName());
		//The order stored in orderbacking must be emptied, 
		// to avoid storing the same order with the same product instance references twice
		getBackingBean(OrderBacking.class).setOrder(new Order());
	}
	
	/**
	 * If a field is changed, set changed to true (used to check if data 
	 * really need to be saved (on exit of the screen,..)
	 * 
	 * @param vce		value changed event
	 */
	public void changed(ValueChangeEvent vce) {
		log.info("a field is changed");
		changed = true;
	}
	
}

