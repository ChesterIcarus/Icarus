package chesterlab;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
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
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultSelector;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultStrategy;
import org.matsim.core.scenario.ScenarioUtils;

public class MatsimRunPrep {

    public MatsimRunPrep(){}

    public Config createConfigFromFile(String filename){
        Config config = ConfigUtils.loadConfig(filename);

        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(1000);

        config.network().setInputFile("PhxPureMatsimNetwork.xml");
        config.network().setInputCRS("WGS84");

        config.plans().setRemovingUnneccessaryPlanAttributes(true);
        config.plans().setInputFile("FinalMatsimPlanOutput.xml");
        config.plans().setInputCRS("WGS84");

        config.global().setNumberOfThreads(6);
        config.qsim().setNumberOfThreads(6);
        config.qsim().setEndTime(36.*3600.);

        config.controler().setRoutingAlgorithmType( RoutingAlgorithmType.FastDijkstra);

//        config.plansCalcRoute().setInsertingAccessEgressWalk(true);
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
        ConfigUtils.writeConfig(config, "MyNewConfig.xml");

        return config;
    }

    public Scenario createScenarioFromConfig(Config config){
        final Scenario scenario = ScenarioUtils.loadScenario(config);
        return scenario;
    }

    public Controler createControlerFromScenario(Scenario scenario){
        return (new Controler(scenario));
    }

}
