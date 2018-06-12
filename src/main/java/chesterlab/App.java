package chesterlab;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException {
        // System.out.println( "Hello World!" );
        MatSimPlansFromKnossos example = new MatSimPlansFromKnossos();
        example.ReadKnossosInput("MATsim_plan_format.json");
        System.out.print(example.knossosPlans);
    }
}
