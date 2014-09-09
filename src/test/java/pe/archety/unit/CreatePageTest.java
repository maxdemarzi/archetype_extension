package pe.archety.unit;

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
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;

public class CreatePageTest {
    private ArchetypeService service;
    private GraphDatabaseService db;

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node page = db.createNode(Labels.Page);
            page.setProperty("url", validURL2);
            tx.success();
        }
    }

    @Before
    public void setUp() throws URISyntaxException {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        service = new ArchetypeService();
        service.migrate(db);
        populate(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldCreatePage() throws IOException {
        Response response = service.createPage(validURL, db);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldGetDuplicatePages() throws IOException {
        Response response = service.createPage(validURL2, db);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldGetPageNotCreatedException() throws IOException {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(Exception.pageNotCreated.getMessage());
        db.shutdown();
        service.createPage(validURL, db);
    }
}
