package com.feeds.pojo;

/*
 * Represents one RSS message
 */
public class FeedMessage {

	int id;
	String title;
	String description;
	String link;
	String author;
	String guid;
	String pubDate;
	String content;
	String matchedKeywords;
	String domainName;
	String fileName;
	Language language;
	EventType eventType;
	boolean relevance;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getpubDate() {
		return pubDate;
	}

	public void setpubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public boolean isRelevance() {
		return relevance;
	}

	public void setRelevance(boolean relevance) {
		this.relevance = relevance;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMatchedKeywords() {
		return matchedKeywords;
	}

	public void setMatchedKeywords(String matchedKeywords) {
		this.matchedKeywords = matchedKeywords;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

}