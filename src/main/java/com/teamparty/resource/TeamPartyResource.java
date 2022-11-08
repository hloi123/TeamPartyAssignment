package com.teamparty.resource;

import com.teamparty.component.TeamParty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

import static com.teamparty.configuration.Constant.GETJOKES_URL;
import static com.teamparty.configuration.Constant.HEALTHCHECK_URL;
import static com.teamparty.configuration.Constant.TEAM_PARTY_URL;

@Path(TEAM_PARTY_URL)
@Produces(MediaType.APPLICATION_JSON)
public class TeamPartyResource {

    private TeamParty teamParty;

    public TeamPartyResource(TeamParty teamParty) {
        this.teamParty = teamParty;
    }

    @GET
    @Path(HEALTHCHECK_URL)
    public String healthCheck() {
        return "Ping server at " + new Date();
    }

    @GET
    @Path(GETJOKES_URL)
    public Response getJokes(@QueryParam("query") @Size(min = 3, max = 120) @Valid @NotNull String query) throws Exception {
        return teamParty.getJokes(query.toLowerCase());
    }
}
