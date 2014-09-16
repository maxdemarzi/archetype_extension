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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;

public class GetPageLinksFunctionalTest {
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

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void shouldGetPageLinks() throws IOException {
        JaxRsResponse response = request.get("service/page/links?url=" + validURL);
        ArrayList actual = objectMapper.readValue(response.getEntity(), ArrayList.class);
        assertEquals(validPageLinks, actual);
    }

    @Test
    public void shouldNotGetPageLinksBecauseOfMissingQueryParameters() throws IOException {
        JaxRsResponse response = request.get("service/page/links");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                pe.archety.Exception.missingQueryParameters.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGetPageLinksBecausePageNotFound() throws IOException {
        JaxRsResponse response = request.get("service/page/links?url=" + invalidWikipediaURL);
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                pe.archety.Exception.pageNotFound.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }
}
