package chesterlab;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.controler.Controler;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class Simulate {
    public static void main(String[] args) throws IOException {
        MatSimPlansFromKnossos example = new MatSimPlansFromKnossos();
        example.CreateEmptyScenario();
        example.ReadKnossosInput("sample_MATsimPlans.json");
        example.CreateMatsimPlans(example.knossosPlans);
        example.WritePlansToFile("FinalMatsimPlanOutput.xml");

        MatsimRunPrep runPrep = new MatsimRunPrep();
        Scenario scenario = runPrep.createScenarioFromConfig(runPrep.createConfigFromFile("MyConfig.xml"));
        Controler controler = runPrep.createControlerFromScenario(scenario);
        controler.run();
    }
}
