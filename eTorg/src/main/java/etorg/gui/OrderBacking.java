package etorg.gui;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import etorg.domain.Order;
import etorg.domain.Order.Status;
import etorg.domain.Product;
import etorg.service.OrderService;

/**
 * The Order backing bean.
 * 
 * An order in status CART is a shopping cart (transient in memory).
 * An order defined with other status values is persistent (in database).
 * Currently the possible status values are: CART, DEFINED, PAID.
 * The status values SENT, RECEIVED are also possible,
 * but not logic to support them is implemented.
 * 
 * This been is implemented to be in session scope. 
 * The Spring container is used, not the JSF container.
 * If set to session scope, the class must be serializable!
 * 
 * Important: 14.12.2015 It is not recommended to use binding if session scope is used
 * according to JSF 2 specifications. So binding is now removed from this code
 * As a consequence validation of input field is simplified, and summary function
 * is not calculated on value change of field, but when buttons are pressed. 
 * The JSF view three therefore cannot be accessed directly.
 * The Websphere server also crashed because of duplicate JSF id's when binding was used.
 * 
 * This problem could be solved by keeping the session scoped things in separate bean,
 * but too much work to do this change.
 * 
 * To enable multiple orders to be used in the backing bean,
 * the order object reference must be changed.
 * This could also be solved by defining a custom Spring scope !?
 * 
 * @author Hans Reier Sigmond
 *
 */

// Do not use the JSF Scopes / container
// @ManagedBean
// @SessionScoped

