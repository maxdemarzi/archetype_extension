package pe.archety;

import java.util.ArrayList;
import java.util.HashMap;

public class TestObjects {

    public static final String validMD5Hash = "f9677252ab88746a25de9410fc0090b2";
    public static final String validMD5Hash2 = "7a0c47442e43f063ae079ec5c0362562";
    public static final String validMD5Hash3 = "9da96a72d9bc49463f6999cbfeea864c";
    public static final String validEmail = "maxdemarzi@gmail.com";
    public static final String validURL = "http://en.wikipedia.org/wiki/Neo4j";
    public static final String validURL2 = "http://en.wikipedia.org/wiki/Nutella";
    public static final String invalidWikipediaURL = "http://en.notwikipedia.org/wiki/Neo4j";

    public static final String notFoundEmail = "not@found.com";

    public static final HashMap<String, Object> validIdentityHash = new HashMap<String, Object>(){{
        put("identity", validMD5Hash);
    }};

    public static final ArrayList<String> validIdentityPages = new ArrayList<String>(2){{
        add(validURL);
        add(validURL2);
    }};

    public static final ArrayList<String> validIdentityKnows = new ArrayList<String>(2){{
        add(validMD5Hash2);
        add(validMD5Hash3);
    }};

}
