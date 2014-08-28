package pe.archety;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType {
    LIKES, HATES, KNOWS, LINKS
}
