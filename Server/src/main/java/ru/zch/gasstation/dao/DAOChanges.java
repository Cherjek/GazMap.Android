package ru.zch.gasstation.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.zch.gasstation.domain.Change;
import ru.zch.gasstation.log.Log;

@Repository
@Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
public class DAOChanges {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory _sessionFactory;

	/**
	 * Removes the change by id
	 */
	public void remove(int id){
		Change change = get(id);
		remove(change);
	}
	
	/**
	 * Removes persistent change
	 */
	public void remove(Change change){
		Session session = _sessionFactory.getCurrentSession();
		session.delete(change);
	}
	
	/**
	 * Save change
	 */
	public Change save(Change change) {
		Session session = _sessionFactory.getCurrentSession();

		session.save(change);

		return change;
	}
	
	/**
	 * Retrieves change by its id
	 * @param id of the change
	 */
	public Change get(int id){
		Change change = null;
		
		Session session = _sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		List<Change> changes = session.createQuery("from Change where id=:id")
		.setParameter("id", id).list();
		
		if(changes.size() == 1){
			change = changes.get(0);
		}else{
			Log.w(String.format("Strange situation with change %d", id));
		}
		
		return change;
	}
}
