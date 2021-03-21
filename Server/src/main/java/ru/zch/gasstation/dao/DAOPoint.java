package ru.zch.gasstation.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.zch.gasstation.domain.Point;

@Repository
@Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
public class DAOPoint {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory _sessionFactory;

	@Transactional
	public Point get(int id) {
		Point result = null;
		Session session = _sessionFactory.getCurrentSession();

		// get point with specified id
		@SuppressWarnings("unchecked")
		List<Point> points = (List<Point>) session.createQuery(" from Point WHERE id=:id").setParameter("id", id).list();

		// get first result if any
		if (points != null && points.size() > 0) {
			result = points.get(0);
		}

		return result;
	}

	/**
	 * Get all points
	 */
	@Transactional
	public List<Point> getListAll() {
		Session session = _sessionFactory.getCurrentSession();

		// get point with specified id
		@SuppressWarnings("unchecked")
		List<Point> points = (List<Point>) session.createQuery(" from Point").list();

		return points;
	}

	/**
	 * Get all not deleted and accepted points
	 */
	@Transactional
	public List<Point> getList() {
		Session session = _sessionFactory.getCurrentSession();

		// get point with specified id
		@SuppressWarnings("unchecked")
		List<Point> points = (List<Point>) session.createQuery(" from Point WHERE isDeleted = :deleted AND isAccepted = :accepted").setParameter("deleted", (byte) 0).setParameter("accepted", (byte) 1).list();

		return points;
	}

	/**
	 * Get points which was modified after or in a moment of supplied stamp
	 * 
	 * @param stamp
	 *            in secconds
	 * @return
	 */
	@Transactional
	public List<Point> getList(long stamp) {
		Session session = _sessionFactory.getCurrentSession();

		// get point with specified id
		@SuppressWarnings("unchecked")
		List<Point> points = (List<Point>) session.createQuery(" from Point WHERE modified >= FROM_UNIXTIME(:stamp) AND isAccepted = :accepted").setParameter("stamp", stamp).setParameter("accepted", (byte) 1).list();

		return points;
	}

	@Transactional
	public Point update(Point obj) {
		Session session = _sessionFactory.getCurrentSession();
		if (obj.getId() == null || obj.getId() < 1) {
			session.save(obj);
		} else {
			session.update(obj);
		}

		return obj;
	}

	@Transactional
	public void delete(int id) {
		Point point = get(id);
		// point.setDeleted(true);
		point.setIsDeleted((byte) 1);
	}

	@Transactional
	public Point make() {
		return new Point();
	}
}
