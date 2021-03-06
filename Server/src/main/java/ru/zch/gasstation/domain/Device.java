package ru.zch.gasstation.domain;

// Generated 06.03.2015 2:11:10 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Device generated by hbm2java
 */
@Entity
@Table(name = "device", catalog = "gasstation", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Device implements java.io.Serializable {
	private static final long serialVersionUID = -2309218810860995280L;
	
	private Integer id;
	private String name;
	private Set<Vote> votes = new HashSet<>(0);

	public Device() {
	}

	public Device(String name, Set<Vote> votes) {
		this.name = name;
		this.votes = votes;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", unique = true, length = 20)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "device")
	public Set<Vote> getVotes() {
		return this.votes;
	}

	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}

}
