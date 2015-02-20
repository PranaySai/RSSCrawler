package com.feeds.pojo;

public enum Language {

	English("en"), French("fr");

	private String strLanguageCode;

	private Language(String strLanguageCode) {
		this.strLanguageCode = strLanguageCode;
	}

	public String getStrLanguageCode() {
		return strLanguageCode;
	}

	public void setStrLanguageCode(String strLanguageCode) {
		this.strLanguageCode = strLanguageCode;
	}

}
