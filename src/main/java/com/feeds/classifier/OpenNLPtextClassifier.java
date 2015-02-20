package com.feeds.classifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerEvaluator;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.util.InvalidFormatException;

import org.apache.log4j.Logger;

import com.feeds.db.InsertBatch;
import com.feeds.pojo.FeedMessage;

public class OpenNLPtextClassifier {

	final static Logger logger = Logger.getLogger(OpenNLPtextClassifier.class);

	/*
	 * public static void main(String[] args) throws
	 * InvalidFormatException,IOException, SQLException {
	 * 
	 * 
	 * double accuracy = 0; new OpenNLPtextClassifier().train(); //String cat =
	 * "1"; //String content =
	 * "Uhuru heads for US, targets trade and security - News - nation.co.ke Latest BusinessLatest SportsLatest Blogs & OpinionIn SummaryMr Kenyatta is scheduled to address the Corporate Council on Africa which represents some of the major multinational companies in the US including General Electric Ltd, Coca Cola and IBM.  President Kenyatta’s delegation includes Cabinet Secretaries Amina Mohamed (Foreign Affairs and International Trade), Joseph Ole Lenku (Interior and Coordination of National Government), and Henry Rotich (National Treasury).advertisementPresident Uhuru Kenyatta will Sunday travel to Washington D.C. USA, for an official visit. President Kenyatta’s visit will focus on strengthening ties between the US and Kenya in trade, capital investment, infrastructure, energy and security. The President will also attend the US-Africa Leaders’ Summit where issues on development, investing in Africa’s future, peace and Security will be discussed. CORPORATE COUNCIL ON AFRICAMr Kenyatta is scheduled to address the Corporate Council on Africa which represents some of the major multinational companies in the US including General Electric Ltd, Coca Cola and IBM.  President Kenyatta’s delegation includes Cabinet Secretaries Amina Mohamed (Foreign Affairs and International Trade), Joseph Ole Lenku (Interior and Coordination of National Government), and Henry Rotich (National Treasury).Others will be Mr Michael Kamau (Transport and Infrastructure) and Adan Mohammed (Industrialization and Enterprise Development).“Mr Kenyatta is also expected to hold talks with World Bank President Jim Yong Kim,�? according to the dispatch from State House.Business people interested to invest in Kenya will also have an opportunity to share their proposals with the delegation at a forum convened by the Kenya Private Sector Alliance (KEPSA), the Kenya National Chamber of Commerce and Industry, and the US Chamber of Commerce.During the meeting, the President will discuss Kenya’s mega infrastructure projects including expansion of airports, roads, commuter railway lines and construction of new ports along the coastal region.This would present diverse opportunities for investors keen to invest in Africa.Kenya’s delegation will also push for a seamless renewal of the African Growth and Opportunity Act (AGOA) for a further 15 years, and favourable policies to boost trade."
	 * ;
	 * 
	 * new OpenNLPtextClassifier().test();
	 * 
	 * logger.debug("All done"); }
	 */
	/*
	 * public void train() { String onlpModelPath =
	 * "C://Users//nkim30//Documents//My Works//JavaWorkspace//temp//training_model.bin"
	 * ; String trainingDataFilePath =
	 * "C://Users//nkim30//Documents//Project//Foresight//ONLP//training.txt";
	 * DoccatModel model = null; InputStream dataInputStream = null;
	 * OutputStream onlpModelOutput = null; try {
	 * 
	 * We are storing all training data in a single text file and training data
	 * should be in the following format category_of_data1 data1
	 * category_of_data2 data2 . . . category_of_dataN dataN
	 * 
	 * 
	 * // Read training data file dataInputStream = new
	 * FileInputStream(trainingDataFilePath); // Read each training instance
	 * ObjectStream<String> lineStream = new
	 * PlainTextByLineStream(dataInputStream, "UTF-8");
	 * ObjectStream<DocumentSample> sampleStream = new
	 * DocumentSampleStream(lineStream); // Calculate the training model model =
	 * DocumentCategorizerME.train("en", sampleStream); } catch (IOException e)
	 * { System.err.println(e.getMessage()); } finally { if (dataInputStream !=
	 * null) { try { dataInputStream.close(); } catch (IOException e) {
	 * System.err.println(e.getMessage()); } } }
	 * 
	 * Now we are writing the calculated model to a file in order to use the
	 * trained classifier in production
	 * 
	 * try { onlpModelOutput = new BufferedOutputStream(new
	 * FileOutputStream(onlpModelPath)); model.serialize(onlpModelOutput); }
	 * catch (IOException e) { System.err.println(e.getMessage()); } finally {
	 * if (onlpModelOutput != null) { try { onlpModelOutput.close(); } catch
	 * (IOException e) { System.err.println(e.getMessage()); } } } }
	 */

	/*
	 * Now we call the saved model and test it Give it a new text document and
	 * the expected category
	 */
	public List<FeedMessage> classifyContent(List<FeedMessage> lstFeeds,String classificationModelFilePath) {
		List<FeedMessage> lstRelevantFeeds = new ArrayList<FeedMessage>();
		try {
			InputStream is = new FileInputStream(classificationModelFilePath);
			DoccatModel classificationModel = new DoccatModel(is);
			DocumentCategorizerME classificationME = new DocumentCategorizerME(
					classificationModel);
			DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(
					classificationME);

			for (int i = 0; i < lstFeeds.size(); i++) {
				FeedMessage feed = lstFeeds.get(i);

				String documentContent = feed.getContent();
				DocumentSample sample = new DocumentSample("1", documentContent);
				double[] classDistribution = classificationME
						.categorize(documentContent);
				String predictedCategory = classificationME
						.getBestCategory(classDistribution);
				modelEvaluator.evaluteSample(sample);
				logger.debug("Model prediction : " + predictedCategory);
				if (predictedCategory.equals("1")) {
					feed.setRelevance(true);
					lstRelevantFeeds.add(feed);
				}

			}
			double result = modelEvaluator.getAccuracy();
			logger.debug("Accuracy : " + result);

			logger.debug("***************************************************");
			logger.debug("The total feeds being inserted to db are "
					+ lstRelevantFeeds.size());

		} catch (Exception e) {

		}
		return lstRelevantFeeds;
	}
}
