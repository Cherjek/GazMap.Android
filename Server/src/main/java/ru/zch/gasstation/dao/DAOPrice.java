package ru.zch.gasstation.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.zch.gasstation.domain.Price;

@Repository
@Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
public class DAOPrice {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory _sessionFactory;

	@Transactional
	public Set<Price> save(int pointId, Set<Price> prices){
		Session session = _sessionFactory.getCurrentSession();

		//remove old
		List<Price> currentPrices = get(pointId);
		for(Price price : currentPrices){
			session.delete(price);
		}
		
		session.flush();
		
		
		//save new
		for(Price price : prices){
			session.save(price);
		}
		session.flush();
		
//		//collect new prices
//		Set<Price> result = new HashSet<>();
//		List<Price> resultList = get(pointId);
//		for(Price price : resultList){
//			result.add(price);
//		}
		
		return prices;
	}
	
	/**
	 * Validate vote value. It can be only -1 or 1.
	 * 
	 * @param vote
	 *            have to be -1 or 1. Or be normalized anyway
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Price> get(int pointId) {
		Session session = _sessionFactory.getCurrentSession();

		List<Price> prices = (List<Price>) session.createQuery("from Price where pid=:id").setParameter("id", pointId).list();

		return prices;
	}
}
