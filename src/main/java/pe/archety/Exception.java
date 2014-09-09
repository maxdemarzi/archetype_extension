package pe.archety;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Exception extends WebApplicationException {

    public Exception(int code, String error) {
        super(new Throwable(error), Response.status(code)
        .entity("{\"error\":\""+ error + "\"}")
        .type(MediaType.APPLICATION_JSON)
        .build());
    }

    public static Exception missingQueryParameters = new Exception(400, "Missing Query Parameters.");
    public static Exception invalidEmailParameter = new Exception(400, "Invalid E-mail Parameter.");
    public static Exception invalidMD5HashParameter = new Exception(400, "Invalid MD5Hash Parameter.");
    public static Exception identityNotFound = new Exception(404, "Identity not found.");
    public static Exception pageNotFound = new Exception(404, "Page not found.");
    public static Exception identityNotCreated = new Exception(500, "Unable to create Identity.");
    public static Exception pageNotCreated = new Exception(500, "Unable to create Page.");
    public static Exception invalidURL = new Exception(400, "Invalid URL Parameter.");
    public static Exception invalidWikipediaURL = new Exception(400, "Must be a valid Wikipedia URL.");
    public static Exception wikipediaURLNotFound = new Exception(400, "Wikipedia URL not Found.");

}
