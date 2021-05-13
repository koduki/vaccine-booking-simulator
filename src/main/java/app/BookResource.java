package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/book")
public class BookResource {

    private Map<String, Integer> bookTable = new HashMap<>() {
        {
            for (int i = 0; i < 30; i++) {
                put(String.format("06%02d", i), 5);
            }
        }
    };

    private Map<String, Integer> showBooking() {
        return bookTable;
    }

    private boolean addBooking(String date) {
        if (bookTable.get(date) > 0) {
            bookTable.put(date, bookTable.get(date) - 1);
            return true;
        } else {
            return false;
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response show() throws JsonProcessingException {
        return Response.ok(new ObjectMapper().writeValueAsString(showBooking())
        ).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(Map<String, String> params) throws JsonProcessingException {
        var date = params.get("date");
        var name = params.get("name");

        if (addBooking(date)) {
            return Response.ok(new ObjectMapper().writeValueAsString((Map.of(
                    "status", "success",
                    "name", name,
                    "date", date,
                    "count", bookTable.get(date)
            )))).build();
        } else {
            return Response.ok(new ObjectMapper().writeValueAsString((Map.of(
                    "status", "error",
                    "message", "full"
            )))).build();
        }

    }

}
