package com.saven.mongodb.loadtest;

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
 * 
 * This application will populate a collection and query it on each mongo
 * connection There will be n no. of threads configured by
 * max.parallel.connections and each thread will have m no. of mongo connections
 * configured by mongodb.connections.
 * 
 * Eg. if mongodb.connections = 10 and max.parallel.connections = 20, then 10 *
 * 20 * 10 records will be inserted into the mongo collection
 * 
 * references:
 * https://docs.mongodb.com/manual/reference/configuration-options/#net.maxIncomingConnections
 * https://docs.mongodb.com/manual/reference/ulimit/#ulimit
 * https://docs.mongodb.com/getting-started/java/query/
 *
 * @author Hidayath
 */
public class ConcurrentLoadingnQuery {

	private static final Logger logger = Logger.getLogger("ConcurrentLoadingnQuery");

	public static void main(String args[]) {
		String mongoHost = "127.0.0.1";
		int mongoPort = 27017;
		String mongoDbName = "test";
		int connections = 10;
		int maxParallelConn = 10;
		String mongoCollectionName = "restaurants";

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
					maxParallelConn = Integer.parseInt(props.getProperty("max.parallel.connections"));
					mongoCollectionName = props.getProperty("mongodb.collection");
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
			} else if (args[i].equals("-m") || args[i].equals("--max-ll-connections")) {
				maxParallelConn = Integer.parseInt(args[++i]);
			} else if (args[i].equals("-o") || args[i].equals("--collection")) {
				mongoCollectionName = args[++i];
			}
		}

		logger.info("              mongo host:" + mongoHost);
		logger.info("              mongo port:" + mongoPort);
		logger.info("                mongo db:" + mongoDbName);
		logger.info("        mongo collection:" + mongoCollectionName);
		logger.info("       mongo connections:" + connections);
		logger.info("max parallel connections:" + maxParallelConn);

		final String fMongoHost = mongoHost;
		final int fMongoPort = mongoPort;
		final String fMongoDbName = mongoDbName;
		final int fConnections = connections;
		final String fMongoCollectionName = mongoCollectionName;

		// a 30 seconds of before starting the actual action
		// in this time user can open jvisualvm or jconsole
		// and watch the application performance
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {

		}

		// run for given no. of max.parallel.connections
		for (int parallelConn = 0; parallelConn < maxParallelConn; parallelConn++) {

			final int pConnIdx = parallelConn;

			// start the anonymous thread
			new Thread(new Runnable() {

				@Override
				public void run() {
					Thread.currentThread().setName("app-thread-" + pConnIdx);

					MongoClient mongoClient = null;

					// run for given no. of mongodb.connections
					for (int conni = 0; conni < fConnections; conni++) {
						mongoClient = new MongoClient(fMongoHost, fMongoPort);

						MongoDatabase db = mongoClient.getDatabase(fMongoDbName);

						MongoCollection<Document> collection = db.getCollection(fMongoCollectionName);

						logger.info("thread#" + pConnIdx + ", Connection#" + conni);

						List<Document> documents = new ArrayList<Document>();

						// insert 10 records
						for (int i = 0; i < 10; i++) {
							Document doc = new Document("i", i);
							try {
								int initIdx = (int) Math.round(Math.random() * 21);
								doc.append("name", "ABCDEFGHIJKLMNOPQRSTUVWZYZ".substring(initIdx,
										initIdx + 1 + (int) Math.round(Math.random() * 4)));
								initIdx = (int) Math.round(Math.random() * 21);
								doc.append("address", "ABCDEFGHIJKLMNOPQRSTUVWZYZ".substring(initIdx,
										initIdx + 1 + (int) Math.round(Math.random() * 4)));
							} catch (StringIndexOutOfBoundsException siobe) {
							}
							doc.append("dob", Math.round(Math.random() * 31) + "-" + Math.round(Math.random() * 12)
									+ "-" + (1975 + Math.round(Math.random() * 40)));
							documents.add(doc);
						}

						collection.insertMany(documents);

						// make a simple query
						// get me records with name as "ABCD" or
						// dob is 1987
						FindIterable<Document> iterable = collection
								.find(or(eq("name", "ABCD"), regex("dob", Pattern.compile(".*1987"))));

						final int fpConnIdx = pConnIdx;
						final int fconni = conni;
						iterable.forEach(new Block<Document>() {
							@Override
							public void apply(final Document doc) {
								logger.info("[thread#" + fpConnIdx + ", Connection#" + fconni + "] record:"
										+ doc.get("name") + "," + doc.get("address") + "," + doc.get("dob"));
							}
						});

						// a 5 seconds of delay before creating another connections
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {

						}

						mongoClient.close();
					}

					// a 30 seconds of delay before exiting the thread
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {

					}

					if (mongoClient != null)
						mongoClient.close();

				}

			}).start();
		}

	}
}