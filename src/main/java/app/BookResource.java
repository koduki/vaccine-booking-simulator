package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agroal.api.AgroalDataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/book")
public class BookResource {

    @Inject
    AgroalDataSource defaultDataSource;

    @ConfigProperty(name = "app.booking.limit")
    int bookingLimmitNumber;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response show() throws JsonProcessingException, SQLException {

        return Response.ok(new ObjectMapper().writeValueAsString(showBooking())
        ).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(Map<String, String> params) throws JsonProcessingException, SQLException {
        var date = params.get("date");
        var name = params.get("name");

        try ( var con = defaultDataSource.getConnection()) {
            if (isAvailable(con, date)) {
                if (addBooking(con, date, name)) {
                    return Response.ok(new ObjectMapper().writeValueAsString((Map.of(
                            "status", "success",
                            "name", name,
                            "date", date
                    )))).build();
                } else {
                    return Response.ok(new ObjectMapper().writeValueAsString((Map.of(
                            "status", "error",
                            "message", "full"
                    )))).build();
                }
            } else {
                return Response.ok(new ObjectMapper().writeValueAsString((Map.of(
                        "status", "error",
                        "message", "full"
                )))).build();
            }
        }
    }

    private Map<Date, Integer> showBooking() throws SQLException {
        var book = new HashMap<Date, Integer>();
        try ( var con = defaultDataSource.getConnection();) {
            for (int day = 1; day <= 30; day++) {
                var date = Date.valueOf(String.format("2021-06-%02d", day));
                try ( var st = con.prepareStatement("select count(*) from book where date=?")) {
                    st.setDate(1, date);
                    try ( var rs = st.executeQuery()) {
                        rs.next();
                        book.put(date, rs.getInt(1));
                    }
                }
            }
        }
        return book;
    }

    private boolean addBooking(Connection con, String date, String name) throws SQLException {
        try ( var st = con.prepareStatement("INSERT INTO book (date, user_name) VALUES(?, ?)")) {
            st.setDate(1, Date.valueOf(date));
            st.setString(2, name);
            st.execute();
        }

        try ( var st = con.prepareStatement("SELECT user_name FROM (SELECT user_name FROM book where date=? ORDER BY created_at LIMIT ?) as top where user_name=?;")) {
            st.setDate(1, Date.valueOf(date));
            st.setInt(2, bookingLimmitNumber);
            st.setString(3, name);
            try ( var rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean isAvailable(Connection con, String date) throws SQLException {
        try ( var st = con.prepareStatement("select count(*) from book where date=?")) {
            st.setDate(1, Date.valueOf(date));
            try ( var rs = st.executeQuery()) {
                rs.next();
                int count = rs.getInt(1);
                return count < bookingLimmitNumber;
            }
        }
    }

}
