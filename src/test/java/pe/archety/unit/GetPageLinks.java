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

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;

public class GetPageLinks {
    private ArchetypeService service;
    private GraphDatabaseService db;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node page = db.createNode(Labels.Page);
            page.setProperty("url", validURL);

            Node page2 = db.createNode(Labels.Page);
            page2.setProperty("url", validURL2);
            page.createRelationshipTo(page2, RelationshipTypes.LINKS);

            Node page3 = db.createNode(Labels.Page);
            page3.setProperty("url", validURL3);
            page.createRelationshipTo(page3, RelationshipTypes.LINKS);

            tx.success();
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        service = new ArchetypeService();
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        populate(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void shouldGetPageLinks() throws IOException {
        Response response = service.getPageLinks(validURL, db);
        ArrayList actual = objectMapper.readValue((String)response.getEntity(), ArrayList.class);
        assertEquals(validPageLinks, actual);
    }

    @Test
    public void shouldThrowBecauseOfPageNotFound() throws IOException {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(pe.archety.Exception.pageNotFound.getMessage());
        service.getPageLinks(invalidWikipediaURL, db);
    }
}
