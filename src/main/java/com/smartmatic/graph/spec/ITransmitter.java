package com.smartmatic.graph.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

public interface ITransmitter {

    @GET
    @Produces("application/json")
    @Path("/transmit")
    Response transmit();


}
