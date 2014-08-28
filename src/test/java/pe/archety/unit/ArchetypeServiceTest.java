package pe.archety.unit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import pe.archety.ArchetypeService;

import static org.junit.Assert.assertEquals;

public class ArchetypeServiceTest {
    private ArchetypeService service;
    private GraphDatabaseService db;

    @Before
    public void setUp() {
        service = new ArchetypeService();
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void shouldRespondToHelloWorld() {
        assertEquals("Hello World!", service.helloWorld());
    }

    @Test
    public void shouldWarmUp() {
        assertEquals("Warmed up and ready to go!", service.warmUp(db));
    }

    @Test
    public void shouldMigrate() {
        assertEquals("Migrated!", service.migrate(db));
        assertEquals("Already Migrated!", service.migrate(db));
    }

}
