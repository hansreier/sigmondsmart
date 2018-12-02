package etorg.config;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Implement namingStrategy

public class NamingStrategy implements PhysicalNamingStrategy {

	private static final Logger log = LoggerFactory.getLogger(NamingStrategy.class);
	private String hibernateDialect;

	public NamingStrategy(String hibernateDialect) {
		this.hibernateDialect = hibernateDialect;
	}

	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		String tableName;
		tableName = name.getText().toLowerCase();
		log.debug("tableName: {}", tableName);
		if ( hibernateDialect.toLowerCase().contains("postgres")) {
			if (tableName.equals("user")) {
				tableName = "customer";
				log.info("tableName changed to:" + tableName);
				name = Identifier.toIdentifier(tableName);
			}
		}
		return name;
	};

	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		log.debug("identifier name:"+name);
		return name;
	}

}

