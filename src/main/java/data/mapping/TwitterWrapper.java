package data.mapping;

import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.TwitterException;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import com.taxonic.carml.engine.RmlMapper;
import com.taxonic.carml.logical_source_resolver.CsvResolver;
import com.taxonic.carml.logical_source_resolver.JsonPathResolver;
import com.taxonic.carml.logical_source_resolver.XPathResolver;
import com.taxonic.carml.model.TriplesMap;
import com.taxonic.carml.util.RmlMappingLoader;
import com.taxonic.carml.vocab.Rdf;

import data.collection.TwitterCollector;
import data.ingestion.KafkaBroker;
import data.store.LoadRDF;

public class TwitterWrapper {
	
	String topicName = "TwitterPublication";

	public void mapp() throws IOException {
		KafkaBroker broker = new KafkaBroker();
		JSONObject JSON_complete = null;
		ConsumerRecords<String, String> records=broker.consumeTweet(topicName);
		
		Path basePath = Paths
				.get("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/rml/");
		RmlMapper mapper = RmlMapper
				.newBuilder()
				.fileResolver(basePath)
				
				.addFunctions(new RMLFunctions()).build();
		for (ConsumerRecord<String, String> record : records) {

			// print the offset,key and value for the consumer records.
			System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
			mapper.bindInputStream("input", new ByteArrayInputStream(record.value().getBytes()));
		}
	
		// Get mapping file from same folder
		Set<TriplesMap> mapping = RmlMappingLoader
				.build()
				.load(Paths
						.get("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/rml/TweetRML.ttl"),
						RDFFormat.TURTLE);

		// Execute mapping
		Model result = mapper.map(mapping);
		// Print model
		result.forEach(System.out::println);
		FileOutputStream out = new FileOutputStream(
				"/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/ouPut/output.rdf");
		try {
			Rio.write(result, out, RDFFormat.TURTLE);
			// Rio.write(result, out, RDFFormat.TRIG);

		} finally {
			out.close();
		}
	}
	
	public static void mappTweetFronFile () throws IOException{
		Path basePath = Paths
				.get("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/rml");
		RmlMapper mapper = RmlMapper
				.newBuilder()
				.fileResolver(basePath)
				.setLogicalSourceResolver(Rdf.Ql.JsonPath,
						new JsonPathResolver())
				.setLogicalSourceResolver(Rdf.Ql.XPath, new XPathResolver())
				.setLogicalSourceResolver(Rdf.Ql.Csv, new CsvResolver())
				.addFunctions(new RMLFunctions()).build();

		// Get mapping file from same folder
		Set<TriplesMap> mapping = RmlMappingLoader
				.build()
				.load(Paths
						.get("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/rml/TwitterRML.ttl"),
						RDFFormat.TURTLE);

		// Execute mapping
			Model result = mapper.map(mapping);
			//result.forEach(System.out::println);
		 //Store RDF in Allegrograph
			LoadRDF loadRdf= new LoadRDF ();
				try {
					loadRdf.example2(result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		// Print model
		//result.forEach(System.out::println);
		FileOutputStream out = new FileOutputStream(
				"/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/outPut/TwwetRDF.rdf");
		try {
		Rio.write(result, out, RDFFormat.RDFXML);

		} finally {
			out.close();
		}
	}
	public static void main(String[] args) throws IOException  {
TwitterWrapper tw = new TwitterWrapper();
//tw.mapp();
mappTweetFronFile();
}


}
