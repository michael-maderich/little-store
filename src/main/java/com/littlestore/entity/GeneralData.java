package com.littlestore.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="generaldata")
public class GeneralData implements Serializable {

	private static final long serialVersionUID = 1234567891L;

	@Id
	@Column(name="generalId", length=11, nullable=false, unique=true)
	private int generalId;

	@Basic(optional=false)
	@Column(name="generalName", length=255, nullable=false)
	private String generalName;

	@Basic(optional=false)
	@Column(name="generalValue", length=255, nullable=false)
	private String generalValue;
	
	public GeneralData() {
	}

	public GeneralData(int generalId, String generalName, String generalValue) {
		this.generalId = generalId;
		this.generalName = generalName;
		this.generalValue = generalValue;
	}

	public int getGeneralId() {
		return generalId;
	}
	public void setGeneralId(int generalId) {
		this.generalId = generalId;
	}
	
	public String getGeneralName() {
		return generalName;
	}
	public void setGeneralName(String generalName) {
		this.generalName = generalName;
	}

	public String getGeneralValue() {
		return generalValue;
	}
	public void setGeneralValue(String generalValue) {
		this.generalValue = generalValue;
	}

	@Override
	public String toString() {
		return "GeneralData [generalData=ID " + generalId + ": " + generalName + " - " + generalValue + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + generalId;
		result = prime * result + ((generalName == null) ? 0 : generalName.hashCode());
		result = prime * result + ((generalValue == null) ? 0 : generalValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneralData other = (GeneralData) obj;
		if (generalId != other.generalId)
			return false;
		if (generalName == null) {
			if (other.generalName != null)
				return false;
		} else if (!generalName.equals(other.generalName))
			return false;
		if (generalValue == null) {
			if (other.generalValue != null)
				return false;
		} else if (!generalValue.equals(other.generalValue))
			return false;
		return true;
	}
}
