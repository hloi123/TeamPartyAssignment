package com.teamparty;

import com.teamparty.configuration.TpConfiguration;
import com.teamparty.controller.TeamPartyController;
import com.teamparty.resource.TeamPartyResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TeamPartyApplicationRun extends Application<TpConfiguration> {

    public static void main(String[] args) throws Exception {
        new TeamPartyApplicationRun().run(args);
    }

    @Override
    public String getName() {
        return "Team party project";
    }

    @Override
    public void initialize(Bootstrap<TpConfiguration> bootstrap) {
    }

    @Override
    public void run(TpConfiguration configuration, Environment environment) throws Exception {
        TeamPartyResource teamPartyResource = new TeamPartyResource(new TeamPartyController(configuration));
        environment.jersey().register(teamPartyResource);
    }
}
