package com.saventech.javadriversample;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * source: https://docs.mongodb.com/getting-started/java/query/
 *
 * @author Hidayath
 */
public class Sample {

	private static final Logger logger = Logger.getLogger("MongoDB-Server-Tuning");

	public static void main(String args[]) {
		String mongoHost = "127.0.0.1";
		int mongoPort = 27017;
		String mongoDbName = "test";
		int connections = 10;

		// load the values from the properties file
		// if the file is supplied through option -f or --file
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f") || args[i].equals("--file")) {
				Properties props = new Properties();
				try {
					FileInputStream input = new FileInputStream(args[++i]);
					props.load(input);
					mongoHost = props.getProperty("mongodb.host");
					mongoPort = Integer.parseInt(props.getProperty("mongodb.port"));
					mongoDbName = props.getProperty("mongodb.db");
					connections = Integer.parseInt(props.getProperty("mongodb.connections"));

					input.close();
				} catch (FileNotFoundException e) {
					logger.warning("the properties file doesn't exist");
				} catch (IOException e) {
					logger.warning("Unable to load the properties file");
				} finally {

				}
			}
		}

		// override the values with that of supplied
		// at command line options
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-h") || args[i].equals("--host")) {
				mongoHost = args[++i];
			} else if (args[i].equals("-p") || args[i].equals("--port")) {
				mongoPort = Integer.parseInt(args[++i]);
			} else if (args[i].equals("-d") || args[i].equals("--db")) {
				mongoDbName = args[++i];
			} else if (args[i].equals("-c") || args[i].equals("--connections")) {
				connections = Integer.parseInt(args[++i]);
			}
		}

		logger.info(" mongo host:" + mongoHost);
		logger.info(" mongo port:" + mongoPort);
		logger.info("   mongo db:" + mongoDbName);

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {

		}

		MongoClient mongoClient = null;

		for (int conni = 0; conni < connections; conni++) {
			mongoClient = new MongoClient(mongoHost, mongoPort);

			MongoDatabase db = mongoClient.getDatabase(mongoDbName);

			MongoCollection<Document> collection = db.getCollection("restaurants");

			logger.info("Connection#" + conni);

			List<Document> documents = new ArrayList<Document>();
			for (int i = 0; i < 100; i++) {
				Document doc = new Document("i", i);
				try {
					int initIdx = (int) Math.round(Math.random() * 21);
					doc.append("name", "ABCDEFGHIJKLMNOPQRSTUVWZYZ".substring(initIdx,
							initIdx + (int) Math.round(Math.random() * 4)));
					initIdx = (int) Math.round(Math.random() * 21);
					doc.append("address", "ABCDEFGHIJKLMNOPQRSTUVWZYZ".substring(initIdx,
							initIdx + (int) Math.round(Math.random() * 4)));
				} catch (StringIndexOutOfBoundsException siobe) {
				}
				doc.append("dob", Math.round(Math.random() * 31) + "-" + Math.round(Math.random() * 12) + "-"
						+ (1975 + Math.round(Math.random() * 40)));
				documents.add(doc);
			}

			collection.insertMany(documents);

			FindIterable<Document> iterable = collection
					.find(or(eq("name", "ABCD"), regex("dob", Pattern.compile(".*2017"))));

			iterable.forEach(new Block<Document>() {
				@Override
				public void apply(final Document doc) {
					logger.info(doc.get("name") + "," + doc.get("address") + "," + doc.get("dob"));
				}
			});

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {

			}

			mongoClient.close();
		}

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {

		}

		mongoClient.close();
	}
}