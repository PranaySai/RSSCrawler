package com.feeds.pojo;

public enum EventType {
	
	Political("SPSG"),ClimateChange("CC");
	
	private String strEventTypeCode;

	private EventType(String strEventTypeCode) {
		this.strEventTypeCode = strEventTypeCode;
	}

	public String getstrEventTypeCode() {
		return strEventTypeCode;
	}

	public void setstrEventTypeCode(String strEventTypeCode) {
		this.strEventTypeCode = strEventTypeCode;
	}
}
