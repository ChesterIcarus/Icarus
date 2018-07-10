package gui;

import org.matsim.run.gui.Gui;

import chesterlab.Simulate;

public class RunGui {
    RunGui() {
        Gui.show("Sample GUI", Simulate.class);
    }
    public static void main(String[] args) {
        RunGui runGui = new RunGui();
    }
}