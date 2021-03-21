package ru.zch.gasstation.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.zch.gasstation.domain.Device;
import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.domain.Vote;

@Repository
@Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
public class DAOVote {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory _sessionFactory;

	@SuppressWarnings("unchecked")
	public Vote getVote(Device device, Point point) {
		Session session = _sessionFactory.getCurrentSession();
		Vote vote = null;

		List<Vote> votes = (List<Vote>) session.createQuery("from Vote WHERE did = :deviceId AND pid=:pointId")
				.setParameter("deviceId", device.getId())
				.setParameter("pointId", point.getId())
				.list();

		if (votes.size() > 0) {
			vote = votes.get(0);
		}

		return vote;
	}

	/**
	 * Validate vote value. It can be only -1 or 1.
	 * 
	 * @param vote
	 *            have to be -1 or 1. Or be normalized anyway
	 * @return
	 */
	@Transactional
	public Vote update(Vote vote) {
		if (vote != null) {
			// normalize value
			int value = vote.getVote() < 0 ? -1 : 1;
			vote.setVote(value);

			// do update
			Session session = _sessionFactory.getCurrentSession();
			if (vote.getId() == null || vote.getId() < 1) {
				session.save(vote);
			} else {
				session.update(vote);
			}
		}
		return vote;
	}
}
