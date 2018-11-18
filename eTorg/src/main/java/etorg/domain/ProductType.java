package etorg.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * This class defines a product type.
 * In this version a product type cannot be dependent of other product types.
 * 
 * @author Hans Reier Sigmond
 *
 */

//By default, properties of superclass are ignored and not persistent, 
// add @MappedSuperclass to avoid this

@MappedSuperclass
public class ProductType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long productTypeId;
	private String name;
	private String description;
	private String manufacturer;
	private String country;
	private double price;
	
	/**
	 * 
	 * ProductType constructor
	 * 
	 * @param productTypeId			The identifier of the product type.
	 * @param name					The name of the product type.
	 * @param description			An eventual description of the product type.
	 * @param manufacturer			The manufacturer of the product type.
	 * @param country				The country where the product type is produces (origin).
	 * @param price					The price of the product type.
	 */

	public ProductType(long productTypeId, String name, String description, String manufacturer, String country, double price) {
		super();
		this.productTypeId = productTypeId;
		this.name = name;
		this.description = description;
		this.manufacturer = manufacturer;
		this.country = country;
		this.price = price;
	}
	
	public ProductType() {
	}
	
	//Getters and setters
	
	
	@Column(length=40)
	public long getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(long productTypeId) {
		this.productTypeId = productTypeId;
	}

	@Column(length=50)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(length=80)
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Column(length=50)
	public String getCountry() {
		return country;
	}

	
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Column
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}	
}
