package com.littlestore.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="generalData")
public class GeneralData implements Serializable {

	private static final long serialVersionUID = 1114567899L;

	@Id
	@Column(name="generalId", length=11, nullable=false, unique=true)
	private int generalId;

	@Basic(optional=false)
	@Column(name="generalName", length=255, nullable=false)
	private String generalName;

	@Basic(optional=false)
	@Column(name="generalValue", length=255, nullable=false)
	private String generalValue;
	
	@Basic
	@Column(name="generalCategory", length=50, nullable=false)
	private String generalCategory;
	
	public GeneralData() {
	}

	public GeneralData(int generalId, String generalName, String generalValue, String generalCategory) {
		this.generalId = generalId;
		this.generalName = generalName;
		this.generalValue = generalValue;
		this.generalCategory = generalCategory;
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

	public String getGeneralCategory() {
		return generalCategory;
	}
	public void setGeneralCategory(String generalCategory) {
		this.generalCategory = generalCategory;
	}

	@Override
	public String toString() {
		return "GeneralData [generalData=ID " + generalId + ": (" + generalCategory + ") " + generalName + " - " + generalValue + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((generalCategory == null) ? 0 : generalCategory.hashCode());
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
		if (generalCategory == null) {
			if (other.generalCategory != null)
				return false;
		} else if (!generalCategory.equals(other.generalCategory))
			return false;
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
