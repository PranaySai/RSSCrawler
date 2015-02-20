package com.feeds.crawler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.xml.sax.InputSource;
import com.feeds.pojo.EventType;
import com.feeds.pojo.FeedMessage;
import com.feeds.pojo.Language;
import com.google.common.base.Joiner;
import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

public class ExtractText {

	static final Pattern PAT_CHARSET = Pattern.compile("charset=([^; ]+)$");

	final static Logger logger = Logger.getLogger(ExtractText.class);

	public List<FeedMessage> matchKeywords(List<FeedMessage> lstFeeds,
			Language language, EventType eventType) {

		logger.debug("Extracting contents from feeds started");

		String strLine = "";
		List<FeedMessage> lstMatchedFeeds = new ArrayList<FeedMessage>();
		List<String> tokens = new ArrayList<String>();
		String content = null;
		Properties props = new Properties();
		FileInputStream fis = null;
		String keywordType = null;
		try {
			ClassLoader classLoader = ExtractText.class.getClassLoader();
			File file = new File(classLoader
					.getResource("resources.properties").getFile());
			fis = new FileInputStream(file);
			props.load(fis);
			if (language.getStrLanguageCode().equals("en")
					&& eventType.getstrEventTypeCode().equals("SPSG")) {
				keywordType = props.getProperty("spsgkeywords_en");
			} else if (language.getStrLanguageCode().equals("en")
					&& eventType.getstrEventTypeCode().equals("CC")) {
				keywordType = props.getProperty("climatechangekeywords_en");
			} else if (language.getStrLanguageCode().equals("fr")
					&& eventType.getstrEventTypeCode().equals("CC")) {
				keywordType = props.getProperty("climatechangekeywords_fr");
			}

			// read all the feeds from the files
			file = new File(classLoader
					.getResource(keywordType + ".properties").getFile());
			FileInputStream keywordStream = new FileInputStream(file);
			BufferedReader kwBr = new BufferedReader(new InputStreamReader(
					keywordStream));
			while ((strLine = kwBr.readLine()) != null) {
				tokens.add(strLine);
			}
			String patternString = "\\b(" + StringUtils.join(tokens, "|")
					+ ")\\b";
			Pattern pattern = Pattern.compile(patternString);
			for (int i = 0; i < lstFeeds.size(); i++) {
				FeedMessage feed = lstFeeds.get(i);
				logger.debug("Processing link " + feed.getLink());
				content = feed.getContent();
				try {
					if (content.length() > 0) {
						Matcher enmatcher = pattern.matcher(content);
						Set<String> matchKeyWord = new HashSet<String>();
						while (enmatcher.find()) {
							matchKeyWord.add(enmatcher.group());
						}
						if (matchKeyWord.size() > 0) {
							feed.setMatchedKeywords(Joiner.on(",").join(
									matchKeyWord));
							lstMatchedFeeds.add(feed);
						}
					} else
						logger.debug("No content extracted from : "
								+ feed.getLink());
				} catch (Exception e) {
					logger.debug("Exception occured while processing "
							+ feed.getLink());
					logger.error(e.getMessage());
				}
				kwBr.close();
			}

		} catch (Exception e) {
			logger.error("Failed for " + strLine);
			logger.error(e.getMessage());
		}

		return lstMatchedFeeds;

	}

