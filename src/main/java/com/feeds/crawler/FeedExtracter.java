package com.feeds.crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.feeds.pojo.FeedMessage;

public class FeedExtracter {

	private static int count = 0;
	final static Logger logger = Logger.getLogger(FeedExtracter.class);

	public List<FeedMessage> extractLinksfromRSSFeeds() {

		Document rssDoc = null;
		String strLine = "";
		List<FeedMessage> lstFeeds = null;
		try {
			FileInputStream fstream = new FileInputStream(
					"C://Users//pdadi//workspace//RSSFeedCrawler//RSSFeedLinks.properties");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));
			lstFeeds = new ArrayList<FeedMessage>();
			while ((strLine = br.readLine()) != null) {
				try {
					rssDoc = Jsoup.connect(strLine).userAgent("Mozilla/4.76")
							.ignoreContentType(true).timeout(10000).get();
				} catch (Exception e) {
					try {
						rssDoc = Jsoup.connect(strLine).ignoreContentType(true)
								.timeout(5000).get();
					} catch (Exception e1) {
						logger.debug("Failed for " + strLine);
					}
				}
				Elements entries = rssDoc.select("item");
				String dummy = "";
				for (Element item : entries) {
					FeedMessage feed = new FeedMessage();
					dummy = item.select("title").first().text().toString();
					dummy = dummy.trim();
					if (dummy.startsWith("<![CDATA[")) {
						dummy = dummy.substring(9);
						int i = dummy.indexOf("]]&gt;");
						if (!(i == -1)) {
							dummy = dummy.substring(0, i);
						}
					}
					String title = new String(dummy.getBytes());
					dummy = item.select("description").first().text()
							.toString();
					String description = new String(dummy.getBytes());
					dummy = item.select("link").first().nextSibling()
							.toString();
					String link = new String(dummy.getBytes());
					int index = link.indexOf("&amp;story_title=");
					if (index != -1)
						feed.setLink(link.substring(0, index));
					else
						feed.setLink(link);
					String date = item.select("pubdate").text().toString();
					feed.setDescription(description);
					if (title.length() > 0)
						feed.setTitle(title);
					else
						feed.setTitle("RSS Text");
					SimpleDateFormat fromFeed = new SimpleDateFormat(
							determineDateFormat(date));
					SimpleDateFormat myFormat = new SimpleDateFormat(
							"yyyy-MM-dd");

					try {
						String reformattedStr = myFormat.format(fromFeed
								.parse(date));
						feed.setpubDate(reformattedStr);
					} catch (ParseException e) {
						logger.error("Date Parse exception occured with "
								+ date);
					}
					lstFeeds.add(feed);
				}
				logger.debug("Feeds extracted from " + strLine);
			}
			logger.debug("the total feeds are " + lstFeeds.size());

		} catch (Exception e) {
			logger.error("Failed for " + strLine);
			logger.error(e.getMessage());
		}
		return lstFeeds;

	}

	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
		{
			put("^\\d{8}$", "yyyyMMdd");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
			put("^\\d{12}$", "yyyyMMddHHmm");
			put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd-MM-yyyy HH:mm");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$",
					"yyyy-MM-dd HH:mm");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$",
					"MM/dd/yyyy HH:mm");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$",
					"yyyy/MM/dd HH:mm");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd MMM yyyy HH:mm");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd MMMM yyyy HH:mm");
			put("^\\d{14}$", "yyyyMMddHHmmss");
			put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd-MM-yyyy HH:mm:ss");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"yyyy-MM-dd HH:mm:ss");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{1,6}$",
					"yyyy-MM-dd HH:mm:ss");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"MM/dd/yyyy HH:mm:ss");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"yyyy/MM/dd HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd MMM yyyy HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd MMMM yyyy HH:mm:ss");
			put("^\\[A-Za-z]{3},\\s\\d{1,2}\\s[a-z]{3}\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}",
					"EEE, dd MMM yyyy HH:mm:ss");
		}
	};

	/**
	 * Determine SimpleDateFormat pattern matching with the given date string.
	 * Returns null if format is unknown. You can simply extend DateUtil with
	 * more formats if needed.
	 * 
	 * @param dateString
	 *            The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is
	 *         unknown.
	 * @see SimpleDateFormat
	 */
	public static String determineDateFormat(String dateString) {
		for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
			if (dateString.toLowerCase().matches(regexp)) {
				return DATE_FORMAT_REGEXPS.get(regexp);
			}
		}
		return "EEE, dd MMM yyyy"; // Unknown format.
	}

}
