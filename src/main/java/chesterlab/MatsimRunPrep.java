package chesterlab;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultSelector;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultStrategy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.transformations.GeotoolsTransformation;

import static org.matsim.core.network.NetworkUtils.createNetwork;

public class MatsimRunPrep {

    public MatsimRunPrep(){};

    private Scenario scenario;
    private Network network;
    private Config config;
    private Controler controler;
    private String networkFile = "PhxPureMatsimNetwork.xml";
    private String inputCrs = "EPSG:2223";
    private String planInputFile = "FinalMatsimPlanOutput.xml";
    private String configOutputFile = "MyNewConfig.xml";

    public Config createConfigFromFile(String filename){
        config = ConfigUtils.loadConfig(filename);

        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(0);

        config.network().setInputFile(networkFile);
        config.network().setInputCRS(inputCrs);

        config.plans().setRemovingUnneccessaryPlanAttributes(true);
        config.plans().setInputFile(planInputFile);
        config.plans().setInputCRS(inputCrs);

        config.global().setNumberOfThreads(6);
        config.qsim().setNumberOfThreads(6);
        config.qsim().setEndTime(36.*3600.);

        config.controler().setRoutingAlgorithmType( RoutingAlgorithmType.FastDijkstra);

        config.scenario().setSimulationPeriodInDays(1);

        {
            StrategySettings stratSets = new StrategySettings( ) ;
            stratSets.setStrategyName( DefaultStrategy.ReRoute.name() );
            stratSets.setWeight(0.1);
            config.strategy().addStrategySettings(stratSets);
        }
        {
            StrategySettings stratSets = new StrategySettings( ) ;
            stratSets.setStrategyName( DefaultSelector.ChangeExpBeta ) ;
            stratSets.setWeight(0.9);
            config.strategy().addStrategySettings(stratSets);
        }

        config.qsim().setTrafficDynamics(TrafficDynamics.queue);
        ConfigUtils.writeConfig(config, configOutputFile);

        return config;
    }

    public Scenario createScenarioFromConfig(Config config){
        scenario = ScenarioUtils.loadScenario(config);
        return scenario;
    }

    public Controler createControlerFromScenario(Scenario scenario){
        controler = new Controler(scenario);
        return controler;
    }

    public void transformNetwork(final String from_crs, final String to_crs){
            NetworkWriter networkWriter = new NetworkWriter(new GeotoolsTransformation(from_crs, to_crs), scenario.getNetwork());
            networkWriter.writeFileV2(networkFile);
    }
}
