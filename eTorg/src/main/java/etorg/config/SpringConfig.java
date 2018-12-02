package etorg.config;

import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.hibernate.SessionFactory;
// import org.hibernate.cfg.EJB3NamingStrategy;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
// import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import com.ibm.nosql.json.api.*;
import com.ibm.nosql.json.util.*;

import etorg.domain.Order;
import etorg.domain.Product;
import etorg.domain.ProductType;
import etorg.domain.User;
import etorg.gui.ViewScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author no011033
 * 
 *         Configuration of Spring beans, to replace XML used earlier.
 *
 *         A note about properties file: The file on the Oxxus production server
 *         (prefixed with file), is hard coded on separate location on server.
 *         The parameter names need to be equal with the others, but not the
 *         contents. All have to be there else you risk that the web application
 *         will not start on server.
 * 
 *         The parameter hibernate.naming.strategy is required for old XML
 *         config version of project. This Java config version uses keyword
 *         search in jdbc.hibernate.dialect instead (for mysql and postgres)
 *         instead of looking up hibernate.naming.strategy class directly.
 */
// Old method were properties files search for is hard coded and not search for
// by profile
// @PropertySources({
// @PropertySource(value="classpath:hibernate_postgres.properties",
// ignoreResourceNotFound = true),
// @PropertySource(value="classpath:hibernate_mysql.properties",
// ignoreResourceNotFound = true),
// @PropertySource(value="file:///var/tomcat7/conf/hibernate_mysql.properties",
// ignoreResourceNotFound = true)
// })

@Configuration
@ComponentScan(basePackages = "etorg")
@EnableTransactionManagement
public class SpringConfig {

	private static final Logger log = LoggerFactory.getLogger(SpringConfig.class);
	// Cannot get direct resource lookup using JNDI to work.
	// @Resource(lookup="jdbc/mydb")
	// @Resource(lookup = "jdbc/mysql")
	// private DataSource dataSource;

	/**
	 * MySql localhost profile, must define Active Profile value
	 * -Dspring.profiles.active="mysql" to be set e.g. in Tomcat config in Eclipse
	 */
	@Configuration
	@Profile("mysql")
	@PropertySource(value = "classpath:hibernate_mysql.properties")
	static class MySQL {
	}

	/**
	 * No sql localhost profile, must define Active Profile value
	 * -Dspring.profiles.active="nosql" to be set e.g. in Tomcat config in Eclipse
	 * MySQL is used, but dummy data source. If you try to run eTorg locally without
	 * a database installed, this profile must be used. If not used, c3p0 connection
	 * pool will create timeout problem. Errors will of cause occur in ETorg when
	 * trying to log into database.
	 */
	@Configuration
	@Profile("nosql")
	@PropertySource(value = "classpath:hibernate_mysql.properties")
	static class NoSQL {
	}

	/**
	 * Postgres localhost profile, must define Active Profile value
	 * -Dspring.profiles.active="postgres" to be set e.g. in Tomcat config in
	 * Eclipse
	 */
	@Configuration
	@Profile("postgres")
	@PropertySource(value = "classpath:hibernate_postgres.properties")
	static class Postgres {
	}

	/**
	 * Default profile (uses MySql), need not define Active Profile value This is
	 * also used for production server on Oxxus.
	 */
	@Configuration
	@Profile("default")
	@PropertySources({ @PropertySource(value = "classpath:hibernate_mysql.properties", ignoreResourceNotFound = true),
			@PropertySource(value = "file:///var/tomcat7/conf/hibernate_mysql.properties", ignoreResourceNotFound = true) })
	static class Default {
	}

	// Direct allocation using value removed
	// @Value("${jdbc.hibernate.dialect}")
	private String hibernateDialect;

	// Removed, not required
	// @Value("${hibernate.naming.strategy}")
	// private String hibernateNamingStrategy;

	// @Value("${jdbc.url}")
	private String jdbcUrl;

