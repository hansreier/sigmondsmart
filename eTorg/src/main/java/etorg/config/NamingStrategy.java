package etorg.config;

import org.hibernate.cfg.EJB3NamingStrategy;

public class NamingStrategy extends EJB3NamingStrategy {

	private static final long serialVersionUID = -6122071934762461017L;

	/*
	 * Customize hibernate class to table name mapping for Postgres User is mapped
	 * to Customer For Hibernate 4 this seems to work. (Maybe not for Hibernate 5).
	 * 
	 * @see org.hibernate.cfg.EJB3NamingStrategy#classToTableName(java.lang.String)
	 */
	// @Override
	// public String classToTableName(String className) {
	// if (className.toLowerCase().equals("user"))
	// className = "Customer";
	// return super.classToTableName(className);
	// }

}
