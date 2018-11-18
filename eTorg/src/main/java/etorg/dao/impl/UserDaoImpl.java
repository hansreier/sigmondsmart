package etorg.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import etorg.dao.UserDao;
import etorg.domain.User;


// @Repository injects Hibernate sessionfactory 
// and enables component scanning and exeption translation
@Repository
public class UserDaoImpl implements UserDao {
	
	@Autowired(required=true)
	private SessionFactory sessionFactory;

//  seems not to be required, if required should be included in interface.	
//	public void setSessionFactory(SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;
//		}

	public void createUser(User user) {
			sessionFactory.getCurrentSession().save(user);
	}
	
	public User readUser(String userName) {
		//Change code because of newer version of hibernate to get rid of warning.
		//Hibernate warning
		//Session.byNaturalId(etorg.domain.User) should be used for naturalId queries instead of Restrictions.naturalId() from a Criteria
		//return (User) sessionFactory.getCurrentSession().createCriteria(User.class)			
		//		.add( Restrictions.naturalId()
		//		.set("userName", userName))
		//		.uniqueResult();
		
		return (User) sessionFactory.getCurrentSession().byNaturalId(User.class)
			.using("userName", userName)	
			.load();		
	}
	
	public User readUser(long userId) {
			return (User) sessionFactory.getCurrentSession().get(User.class,userId);
	}
	
	public void updateUser(User user) {
		sessionFactory.getCurrentSession().update(user);
	}

	
}
