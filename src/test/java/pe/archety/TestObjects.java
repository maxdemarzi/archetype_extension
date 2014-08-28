package pe.archety;

import java.util.HashMap;

public class TestObjects {

    public static final String validMD5Hash = "f9677252ab88746a25de9410fc0090b2";
    public static final String validEmail = "maxdemarzi@gmail.com";
    public static final String validURL = "http://en.wikipedia.org/wiki/Neo4j";

    public static final String notFoundEmail = "not@found.com";

    public static final HashMap<String, Object> validIdentityHash = new HashMap<String, Object>(){{
        put("identity", validMD5Hash);
    }};
}
