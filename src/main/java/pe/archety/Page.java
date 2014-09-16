package pe.archety;

import org.apache.commons.validator.routines.UrlValidator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Page {

    public static boolean isValidURL(String text) {
        return UrlValidator.getInstance().isValid(text);
    }

    public static URL getValidURL(String text) {
        try {
            return new URL(text);
        } catch (Throwable t) {
            throw Exception.invalidURL;
        }
    }

    public static boolean isValidWikipediaURL(URL url) {
        return url.getHost().equals("en.wikipedia.org");
    }

    public static boolean isWikipediaURLFound(URL url) throws IOException {
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestMethod("HEAD");
        return (huc.getResponseCode() == 200);
    }

    public static String getPageURL(String text) throws IOException {
        if(text.isEmpty()) {
            throw Exception.missingQueryParameters;
        }

        if (isValidURL(text)) {
            URL url = getValidURL(text);
            if (isValidWikipediaURL(url)) {
                if(isWikipediaURLFound(url)) {
                    return text;
                } else {
                    throw Exception.wikipediaURLNotFound;
                }
            } else {
                throw Exception.invalidWikipediaURL;
            }
        } else {
            throw Exception.invalidURL;
        }
    }

    public static Node getPageNode(String url, GraphDatabaseService db) {
        Node page = IteratorUtil.singleOrNull(
                db.findNodesByLabelAndProperty(Labels.Page, "url", url));
        if(page != null) {
            return page;
        } else {
            throw Exception.pageNotFound;
        }
    }
}