// Use the Spring scope / container
@Component
@Scope("session")
public class OrderBacking extends Backing implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * The order data.
	 */
	private Order order;
	
	/**
	 * GUI change flag
	 */
	private boolean changed;
	
	/**
	 * New user flag
	 */
	private boolean newUser;
	
	/**
	 * A variable that indicated if Javascript is enabled in a browser.
	 * One problem with this variable is that it cannot be set correctly 
	 * before a form is submitted to the server and it also is
	 * dependent of hidden fields in the JSF markup.
	 */
	private boolean javascriptEnabled = false;
	
	/**
	 * This string stores the locale as selected by the user
	 * This setting overrides the default browser settings is set.
	 */
	private String locale;
	
	/**
	 * This string stores the locale as given by default browser settings
	 */
	private String defaultLocale;
	
	/**
	 * The order data access object
	 */
	@Autowired
	private OrderService orderService;
	
	private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
	
	/**
	 * Constructor, create order and an empty product list.
	 */
	public OrderBacking() {
		order = new Order();
		changed = false;
		log.debug("creating order "+ dateFormat.format(order.getChangedDate()));
		defaultLocale = getCurrentLocale();
		locale = defaultLocale;
		log.info("default browser locale is:"+ locale);
	}
	//Getters and setters;
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}
	
	public boolean isJavascriptEnabled() {
		return javascriptEnabled;
	}


	public void setJavascriptEnabled(boolean javascriptEnabled) {
		this.javascriptEnabled = javascriptEnabled;
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	
	/**
	 * Save the order.
	 */
	public void save() {
		if (!order.getCustomer().isLoggedIn()) return;
		if (!changed) return;
		Date oldDate = order.getChangedDate();
		recalc();
		order.setChangedDate(new Date());
		try {
			orderService.saveOrder(order);
			changed = false;
		} 
		catch (RuntimeException e) {
			order.setChangedDate(oldDate);
			displayDaoError(e);    
		} 
	}
	
	/**
	 * Add a (new or existing) product from the product list and calculate new price.
	 *
	 * @param product product to be added (A new product instance is created).
	 */
	public void addProduct(Product product) {
		changed = true;
		order.setPrice(order.getPrice() + product.getPrice()*product.getCount());
		for (Product prod: order.getProductList()) {
			if (prod.getProductTypeId()== product.getProductTypeId()) {
				prod.setCount(prod.getCount() + product.getCount());
				return;
			}
		}
		order.getProductList().add(new Product(order, product));
	}
	
	/**
	 * Increase product instances to existing product in list using the add products button.
	 */
	public void increaseProducts() {
		for (Product prod: order.getProductList()) {
			if (prod.isSelected()) {
				prod.setCount(prod.getCount() + 1);
				order.setPrice(order.getPrice() + prod.getPrice());
				changed = true;
			}
		}
	}	
	
	/**
	 * Decrease product instances to existing product in list using the add products button.
	 */
	public void decreaseProducts() {
		for (Product prod: order.getProductList()) {
			if (prod.isSelected()) {
				if (prod.getCount() > 1) {
					prod.setCount(prod.getCount()-1);
					order.setPrice(order.getPrice()-prod.getPrice());
					changed = true;
				}
			}
		}
	}
	
	/**
	 * Delete selected products using the delete products button.
	 */
	public void deleteProducts() {
		List<Product> removeList = new ArrayList<Product>();
		for (Product product: order.getProductList()) {
			if (product.isSelected())  {
				removeList.add(product);
				order.setPrice(order.getPrice()-product.getTotal());
				changed = true;
			}
		}
		order.getProductList().removeAll(removeList);
	}
	
	/**
	 * Recalculate the product price using the recalculate button.
	 */
	private void recalc() {
		double totalPrice = 0;
		for (Product product: order.getProductList()) {
				totalPrice = totalPrice + product.getPrice() * product.getCount();		
		}
		order.setPrice(totalPrice);
	}
	
	/**
	 * Recalculate the product price using the recalculate button.
	 */
	public void recalculate() {
		recalc();
		changed = true;
	}
	
	/**
	 * If none is selected, select all, else unselect all.
	 */
	public void toggleSelected() {
		recalc();
		boolean allUnselected = true;
		for (Product product: order.getProductList()) {
			if (product.isSelected())  {
				allUnselected = false;
				break;
			}
		}
		for (Product product: order.getProductList()) {
			product.setSelected(allUnselected);
		}
	}
	
	/**
	 * Verify order information.
	 * If the order is transient (in status CART) it will be attemped saved,
	 * and the status will be set to DEFINED. If the user is not logged in,
	 * the user will enter the login screen and be asked to log in.
	 * If the user is logged in, the order screen will be loaded to let the user
	 * verify all the users orders.
	 */
	public String verifyOrder() {
		log.debug("verify order");
		String result = "customer?faces-redirect=true";

		//check if the customer needs to log in, and log in if required
		if (order.getCustomer() != null && (order.getCustomer().isLoggedIn())) {
			
			//existing order
			if (order.getStatus() != Order.Status.CART) {
				recalc();
				// save the order
				save();
				return result;
			}	
		
			Date oldDate = order.getChangedDate();
			order.setChangedDate(new Date());
			order.setStatus(Status.DEFINED);
			recalc();
			// create the order in the database	
			try {
				orderService.createOrder(order);
			} 
			catch (RuntimeException e) {
				order.setChangedDate(oldDate);
				order.setStatus(Status.CART);
				displayDaoError(e);    
				result = "order";	
			} 
		} else {
			newUser = false;
			result = "login?faces-redirect=true";
		}
		return result;
	}	
	
	/**
	 * Order the products. 
	 * Currently this only implies a status change to PAID, and a new save of the order.
	 * No logic for paying e.g. by credit card is implemented.
	 * No logic for sending the order to the customer is implemented either.
	 */
	public void order() {
		log.debug("order!");
		if (!order.getCustomer().isLoggedIn()) return;
		if (order.getStatus() == Order.Status.DEFINED)
		{	
			recalc();
			order.setStatus(Order.Status.PAID); 
			Date oldDate = order.getChangedDate();
			order.setChangedDate(new Date());
			try {
				orderService.saveOrder(order);
			} 
			catch (RuntimeException e) {
				order.setStatus(Order.Status.DEFINED);
				order.setChangedDate(oldDate);
				displayDaoError(e);
				return;
			} 
			displayMessage("ordered", FacesMessage.SEVERITY_INFO);
		}	
	}
	
	/**
	 * If a value is changed, set changed to true (used to check if data 
	 * really need to be saved (on exit of the screen,..)
	 * 
	 * @param vce		value changed event
	 */
	public void changed(ValueChangeEvent vce) {
		changed = true;
	}
}
