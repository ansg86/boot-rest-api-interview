package posmy.interview.boot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import posmy.interview.boot.enumeration.ROLE_LABEL;

@Entity
@Table(name = "ROLES")
public class Roles {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "label")
	@Enumerated(EnumType.STRING)
	private ROLE_LABEL label;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ROLE_LABEL getLabel() {
		return label;
	}

	public void setLabel(ROLE_LABEL label) {
		this.label = label;
	}
}
