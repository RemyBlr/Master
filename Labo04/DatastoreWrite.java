package ch.heigvd.cld.lab;

import com.google.appengine.api.datastore.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWrite extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain");
        PrintWriter pw = resp.getWriter();

        String queryString = req.getQueryString();
        if (queryString == null) {
            pw.println("No query parameters provided.");
            return;
        }

        String[] values = queryString.split("&");
        Map<String, String> fields = new HashMap<>();
        String kind = null;
        String key = null;

        for (String v : values) {
            String[] pair = v.split("=");
            if (pair.length != 2) {
                pw.println("Invalid query parameter: " + v);
                return;
            }

            String field = pair[0];
            String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);

            if ("_kind".equals(field)) {
                kind = value;
            } else if ("_key".equals(field)) {
                key = value;
            } else {
                fields.put(field, value);
            }
        }

        if (kind == null) {
            pw.println("No _kind parameter provided.");
            return;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


        Key entityKey = key != null ? KeyFactory.createKey(kind, key) : KeyFactory.createKey(kind, UUID.randomUUID().toString());
        Entity entity = new Entity(entityKey);

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            entity.setProperty(entry.getKey(), entry.getValue());
        }

        datastore.put(entity);


        pw.println("Success");
    }
}