	public List<FeedMessage> extractText(List<FeedMessage> lstFeeds) {

		logger.debug("Extracting contents from feeds started");
		String strLine = "", title;
		String content = null, utfContent;
		List<FeedMessage> lstMatchedFeeds = new ArrayList<FeedMessage>();
		try {
			for (int i = 0; i < lstFeeds.size(); i++) {
				FeedMessage feed = lstFeeds.get(i);
				logger.debug("Processing link " + feed.getLink());
				try {
					URL url = new URL(feed.getLink());
					BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
					HTMLHighlighter hh = HTMLHighlighter
							.newExtractingInstance();
					HTMLDocument hDoc = fetch(url);
					TextDocument tdoc = new BoilerpipeSAXInput(
							hDoc.toInputSource()).getTextDocument();
					extractor.process(tdoc);
					title = feed.getTitle();
					InputSource is = hDoc.toInputSource();
					String extractedHtml = hh.process(tdoc, is);
					Document doc = Jsoup.parse(extractedHtml);
					doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
					doc.select("*[style*=display:none]").remove();
					doc.select("a").unwrap();
					Element element = doc.body();
					HTMLTraversor formatter = new HTMLTraversor();
					NodeTraversor traversor = new NodeTraversor(formatter);
					traversor.traverse(element); // walk the DOM, and call
													// .head() and .tail() for
													// each node
					content = formatter.toString().trim();
					content = Jsoup.clean(content, Whitelist.none());

					title = (tdoc.getTitle() != null && tdoc.getTitle()
							.length() < 206) ? tdoc.getTitle() : title;
					String filename = title;
					filename = filename.replace("<", "").replace(">", "")
							.replace("*", "").replace(";", "").replace(":", "")
							.replace("\\", "").replace("/", "")
							.replace("]", "").replace("|", "").replace("?", "")
							.replace("'", "").replace("`", "")
							.replace("\"", "").trim();
					utfContent = new String(content.getBytes("UTF8"));
					feed.setContent(utfContent);
					feed.setRelevance(true);
					feed.setDomainName(url.getHost());
					feed.setFileName(filename);
					lstMatchedFeeds.add(feed);
				} catch (Exception e) {
					logger.debug("URL failed to be fetched from "
							+ feed.getLink());
					logger.error(e.getMessage());
				}
			}

		} catch (Exception e) {
			logger.error("Failed for " + strLine);
			logger.error(e.getMessage());
		}

		return lstMatchedFeeds;
	}

	public static HTMLDocument fetch(final URL url) throws IOException {
		final HttpURLConnection httpcon = (HttpURLConnection) url
				.openConnection();
		httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
		final String ct = httpcon.getContentType();

		Charset cs = Charset.forName("UTF8");
		if (ct != null) {
			Matcher m = PAT_CHARSET.matcher(ct);
			if (m.find()) {
				final String charset = m.group(1);
				try {
					cs = Charset.forName(charset);
				} catch (UnsupportedCharsetException e) {
				}
			}
		}

		java.io.InputStream in = httpcon.getInputStream();

		final String encoding = httpcon.getContentEncoding();
		if (encoding != null) {
			if ("gzip".equalsIgnoreCase(encoding)) {
				in = new GZIPInputStream(in);
			} else {
				logger.error("WARN: unsupported Content-Encoding: " + encoding);
			}
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int r;
		while ((r = in.read(buf)) != -1) {
			bos.write(buf, 0, r);
		}
		in.close();

		final byte[] data = bos.toByteArray();

		return new HTMLDocument(data, cs);
	}
}

class HTMLTraversor implements NodeVisitor {

	private StringBuilder accum = new StringBuilder(); // holds the accumulated
														// text
	private StringBuilder content = new StringBuilder(); // holds the actual
															// text
	private boolean isContent = true;

	// hit when the node is first seen
	public void head(Node node, int depth) {
		String name = node.nodeName();
		if (node instanceof TextNode) {
			String nodeText = ((TextNode) node).text();
			if (nodeText.length() > 50 || accum.toString().trim().length() > 0) {
				nodeText = nodeText.trim();
				append(nodeText);
			}
		} else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5"))
			append(" ");
		else if (name.equals("br"))
			append(" ");
	}

	// hit when all of the node's children (if any) have been visited
	public void tail(Node node, int depth) {
		String name = node.nodeName();
		if (name.equals("div")) {
			Node siblingNode = node.nextSibling();
			if (siblingNode == null && content.length() < accum.length()
					&& isContent) {
				this.content = accum;
				accum = new StringBuilder();
				if (content.length() > 300)
					isContent = false;
			}

		}
	}

	// appends text to the string builder with a simple word wrap method
	private void append(String text) {
		// fits as is, without need to wrap text
		accum.append(text + " ");
	}

	public String toString() {
		if (content.length() == 0)
			return accum.toString();
		else
			return content.toString();
	}

}
