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

import static pe.archety.TestObjects.*;

import static org.junit.Assert.assertEquals;

public class GetIdentityTest {
    private ArchetypeService service;
    private GraphDatabaseService db;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node identity = db.createNode(Labels.Identity);
            identity.setProperty("hash", validMD5Hash);
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
    public void shouldGetIdentityByEmail() throws IOException {
        Response response = service.getIdentity(validEmail, "", db);
        HashMap actual = objectMapper.readValue((String)response.getEntity(), HashMap.class);
        assertEquals(validIdentityHash, actual);
    }

    @Test
    public void shouldGetIdentityByHash() throws IOException {
        Response response = service.getIdentity("", validMD5Hash, db);
        HashMap actual = objectMapper.readValue((String)response.getEntity(), HashMap.class);
        assertEquals(validIdentityHash, actual);
    }

    @Test
    public void shouldThrowBecauseOfIdentityNotFound() throws IOException {
        thrown.expect(Exception.class);
        thrown.expectMessage(Exception.identityNotFound.getMessage());
        service.getIdentity(notFoundEmail, "", db);
    }

}
