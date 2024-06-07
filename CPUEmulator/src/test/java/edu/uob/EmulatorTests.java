package edu.uob;
import Hack.CPUEmulator.CPUEmulatorApplication;
import Hack.CPUEmulator.CPUEmulatorGUI;
import Hack.Controller.ControllerGUI;
import HackGUI.ControllerComponent;
import SimulatorsGUI.CPUEmulatorComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmulatorTests {
    @Test
    void testStuff(){
        CPUEmulatorGUI simulatorGUI = new CPUEmulatorComponent();
        assertNotNull(simulatorGUI);
    }
}
