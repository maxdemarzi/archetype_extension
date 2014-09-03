package pe.archety.functional;

import com.sun.jersey.api.client.Client;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.NeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;
import org.neo4j.server.rest.JaxRsResponse;
import org.neo4j.server.rest.RestRequest;
import pe.archety.*;
import pe.archety.Exception;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;

public class GetIdentityFunctionalTest {
    private static final Client CLIENT = Client.create();
    private static NeoServer server;
    private static RestRequest request;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws IOException {
        server = CommunityServerBuilder.server()
                .withThirdPartyJaxRsPackage("pe.archety", "/v1")
                .build();
        server.start();
        request = new RestRequest(server.baseUri().resolve("/v1"), CLIENT);
        populate(server.getDatabase().getGraph());
    }

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node identity = db.createNode(Labels.Identity);
            identity.setProperty("hash", validMD5Hash);
            tx.success();
        }
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void shouldGetIdentityByEmail() throws IOException {
        JaxRsResponse response = request.get("service/identity?email=" + validEmail);
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        assertEquals(validIdentityHash, actual);
    }

    @Test
    public void shouldGetIdentityByHash() throws IOException {
        JaxRsResponse response = request.get("service/identity?md5hash=" + validMD5Hash);
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        assertEquals(validIdentityHash, actual);
    }

    @Test
    public void shouldNotGetIdentityBecauseOfMissingQueryParameters() throws IOException {
        JaxRsResponse response = request.get("service/identity");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.missingQueryParameters.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGetIdentityBecauseOfInvalidEmailParameter() throws IOException {
        JaxRsResponse response = request.get("service/identity?email=invalid");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.invalidEmailParameter.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGetIdentityBecauseOfInvalidMD5HashParameter() throws IOException {
        JaxRsResponse response = request.get("service/identity?md5hash=invalid");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.invalidMD5HashParameter.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGetIdentityBecauseIdentityNotFound() throws IOException {
        JaxRsResponse response = request.get("service/identity?email=" + notFoundEmail);
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.identityNotFound.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

}
