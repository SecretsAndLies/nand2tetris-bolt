package edu.uob;
import Hack.CPUEmulator.CPUEmulatorApplication;
import Hack.CPUEmulator.CPUEmulatorGUI;
import Hack.Controller.ControllerGUI;
import HackGUI.ControllerComponent;
import SimulatorsGUI.CPUEmulatorComponent;

import javax.swing.*;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmulatorTests {
    @Test
    void testStuff() {
        CPUEmulatorGUI simulatorGUI = new CPUEmulatorComponent();
        ControllerGUI controllerGUI = new ControllerComponent();
        CPUEmulatorApplication application =
                new CPUEmulatorApplication(controllerGUI, simulatorGUI, "../bin/scripts/defaultCPU.txt",
                        "../bin/help/cpuUsage.html", "../bin/help/cpuAbout.html");

    }
}
