package data.query;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;

import com.franz.agraph.repository.AGCatalog;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;
import com.franz.agraph.repository.AGTupleQuery;
import com.franz.agraph.repository.AGValueFactory;

public class Query {

	
	private static final String HOST = getenv("AGRAPH_HOST", "localhost");
    private static final String PORT = getenv("AGRAPH_PORT", "10035");
    
    public static final String SERVER_URL = "http://" + HOST + ":" + PORT;
    public static final String CATALOG_ID = "java-catalog";
    public static final String REPOSITORY_ID = "Youtube";
    public static final String USERNAME = getenv("AGRAPH_USER", "test");
    public static final String PASSWORD = getenv("AGRAPH_PASS", "xyzzy");
    private static String getenv(final String name, final String defaultValue) {
        final String value = System.getenv(name);
            return value != null ? value : defaultValue;
        }

    /**
     * Creating a Repository
     */
    public static AGRepositoryConnection example1() throws Exception {
        // Tests getting the repository up. 
        println("\nStarting example1().");
        AGServer server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
        println("Available catalogs: " + server.listCatalogs());
        AGCatalog catalog = server.getRootCatalog();
        println("Available repositories in catalog " + 
                (catalog.getCatalogName()) + ": " + 
                catalog.listRepositories());
        closeAll();
      //  catalog.deleteRepository(REPOSITORY_ID); 
        AGRepository myRepository = catalog.createRepository(REPOSITORY_ID);
        println("Got a repository.");
        myRepository.initialize();
        println("Initialized repository.");
        println("Repository is writable? " + myRepository.isWritable());
        AGRepositoryConnection conn = myRepository.getConnection();
        closeBeforeExit(conn);
        println("Got a connection.");
        println("Repository " + (myRepository.getRepositoryID()) +
                " is up! It contains " + (conn.size()) +
                " statements."              
                );
        List<String> indices = conn.listValidIndices();
        println("All valid triple indices: " + indices);
        indices = conn.listIndices();
        println("Current triple indices: " + indices);
        println("Removing graph indices...");
        conn.dropIndex("gospi");
        conn.dropIndex("gposi");
        conn.dropIndex("gspoi");
        indices = conn.listIndices();
        println("Current triple indices: " + indices);
        println("Adding one graph index back in...");
        conn.addIndex("gspoi");
        indices = conn.listIndices();
        println("Current triple indices: " + indices);
      /* if (close) 
            conn.close();
            myRepository.shutDown();
            return null;
    }*/
     return conn;
   }

    public static void println(Object x) {
        System.out.println(x);
    }
    static void close(AGRepositoryConnection conn) {
        try {
        	conn.close();
        } catch (Exception e) {
        	System.err.println("Error closing repository connection: " + e);
        	e.printStackTrace();
        }
    }
    private static List<AGRepositoryConnection> toClose = new ArrayList<AGRepositoryConnection>();

    protected static void closeBeforeExit(AGRepositoryConnection conn) {
        toClose.add(conn);
    }
    protected static void closeAll() {
        while (!toClose.isEmpty()) {
        	AGRepositoryConnection conn = toClose.get(0);
        	close(conn);
        	while (toClose.remove(conn)) {
        		// ...
        	}
        }
    }
    public static void example3() throws Exception {
    	 AGRepositoryConnection conn = example1();
        println("\nStarting example3().");
       
            String queryString = "SELECT ?s ?p ?o  WHERE {?s ?p ?o .}";
            AGTupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value s = bindingSet.getValue("s");
                Value p = bindingSet.getValue("p");
                Value o = bindingSet.getValue("o");
                System.out.format("%s %s %s\n", s, p, o);

            }
}
    public static void main (String [] args) throws Exception{
    example3();
    		}
	}
