package data.collection;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import data.ingestion.KafkaBroker;
import data.mapping.TwitterWrapper;

public class TwitterCollector {
	// How many tweets to retrieve in every call to Twitter. 100 is the maximum
		// allowed in the API
		private static final int TWEETS_PER_QUERY = 5;

		// This controls how many queries, maximum, we will make of Twitter before
		// cutting off the results.
		// You will retrieve up to MAX_QUERIES*TWEETS_PER_QUERY tweets.
		//
		// If you set MAX_QUERIES high enough (e.g., over 450), you will undoubtedly
		// hit your rate limits
		// and you an see the program sleep until the rate limits reset
		private static final int MAX_QUERIES = 2;

		// Twitter authentification
		private Twitter getAccessToken() {

			  ConfigurationBuilder cb = new ConfigurationBuilder();
		        cb.setJSONStoreEnabled(true)
				.setDebugEnabled(true)
				.setOAuthConsumerKey("lfG9n2OmS6u3vHJrL8NfslW1h")
				.setOAuthConsumerSecret(
						"EsT591XHkLnpO19kfywN1XsTHAvHCCYHnOkvahiiZ8Bw4fxxql")
				.setOAuthAccessToken(
						"4220969668-E3jrCuF0bCnRudeoKydRRVcMVAI5iZWTCqi3oLq")
				.setOAuthAccessTokenSecret(
						"QZa2Fqe12NiwGGO2Uuz7ymQriRvmvGPGwiDZhp2OWKvuN");
		        TwitterFactory tf = new TwitterFactory(cb.build());
		        Twitter twitter = tf.getInstance();
		        return twitter;
		}

		// collect tweets
		public void collectPosts(String keyword) throws IOException {
			TwitterWrapper wrapper = new TwitterWrapper();
			int totalTweets = 0;
			long maxID = -1;
			boolean testrate = false;
			QueryResult result = null;

			String topicName = "TwitterPublication";
			Twitter twitter = getAccessToken();

			// Do we need to delay because we've already hit our rate limits?
			Query query = new Query(keyword);

			do {
				try {
					result = twitter.search(query);
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				/*
				 * if (result.getRateLimitStatus().getRemaining() == 0) { // Yes we
				 * do, unfortunately ...
				 * System.out.printf("!!! Sleeping for %d seconds due to rate limits\n"
				 * , result.getRateLimitStatus().getSecondsUntilReset());
				 * 
				 * try {
				 * Thread.sleep((result.getRateLimitStatus().getSecondsUntilReset
				 * ()+2) * 1000l); } catch (InterruptedException e) {
				 * e.printStackTrace(); } }
				 */
				List<Status> tweets = result.getTweets();
				System.out.println(" Limit: "
						+ result.getRateLimitStatus().getLimit());
				System.out.println(" Remaining: "
						+ result.getRateLimitStatus().getRemaining());
				System.out.println(" SecondsUntilReset: "
						+ result.getRateLimitStatus().getSecondsUntilReset());
				JSONObject JSON_complete = null;
				for (Status tweet : tweets) {

					System.out.println("*************tweet********** " + tweet);
					// Increment our count of tweets retrieved
					totalTweets++;
					System.out.println(" totalTweets " + totalTweets);

					// Keep track of the lowest tweet ID. If you do not do this, you
					// cannot retrieve multiple
					// blocks of tweets...
					if (maxID == -1 || tweet.getId() < maxID) {
						maxID = tweet.getId();
					}
					// Status To JSON String&
					String statusJson = DataObjectFactory.getRawJSON(tweet);
					// JSON String to JSONObject
					try {
						JSON_complete = new JSONObject(statusJson);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					System.out.println("*******JSON Object**********"
							+ JSON_complete);
					KafkaBroker broker = new KafkaBroker();
					broker.producerSubcription(topicName, statusJson);

				}

			} while ((query = result.nextQuery()) != null);
		//	wrapper.consume(topicName);
		}

		public static void main(String[] args) throws InterruptedException,
				TwitterException, IOException {
			TwitterCollector tp = new TwitterCollector();
			tp.collectPosts("Samsung");

		}


}
