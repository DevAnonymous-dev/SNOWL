package data.mapping;

import java.util.Properties;

import org.ejml.simple.SimpleMatrix;

import com.taxonic.carml.engine.function.FnoFunction;
import com.taxonic.carml.engine.function.FnoParam;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class RMLFunctions {
	public int pars(String str) {
		int x = Integer.parseInt(str);
		return x;
	}

	@FnoFunction("http://localhost/socialNetwork/model#YearMappingFunction")
	public String YearMappingFunction(@FnoParam("http://localhost/socialNetwork/model#intParameterA") String date) {
		System.out.println(" date : " + date);
		String year = date.substring(0, 4);
		return year;
	}

	@FnoFunction("http://localhost/socialNetwork/model#MonthMappingFunction")
	public int MonthMappingFunction(@FnoParam("http://localhost/socialNetwork/model#intParameterA") String date) {
		String month = date.substring(6, 7);
		return pars(month);
	}

	@FnoFunction("http://localhost/socialNetwork/model#DayMappingFunction")
	public int DayMappingFunction(@FnoParam("http://localhost/socialNetwork/model#intParameterA") String date) {
		String dayofMonth = date.substring(8, 10);
		return pars(dayofMonth);
	}

	@FnoFunction("http://localhost/socialNetwork/model#HourMappingFunction")
	public int HourMappingFunction(@FnoParam("http://localhost/socialNetwork/model#intParameterA") String date) {
		String hour = date.substring(11, 13);
		return pars(hour);
	}

	@FnoFunction("http://localhost/socialNetwork/model#MinuteMappingFunction")
	public int MinuteMappingFunction(@FnoParam("http://localhost/socialNetwork/model#intParameterA") String date) {
		String minute = date.substring(14, 16);
		return pars(minute);
	}

	@FnoFunction("http://localhost/socialNetwork/model#SecondMappingFunction")
	public int SecondMappingFunction(@FnoParam("http://localhost/socialNetwork/model#intParameterA") String date) {
		String second = date.substring(17, 19);
		return pars(second);
	}

	// Sandford Core NLP sentiment analysis
	@FnoFunction("http://localhost/socialNetwork/model#OpinionMappingFunction")
	public String OpinionMappingFunction(@FnoParam("http://localhost/socialNetwork/model#intParameterA") String text) {
		StanfordCoreNLP pipeline;
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
		int mainSentiment = 0;
		if (text != null && text.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(text);

			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				System.out.println("sentence : " + sentence.toString());
				Tree tree = sentence.get(SentimentAnnotatedTree.class);
				System.out.println("tree : " + tree.toString());
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				System.out.println("sentiment : " + sentiment);
				SimpleMatrix sentiment_new = RNNCoreAnnotations.getPredictions(tree);
				System.out.println("sentiment_new : " + sentiment_new);
				String partText = sentence.toString();
				System.out.println("partText: " + partText);
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}
			}
		}
		if (mainSentiment == 0) {
			return "VeryNegative";
		} else if (mainSentiment == 1) {
			return "Negative";
		} else if (mainSentiment == 2) {
			return "Neutral";
		} else if (mainSentiment == 3) {
			return "Positive";
		} else
			return "VeryPositive";

	}

	@FnoFunction("http://localhost/socialNetwork/model#TopicMappingFunction")
	public String TopicMappingFunction(@FnoParam("http://localhost/socialNetwork/model#intParameterA") String text) {
		// add algorithm for topic detection

		return "Topic";
	}

}
