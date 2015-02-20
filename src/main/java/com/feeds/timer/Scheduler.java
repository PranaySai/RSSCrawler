package com.feeds.timer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.feeds.classifier.OpenNLPtextClassifier;
import com.feeds.crawler.ExtractText;
import com.feeds.crawler.FeedExtracter;
import com.feeds.db.InsertBatch;
import com.feeds.pojo.EventType;
import com.feeds.pojo.FeedMessage;
import com.feeds.pojo.Language;

public class Scheduler extends TimerTask {

	final static Logger logger = Logger.getLogger(Scheduler.class);

	@Override
	public void run() {
		logger.debug("Start of process ");
		FeedExtracter extract = new FeedExtracter();
		List<FeedMessage> lstFeeds = extract.extractLinksfromRSSFeeds();
		// call the extraction process
		ExtractText extractText = new ExtractText();
		List<FeedMessage> lstProcessedFeeds = extractText.extractText(lstFeeds);
		InsertBatch insert = new InsertBatch();
		insert.insertRawArticleContent(lstProcessedFeeds);
		List<FeedMessage> lstMatchedFeeds = extractText.matchKeywords(
				lstProcessedFeeds, Language.English, EventType.ClimateChange);
		OpenNLPtextClassifier classify = new OpenNLPtextClassifier();
		List<FeedMessage> lstRelevantFeeds = classify
				.classifyContent(lstMatchedFeeds,
						"C://Users//pdadi//workspace//RSSFeedCrawler//TrainedData//training_model.bin");
		insert.insertArticleContent(lstRelevantFeeds, "rss_feed");
		lstMatchedFeeds = extractText.matchKeywords(lstProcessedFeeds, Language.English,
				EventType.Political);
		List<FeedMessage> lstPoliticalRelevantFeeds = classify
				.classifyContent(
						lstMatchedFeeds,
						"C://Users//pdadi//workspace//RSSFeedCrawler//TrainedData//policy_training_model.bin");
		insert.insertArticleContent(lstPoliticalRelevantFeeds, "rss_feed_spsg");
		logger.debug("End of Process ");
	}

	public void start() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 1000, 24 * 60 * 60 * 1000);
	}

	public static void main(String[] args) {

		Scheduler schedular = new Scheduler();
		schedular.start();

	}
}