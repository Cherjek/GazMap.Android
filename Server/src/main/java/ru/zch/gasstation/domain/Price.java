package ru.zch.gasstation.domain;

// Generated 06.03.2015 2:11:10 by Hibernate Tools 4.0.0

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * PointTypes generated by hbm2java
 */
@Entity
@Table(name = "point_types", catalog = "gasstation")
public class Price implements java.io.Serializable {
	private static final long serialVersionUID = 8526989993900604752L;
	
	private PointTypesId id;
	private Type type;
	private Point point;
	private Float price;
	private Date modified;

	public Price() {
	}

	public Price(PointTypesId id) {
		this.id = id;
	}
	
	public Price(PointTypesId id, Float price) {
		this.id = id;
		this.price = price;
	}

	public Price(PointTypesId id, Type type, Point point, Float price, Date modified) {
		this.id = id;
		this.type = type;
		this.point = point;
		this.price = price;
		this.modified = modified;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pid", column = @Column(name = "pid")), @AttributeOverride(name = "tid", column = @Column(name = "tid")) })
	public PointTypesId getId() {
		return this.id;
	}

	public void setId(PointTypesId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tid", insertable = false, updatable = false)
	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pid", insertable = false, updatable = false)
	public Point getPoint() {
		return this.point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	

	@Column(name = "price", precision = 12, scale = 0)
	public Float getPrice() {
		return this.price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	@Column(name = "modified", length = 19, insertable = false, updatable = false)
	public Date getModified() {
		return this.modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

}
