package chesterlab;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class Simulate {
    public static void main(String[] args) throws IOException {
        MatSimPlansFromKnossos example = new MatSimPlansFromKnossos();
        example.CreateScenarioFromConfigFile("MyConfig.xml");
        // example.CreateEmptyScenario();
        example.ReadKnossosInput("MATsim_plan_format.json");
        // example.CreateMatsimPlans(example.knossosPlans);
        example.WritePlansToFile("FinalMatsimPlanOutput.xml");
    }
}
