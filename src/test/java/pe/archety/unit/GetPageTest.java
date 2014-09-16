package pe.archety.unit;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;
import pe.archety.*;
import pe.archety.Exception;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;
import static pe.archety.TestObjects.validPageHash;

public class GetPageTest {
    private ArchetypeService service;
    private GraphDatabaseService db;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node page = db.createNode(Labels.Page);
            page.setProperty("url", validURL);
            tx.success();
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        service = new ArchetypeService();
        populate(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void shouldGetPage() throws IOException {
        Response response = service.getPage(validURL, db);
        HashMap actual = objectMapper.readValue((String)response.getEntity(), HashMap.class);
        assertEquals(validPageHash, actual);
    }

    @Test
    public void shouldThrowBecauseOfPageNotFound() throws IOException {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(Exception.pageNotFound.getMessage());
        service.getPage(validURL2, db);
    }
}
