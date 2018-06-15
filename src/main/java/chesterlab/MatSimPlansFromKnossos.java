package chesterlab;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.Id;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;

import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.GeotoolsTransformation;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.config.Config;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

import static org.matsim.api.core.v01.Id.createPersonId;

public class MatSimPlansFromKnossos {
    private Scenario scenario;
    private Config config;
    JsonNode knossosPlans;

    MatSimPlansFromKnossos() {
    };

    void CreateScenarioFromConfigFile(String filename) {
        // GeotoolsTransformation cTransformation = GeotoolsTransformation("q", "");
        // this.scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        // new MatsimNetworkReader(scenario.getNetwork()).readFile(NETWORKFILE);
        this.config = ConfigUtils.loadConfig(filename);
        this.config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
        this.config.plansCalcRoute().setInsertingAccessEgressWalk(true);
        this.scenario = ScenarioUtils.loadScenario(this.config);
    }

    void CreateEmptyScenario(){
        this.scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
    }

    void ReadKnossosInput(String filename) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.knossosPlans = mapper.readTree(new File(filename));
        } catch (FileNotFoundException fileNotFound) {
        } catch (IOException ioException) {
        }
    }

    void CreateMatsimPlans(JsonNode planObject) {
        for (JsonNode agent : planObject.get("population")) {
            Id<Person> person_id = createPersonId(agent.get("person_id").asText());
            Person person = scenario.getPopulation().getFactory().createPerson(person_id);
            Plan plan = scenario.getPopulation().getFactory().createPlan();
            for (JsonNode act : agent.get("plans")) {
                switch (act.get("actType").asText()) {
                case "ACTIVITY":
                    Coord coord = new Coord(act.get("x").asDouble(), act.get("y").asDouble());
                    String purpose = act.get("purpose").asText();
                    Activity activity = scenario.getPopulation().getFactory().createActivityFromCoord(purpose, coord);
                    activity.setStartTime(act.get("start_time_sec_dbl").asDouble());
                    activity.setEndTime(act.get("end_time_sec_dbl").asDouble());
                    plan.addActivity(activity);
                    break;
                case "LEG":
                    String mode = act.get("mode").asText();
                    Leg leg = scenario.getPopulation().getFactory().createLeg(mode);
                    leg.setDepartureTime(act.get("depart_time_sec_dbl").asDouble());
                    Double travelTime = act.get("travel_time_sec_dbl").asDouble();
                    if (travelTime < 0.0) { travelTime = 0.0;}
                    leg.setTravelTime(travelTime);
                    plan.addLeg(leg);
                    break;
                }
            }
            person.addPlan(plan);
            this.scenario.getPopulation().addPerson(person);
        }
    }

    void WritePlansToFile(String filename) {
        PopulationWriter pWriter = new PopulationWriter(this.scenario.getPopulation(), this.scenario.getNetwork());
        pWriter.write(filename);
    }
}
