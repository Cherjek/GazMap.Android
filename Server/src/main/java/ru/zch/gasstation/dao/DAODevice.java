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

@Repository
@Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
public class DAODevice {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory _sessionFactory;

	/**
	 * Load device if there is already one or create a new one
	 */
	public Device get(String name) {
		Session session = _sessionFactory.getCurrentSession();
		Device device = null;

		// get point with specified id
		@SuppressWarnings("unchecked")
		List<Device> devices = (List<Device>) session.createQuery(" from Device WHERE name=:name").setParameter("name", name).list();

		if (devices.size() > 0) {
			device = devices.get(0);
		} else if (devices.size() == 0) {
			// make new one if there is no device which specifeid Name
			device = new Device(name, null);
			session.save(device);
		}

		return device;
	}
}