	// @Value("${jdbc.username}")
	private String jdbcUsername;

	// @Value("${jdbc.password}")
	private String jdbcPassword;

	// @Value("${jdbc.driver.class}")
	private String jdbcDriverClass;

	private int minPoolSize;
	private int maxPoolSize;
	private int initialPoolSize;
	private int maxIdleTime = 55; // 3600; //(one hour) seconds before expire;
	private int maxIdleTimeExcessConnections = 50; // (seconds before expire unused) to be set much less than
													// maxIdletime
	private int idleConnectionTestPeriod = 55; // 3600
	private int maxStatements = 50;
	private int acquireIncrement = 1;
	// private int dbPort;
	private String dbPort;
	private String dbHost;
	private String dbName;
	boolean clearDB = true;
	boolean serviceFound = false;

	// Either use Environment and env.getProperty to obtain properties
	// e.g. env.getproperty("jdbc.url");
	// or use @Value and PropertySourcesPlaceholderConfigurer
	// @Autowired
	@Resource
	private Environment env;

	/**
	 * Required to resolve placeholder in @value
	 * 
	 * @return
	 */
	// @Bean
	// public static PropertySourcesPlaceholderConfigurer
	// propertySourcesPlaceholderConfigurer() {
	// return new PropertySourcesPlaceholderConfigurer();
	// }

	/**
	 * Hibernate session factory using Spring's Hibernate LocalSessionFactoryBean
	 * for Hibernate 4. Page 307 Spring book
	 * 
	 * @return
	 */
	@Bean
	@Profile({ "default", "mysql", "postgres" })
	public LocalSessionFactoryBean sessionFactory() {
		readProperties();
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setAnnotatedClasses(User.class, Order.class, ProductType.class, Product.class);
		sessionFactory.setHibernateProperties(hibernateProperties(true));
		// sessionFactory.setNamingStrategy(namingStrategy());
		return sessionFactory;
	}

