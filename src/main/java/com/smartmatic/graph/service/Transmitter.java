package com.smartmatic.graph.service;

import com.smartmatic.graph.spec.ITransmission;
import com.smartmatic.graph.spec.ITransmitter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/transmitter")
@Stateless
public class Transmitter implements ITransmitter {

    @EJB
    private ITransmission transmissionBean;

    @Override
    public Response transmit() {
        String tally = transmissionBean.transmission();
        return Response.ok(tally).build();
    }
}
