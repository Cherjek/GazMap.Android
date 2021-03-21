package ru.zch.gasstation.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.zch.gasstation.domain.Type;

@Repository
@Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
public class DAOType {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory _sessionFactory;

	@SuppressWarnings("unchecked")
	public List<Type> getAll() {
		Session session = _sessionFactory.getCurrentSession();

		List<Type> types = (List<Type>) session.createQuery("from Type ").list();

		return types;
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
	public Type get(int id) {
		Session session = _sessionFactory.getCurrentSession();
		Type type = null;

		List<Type> types = (List<Type>) session.createQuery("from Type where id=:id").setParameter("id", id).list();

		if (types.size() > 0) {
			type = types.get(0);
		}

		return type;
	}
}
