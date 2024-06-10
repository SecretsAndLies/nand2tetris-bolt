package edu.uob;


import Hack.CPUEmulator.CPUEmulator;
import Hack.Controller.CommandException;
import Hack.Controller.ProgramException;
import Hack.Controller.VariableException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EmulatorTests {
    
    @Test
    public void testMult() throws VariableException, CommandException, ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.doCommand("set RAM[0] 5".split(" "));
       assertEquals("5",cpuEmulator.getValue("RAM[0]"));
        cpuEmulator.doCommand("set RAM[1] 4".split(" "));
        assertEquals("4",cpuEmulator.getValue("RAM[1]"));
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));
        cpuEmulator.doCommand("load src/test/java/edu/uob/Mult.hack".split(" "));
        // ideally this would be more like while current instruction is not END.
        for (int i = 0; i < 100; i++) {
            cpuEmulator.doCommand("ticktock".split(" "));
        }

        assertEquals("20",cpuEmulator.getValue("RAM[2]"));
    }

    // TODO: a program that influences the screen

    // todo a program that takes user input.
}