	/**
	 * Hibernate session factory using Spring's Hibernate LocalSessionFactoryBean
	 * for Hibernate 4. Page 307 Spring book dummy datasource is used for this
	 * session factory
	 * 
	 * @return
	 */
	@Bean
	@Profile({ "nosql" })
	public LocalSessionFactoryBean sessionFactoryNoSql() {
		readProperties();
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dummyDataSource());
		sessionFactory.setAnnotatedClasses(User.class, Order.class, ProductType.class, Product.class);
		sessionFactory.setHibernateProperties(hibernateProperties(true));
		// sessionFactory.setNamingStrategy(namingStrategy());
		return sessionFactory;
	}

	/**
	 * Hibernate session factory using Spring's Hibernate LocalSessionFactoryBean
	 * for Hibernate 4. Page 307 Spring book.
	 * 
	 * If the service is not found, a dummy datasource and mysql jdbc driver is
	 * used. Hibernate database schema check (e.g. create table is not existing) is
	 * also turned off. I tried other alternatives, e.g. to ommit datasource, but
	 * this created serious Spring configuration problems when using
	 * LocalSessionFactoryBean.
	 * 
	 * @return
	 */
	@Bean
	@Profile("cloud")
	public LocalSessionFactoryBean cloudFactory() {
		readCloudProperties();
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		if (serviceFound) {
			sessionFactory.setDataSource(dataSource());
		} else {
			sessionFactory.setDataSource(dummyDataSource());
		}
		sessionFactory.setAnnotatedClasses(User.class, Order.class, ProductType.class, Product.class);
		sessionFactory.setHibernateProperties(hibernateProperties(serviceFound));
		// sessionFactory.setNamingStrategy(namingStrategy());
		return sessionFactory;
	}

	/**
	 * Read properties from environment file and set other hardcoded properties.
	 */
	private void readProperties() {
		// print logging for debug
		LoggerContext lc = ((ch.qos.logback.classic.Logger) log).getLoggerContext();
		StatusPrinter.print(lc);
		// setting properties
		jdbcUrl = env.getProperty("jdbc.url");
		jdbcUsername = env.getProperty("jdbc.username");
		jdbcPassword = env.getProperty("jdbc.password");
		jdbcDriverClass = env.getProperty("jdbc.driver.class");
		hibernateDialect = env.getProperty("jdbc.hibernate.dialect");
		// setting hard coded values
		// hibernateNamingStrategy = env.getProperty("hibernate.naming.strategy");
		initialPoolSize = 5;
		minPoolSize = 5;
		maxPoolSize = 20;
		maxIdleTime = 3600; // 3600; //(one hour) seconds before expire;
		maxIdleTimeExcessConnections = 50; // (seconds before expire unused) to be set much less than maxIdletime
		idleConnectionTestPeriod = 3600;
	}

	/**
	 * Read properties from cloud using a cloud factory for MySQL and set other
	 * hardcoded properties.
	 * 
	 * The code is not used, but kept for reference. Parsing VCAP_SERVICES is a
	 * better idea. I tried this cloud factory method for IBM SQL database, but had
	 * difficulties fetching all required properties this way.
	 * 
	 */
	private void readMySQLCloudProperties() {
		// If you do not wish to extend AbstractCloudConfig,
		// Create your own Cloud object as an alternative
		CloudFactory cloudFactory = new CloudFactory();
		Cloud cloud = cloudFactory.getCloud();
		// According to doc cloud.getServiceId() should get the service directly, but
		// method does not exist!
		Properties prop = cloud.getCloudProperties();
		// Host prop.getProperty("cloud.services.mysql.connection.host");
		// Appnavn + prop.getProperty("cloud.application.name"));
		jdbcUrl = prop.getProperty("cloud.services.mysql.connection.jdbcurl");
		jdbcUsername = prop.getProperty("cloud.services.mysql.connection.username");
		jdbcPassword = prop.getProperty("cloud.services.mysql.connection.password");
		jdbcDriverClass = "com.mysql.jdbc.Driver";
		hibernateDialect = "org.hibernate.dialect.MySQLDialect";
		// hibernateNamingStrategy = "org.hibernate.cfg.EJB3NamingStrategy";
		minPoolSize = 1;
		maxPoolSize = 4;
		initialPoolSize = 1;
		// It seams like that expire is set to 60 seconds on ClearDB MySQL
		// To avoid problem then maxIdletime must be set low?
		maxIdleTime = 3600; // 3600 (one hour) seconds before expire
		maxIdleTimeExcessConnections = 50; // (seconds before expire unused) to be set much less than maxIdletime
		idleConnectionTestPeriod = 55; // 3600
	}

	/**
	 * Read cloud service properties using VCAP_SERVICES environment variable Find
	 * the type of database service and set hard coded values to fit the database
	 * service type.
	 */
	private void readCloudProperties() {
		parseVCAP();
		if (serviceFound) {
			if (clearDB) {
				jdbcDriverClass = "com.mysql.jdbc.Driver";
				hibernateDialect = "org.hibernate.dialect.MySQLDialect";
				// hibernateNamingStrategy = "org.hibernate.cfg.EJB3NamingStrategy";
				minPoolSize = 1;
				maxPoolSize = 4;
				initialPoolSize = 1;
				// It seams like that expire is set to 60 seconds on ClearDB MySQL
				// To avoid problem then maxIdletime must be set low?
				maxIdleTime = 3600; // 3600 (one hour) seconds before expire
				maxIdleTimeExcessConnections = 50; // (seconds before expire unused) to be set much less than
													// maxIdletime
				idleConnectionTestPeriod = 55; // 3600
			} else {
				// IBM SQL DB (DB2)
				jdbcDriverClass = "com.ibm.db2.jcc.DB2Driver";
				hibernateDialect = "org.hibernate.dialect.DB2Dialect";
				// hibernateNamingStrategy = "org.hibernate.cfg.EJB3NamingStrategy";
				minPoolSize = 5;
				maxPoolSize = 10;
				initialPoolSize = 5;
				maxIdleTime = 3600; // 3600 (one hour) seconds before expire
				maxIdleTimeExcessConnections = 50; // (seconds before expire unused) to be set much less than
													// maxIdletime
				idleConnectionTestPeriod = 3600;
			}
		} else {
			// dummy datasource driver, I use mysql
			jdbcDriverClass = "com.mysql.jdbc.Driver";
			hibernateDialect = "org.hibernate.dialect.MySQLDialect";
		}
	}

	/**
	 * Select data source. user and password Use c3P0 connection pool provider Set
	 * some connection pool parameters. Page 291 Spring book.
	 * 
	 * Note: If defined as a bean, Cloud Foundry will detect it and automatically
	 * reconfigure datasource (auto reconfiguration). The code inside datasource
	 * will then not be run. Hibernate connection parameters will not be set either.
	 * Connection pooling will be set up differently and auto create of tables will
	 * be turned off.
	 * 
	 * https://developer.jboss.org/wiki/HowToConfigureTheC3P0ConnectionPool
	 * http://www.mchange.com/projects/c3p0/
	 * 
	 * Error occurring sometimes using ClearDB MySQL DB access problem: Can not read
	 * response from server. Expected to read 4 bytes, read 0 bytes before
	 * connection was unexpectedly lost. How to fix?
	 * 
	 * http://www.jvmhost.com/articles/hibernate-famous-communications-link-failure-last-packet-sent-successfuly-issue-solved-c3p0
	 * 
	 * Datasource types: ComboPooledDataSource: C3P0 BasicDataSource: DBCP
	 * DriverManagerDataSource: Spring, only for testing, no proper pooling.
	 * 
	 * Apparently if you are using Spring and Hibernate, there are two ways you
	 * could use c3p0. You could let either Spring or Hibernate control the
	 * connection pooling. In this config Hibernate controls connection pooling (do
	 * not use both!)
	 * 
	 * http://syntx.io/configuring-c3p0-connection-pooling-with-spring-and-hibernate/
	 * 
	 * @return
	 */
	// @Bean
	private DataSource dataSource() {
		log.info("inside datasource, so not auto reconfiguration");
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(jdbcDriverClass);
		} catch (PropertyVetoException e) {
			log.error("cannot set driver class");
			return null;
		}
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUser(jdbcUsername);
		dataSource.setPassword(jdbcPassword);
		dataSource.setMinPoolSize(minPoolSize);
		dataSource.setMaxPoolSize(maxPoolSize);
		dataSource.setInitialPoolSize(initialPoolSize);
		dataSource.setMaxIdleTime(maxIdleTime);
		dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
		dataSource.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
		dataSource.setMaxStatements(maxStatements);
		dataSource.setAcquireIncrement(acquireIncrement);
		return dataSource;
	}

	/**
	 * A dummy datasource is really a replacement for code with no data source
	 * included. This is used e.g. in Bluemix to allow the program to work without a
	 * database service added.
	 * 
	 * DriverManagerDataSource is used because it has not connection pooling and the
	 * program will not wait for connection pool timeout to occur before continuing.
	 * 
	 * @return
	 */
	private DataSource dummyDataSource() {
		log.info("inside datasource, so not auto reconfiguration");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(jdbcDriverClass);
		dataSource.setUrl("noDatabaseService");
		dataSource.setUsername("");
		dataSource.setPassword("");
		return dataSource;
	}

	/**
	 * Parse VCAP services. This is an example how this can be done, when two
	 * possible database services can be selected. It is to be noted that since the
	 * VCAP structure is different for every service, this must be taken care of in
	 * the coding. For every new service added to the web application, this parse
	 * VCAP when must be modified.
	 * 
	 * Note: Possibly error handling should be added to this routine, it can in
	 * principle create exceptions everywhere where reading a key and value fails.
	 */
	private void parseVCAP() {
		serviceFound = false;

		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the for DB2 connection info
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			String thekey = null;
			Set<String> keys = obj.keySet();
			log.debug("Searching through VCAP keys");
			// Look for the VCAP key that holds the SQLDB information
			for (String eachkey : keys) {
				log.debug("Key is: " + eachkey);
				// Just in case the service name gets changed to lower case in the future, use
				// toUpperCase
				if (eachkey.toUpperCase().contains("CLEARDB")) {
					thekey = eachkey;
					log.info("MySQL ClearDB database service found and selected");
					break;
				}
				if (eachkey.toUpperCase().contains("SQLDB")) {
					thekey = eachkey;
					log.info("IBM SQL (DB2) database service found and selected");
					clearDB = false;
				}
			}
			if (thekey == null) {
				log.error("Cannot find any suitable database service in the VCAP; exiting");
				return;
			}
			BasicDBList list = (BasicDBList) obj.get(thekey);
			obj = (BasicDBObject) list.get("0");
			log.info("Service found: " + obj.get("name"));
			log.info("The key is: " + thekey);
			// parse all the credentials from the vcap env variable
			obj = (BasicDBObject) obj.get("credentials");
			log.debug("Credentials read: " + obj);
			dbName = (String) obj.get("db");
			dbPort = obj.getString("port");
			jdbcUsername = (String) obj.get("username");
			jdbcPassword = (String) obj.get("password");
			if (clearDB) {
				jdbcUrl = (String) obj.get("jdbcUrl");
				dbHost = (String) obj.get("hostname");
			} else {
				jdbcUrl = (String) obj.get("jdbcurl");
				dbHost = (String) obj.get("host");
			}

		} else {
			log.error("VCAP_SERVICES is null");
			return;
		}
		serviceFound = true;
		log.info("database host: " + dbHost);
		log.info("database port: " + dbPort);
		log.info("database name: " + dbName); // null for cleardb
		log.info("username: " + jdbcUsername);
		// log.info("password: " + jdbcPassword);
		log.info("url: " + jdbcUrl);
	}

	/**
	 * Set hibernate properties
	 * 
	 * note: Connection pool parameters could have been set her but a lot of
	 * problems with it when googling. It should be possible, but using
	 * ComboPoolDataSource to set the parameters is safer.
	 * 
	 * @return
	 */
	private Properties hibernateProperties(boolean update) {
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.dialect", hibernateDialect);
		if (update)
			properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.generate_statistics", "false");
		properties.put("hibernate.id.new_generator_mappings", "false");
		// added to prevent problems, required?
		// properties.put("hibernate.auto_close_session", "true");
		// properties.put("hibernate.connection.release_mode", "after_transaction");
		// properties.put("hibernate.connection.handling_mode", "after_transaction");
		// properties.put("namingStrategy", hibernateNamingStrategy);
		return properties;
	}

	/**
	 * Set naming strategy
	 * 
	 * @return
	 */
	// private EJB3NamingStrategy namingStrategy() {
	// if (hibernateDialect.toLowerCase().contains("postgres")) {
	// return new NamingStrategy();
	// } else {
	// return new EJB3NamingStrategy();
	// }
	// }

	/**
	 * Enable custom scope viewscope note: No guarantee that this one works. Not
	 * tested.
	 * 
	 * @return
	 */
	@Bean
	public static CustomScopeConfigurer viewScope() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.addScope("new", new ViewScope());
		return configurer;
	}

	/**
	 * Transaction manager for a single Hibernate SessionFactory (alternative to
	 * JTA)
	 * 
	 * @param sessionFactory
	 * @return
	 */
	@Bean
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);
		return txManager;
	}
}
