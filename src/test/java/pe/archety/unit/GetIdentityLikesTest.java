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
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;

public class GetIdentityLikesTest {
    private ArchetypeService service;
    private GraphDatabaseService db;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node identity = db.createNode(Labels.Identity);
            identity.setProperty("hash", validMD5Hash);

            Node page1 = db.createNode(Labels.Page);
            page1.setProperty("url", validURL);
            identity.createRelationshipTo(page1, RelationshipTypes.LIKES);

            Node page2 = db.createNode(Labels.Page);
            page2.setProperty("url", validURL2);
            identity.createRelationshipTo(page2, RelationshipTypes.LIKES);

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
    public void shouldGetIdentityLikesByEmail() throws IOException {
        Response response = service.getIdentityLikes(validEmail, "", db);
        ArrayList actual = objectMapper.readValue((String)response.getEntity(), ArrayList.class);
        assertEquals(validIdentityPages, actual);
    }

    @Test
    public void shouldGetIdentityLikesByHash() throws IOException {
        Response response = service.getIdentityLikes("", validMD5Hash, db);
        ArrayList actual = objectMapper.readValue((String)response.getEntity(), ArrayList.class);
        assertEquals(validIdentityPages, actual);
    }

    @Test
    public void shouldThrowBecauseOfIdentityNotFound() throws IOException {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(Exception.identityNotFound.getMessage());
        service.getIdentityLikes(notFoundEmail, "", db);
    }
}
