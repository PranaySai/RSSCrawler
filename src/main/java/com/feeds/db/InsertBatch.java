package com.feeds.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.feeds.pojo.FeedMessage;

public class InsertBatch {
	
	final static Logger logger = Logger.getLogger(InsertBatch.class);

	public void insertArticleContent(List<FeedMessage> lstFeeds,String table_name) {

		Connection con = null;
		final int batchSize = 100;
		try {
			con = DBConnection.getConnection("fdb.properties");
			logger.debug("Inserting into the DB");
			PreparedStatement insertStmt = con
					.prepareStatement("insert into "+table_name+" values (?,?,?,?,?,?,?)");

			for (int i = 0; i < lstFeeds.size(); i++) {
				try {

					FeedMessage feed = lstFeeds.get(i);
					insertStmt.setString(1, feed.getTitle());
					insertStmt.setString(2, feed.getLink());
					insertStmt.setString(3, feed.getpubDate());
					insertStmt.setString(4, feed.getContent());
					insertStmt.setString(5, feed.getDomainName());
					insertStmt.setString(6, feed.getMatchedKeywords());
					insertStmt.setString(7, feed.getFileName());
					insertStmt.addBatch();
					if (i % batchSize == 0) {
						insertStmt.executeBatch();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			insertStmt.executeBatch();
			logger.debug("Inserted into DB");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertRawArticleContent(List<FeedMessage> lstFeeds) {

		Connection con = null;
		final int batchSize = 100;
		try {
			con = DBConnection.getConnection("fdb.properties");
			logger.debug("Inserting into raw_document  DB");
			PreparedStatement insertStmt = con
					.prepareStatement("insert into raw_document values (?,?,?,?,?,?)");

			for (int i = 0; i < lstFeeds.size(); i++) {
				try {

					FeedMessage feed = lstFeeds.get(i);
					insertStmt.setString(1, feed.getTitle());
					insertStmt.setString(2, feed.getLink());
					insertStmt.setString(3, feed.getpubDate());
					insertStmt.setString(4, feed.getContent());
					insertStmt.setString(5, feed.getDomainName());
					insertStmt.setString(6, feed.getFileName());
					insertStmt.addBatch();
					if (i % batchSize == 0) {
						insertStmt.executeBatch();
					}

				} catch (Exception e) {
					logger.debug(e.getMessage());
					e.printStackTrace();
				}

			}
			insertStmt.executeBatch();
			logger.debug("Inserted into raw_document DB size "+lstFeeds.size());

		} catch (Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
