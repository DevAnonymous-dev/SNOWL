package data.mapping;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

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

import data.ingestion.KafkaBroker;
import data.store.LoadRDF;

public class YouTubeWrapper {
	
	public static void mappVideo () throws IOException{
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
						.get("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/rml/YouTubeVideoRML.ttl"),
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
				"/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/outPut/VideoRDF.rdf");
		try {
		Rio.write(result, out, RDFFormat.RDFXML);

		} finally {
			out.close();
		}
	}
	public static void mappComment() throws IOException{
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
						.get("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/rml/YouTubeCommentRML.ttl"),
						RDFFormat.TRIG);

		// Execute mapping
		Model result = mapper.map(mapping);
		
		// Print model
		result.forEach(System.out::println);
		LoadRDF loadRdf= new LoadRDF ();
		try {
			loadRdf.example2(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream out = new FileOutputStream(
				"/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/outPut/VideoCommentRDF.rdf");
		try {
	 Rio.write(result, out, RDFFormat.RDFXML);
		//Rio.write(result, out, RDFFormat.RDFJSON);

		  // 	  
		} finally {
			out.close();
		}
	}
	public static void mappVideoCommentKafka(String comment) throws IOException{
		
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
						.get("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/rml/YouTubeCommentRML.ttl"),
						RDFFormat.TRIG);

		// Execute mapping
		Model result = mapper.map(mapping);
		
		// Print model
		result.forEach(System.out::println);
		FileOutputStream out = new FileOutputStream(
				"/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/outPut/VideoCommentRDF.ttl");
		try {
	 Rio.write(result, out, RDFFormat.TURTLE);
		//Rio.write(result, out, RDFFormat.RDFJSON);

		  // 	  
		} finally {
			out.close();
		}
	}
	public static void mappChannel() throws IOException{
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
						.get("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/rml/ChannelRML.ttl"),
						RDFFormat.TRIG);

		// Execute mapping
		Model result = mapper.map(mapping);
		
		LoadRDF loadRdf= new LoadRDF ();
		try {
			loadRdf.example2(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Print model
		result.forEach(System.out::println);
		FileOutputStream out = new FileOutputStream(
				"/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/outPut/ChannelRDF.ttl");
		try {
	 Rio.write(result, out, RDFFormat.TURTLE);

		  // 	  
		} finally {
			out.close();
		}
	}
	public static void main (String [] args) throws IOException{
mappVideo ();
mappComment();
mappChannel();
	}

}
