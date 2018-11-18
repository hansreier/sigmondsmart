package etorg.gui;

import java.io.Serializable;

import javax.faces.component.html.HtmlDataTable;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import etorg.domain.Order;
import etorg.domain.Product;
import etorg.domain.ProductInventory;

/**
 * 
 * The product inventory backing bean defines a product inventory.
 * The product list is in this version hard coded and defined in constructor.
 * 
 * This backing bean can be used in session, request or view scope.
 * Currently the Spring container is used and a  view scope. 
 * The view scope is a special JSF scope, that is defined as a Spring custom scope.
 * 
 * @author Hans Reier Sigmond
 *
 */

// @ManagedBean
// @ViewScoped  Works as "request" in this case, but the product list is not recreated.

@Component
@Scope("request")

// @Scope("request") Aside from memory issues, 
// the previous choices and count values are not kept in request mode when navigating away.
// and the product inventory list is recreated for every action 

public class ProductInventoryBacking extends Backing implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * The product inventory data.
	 */
	private ProductInventory productInventory;
	
	/**
	 * View table belonging to list of products, required to obtain content of table row.
	 * Must be defined transient (not serializable), else generates error message at list in Tomcat
	 */
	private transient HtmlDataTable productListTable; 
	
	//Getters and setters;
	
	public HtmlDataTable getProductListTable() {
		return productListTable;
	}

	public void setProductListTable(HtmlDataTable productListTable) {
		this.productListTable = productListTable;
	}

	/**
	 * @return 		current row number of product list table, e.g. used to pass value to Javascript
	 */
	public int getProductListRow() {
		return getProductListTable().getRowIndex();
	}
	
	/**
	 * Product inventory constructor.
	 */
	public ProductInventoryBacking() {
		productInventory= new ProductInventory();
	}
	//getters and setters
	
	public ProductInventory getProductInventory() {
		return productInventory;
	}

	public void setProductInventory(ProductInventory productInventory) {
		this.productInventory = productInventory;
	}

	/**
	 * Put the selected products in a new or existing non locked order (shopping cart).
	 * One one shopping cart can exist.
	 * Return to order page.
	 */
	public void order() { 
		OrderBacking orderBacking = getBackingBean(OrderBacking.class);
		if ( orderBacking.getOrder().getLocked() )  {
			log.debug("The order is locked");
			// If the order is locked, define a new order in memory and point to it
			// The customer should be the same as the existing order
			Order newOrder = new Order(orderBacking.getOrder().getCustomer());
			orderBacking.setOrder(newOrder);
		}
		// products can be added to the order
		for (Product product: productInventory.getProductList()) {
			if (product.isSelected())  {
				orderBacking.addProduct(product);
			}
		} 
	}
	

	/**
	 * Unselect all.
	 */
	public void removeSelected() {
		
		for (Product product: productInventory.getProductList()) {
			product.setSelected(false);
		}
	}
}
 