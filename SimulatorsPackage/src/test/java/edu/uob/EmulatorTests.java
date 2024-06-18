package edu.uob;


import Hack.CPUEmulator.CPUEmulator;
import Hack.Controller.CommandException;
import Hack.Controller.ProgramException;
import Hack.Controller.VariableException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class EmulatorTests {
    public boolean screenIs(CPUEmulator cpuEmulator, String value) throws VariableException {
        for (int i = 16384; i < 24576; i++) {
            if(!cpuEmulator.getValue("RAM["+i+"]").equals(value)){
                return false;
            }
        }
        return true;
    }

    public void runCycles(CPUEmulator cpuEmulator, int numCycles) throws VariableException, CommandException, ProgramException {
        for (int i = 0; i < numCycles; i++) {
            cpuEmulator.doCommand("ticktock".split(" "));
        }
    }

    @Test
    public void testStepBack() throws VariableException, CommandException, ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));
        cpuEmulator.doCommand("set RAM[0] 5".split(" "));
        assertEquals("5",cpuEmulator.getValue("RAM[0]"));
        cpuEmulator.doCommand("set RAM[1] 4".split(" "));
        assertEquals("4",cpuEmulator.getValue("RAM[1]"));
        cpuEmulator.doCommand("load src/test/java/edu/uob/Mult.hack".split(" "));
        runCycles(cpuEmulator,1);
        assertEquals("1",cpuEmulator.getValue("A"));
        assertEquals("1",cpuEmulator.getValue("PC"));
        cpuEmulator.stepBack();
        assertEquals("0",cpuEmulator.getValue("A"));
        runCycles(cpuEmulator,2);
        assertEquals("4",cpuEmulator.getValue("RAM[1]"));
        assertEquals("5",cpuEmulator.getValue("RAM[0]"));
        assertEquals("4",cpuEmulator.getValue("D"));
        assertEquals("2",cpuEmulator.getValue("PC"));
        cpuEmulator.stepBack();
        assertEquals("1",cpuEmulator.getValue("PC"));
        assertEquals("0",cpuEmulator.getValue("D"));
        runCycles(cpuEmulator,3);
        assertEquals("4",cpuEmulator.getValue("RAM[16]"));
        cpuEmulator.stepBack();
        assertEquals("0",cpuEmulator.getValue("RAM[16]"));
    }

    @Test
    public void testMult() throws VariableException, CommandException, ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));

        cpuEmulator.doCommand("set RAM[0] 5".split(" "));
        assertEquals("5",cpuEmulator.getValue("RAM[0]"));
        cpuEmulator.doCommand("set RAM[1] 4".split(" "));
        assertEquals("4",cpuEmulator.getValue("RAM[1]"));
        cpuEmulator.doCommand("load src/test/java/edu/uob/Mult.hack".split(" "));
        // ideally this would be more like while current instruction is not END.
        runCycles(cpuEmulator,100);

        assertEquals("20",cpuEmulator.getValue("RAM[2]"));
    }

    @Test
    public void testNoKBD() throws VariableException, CommandException, ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));
        cpuEmulator.doCommand("load src/test/java/edu/uob/kbd.hack".split(" "));
        runCycles(cpuEmulator,100);
        assertNotEquals("7",cpuEmulator.getValue("RAM[25]"));
    }

    @Test
    public void testCorrectKBD() throws VariableException, CommandException, ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));
        // set the keyboard bit of the ram to 97
        cpuEmulator.doCommand("set RAM[24576] 97".split(" "));
        cpuEmulator.doCommand("load src/test/java/edu/uob/kbd.hack".split(" "));
        runCycles(cpuEmulator,100);
        assertEquals("7",cpuEmulator.getValue("RAM[25]"));
    }

    @Test
    public void testIncorrectKBD() throws VariableException, CommandException, ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));
        // set the keyboard bit of the ram to 96
        cpuEmulator.doCommand("set RAM[24576] 96".split(" "));
        cpuEmulator.doCommand("load src/test/java/edu/uob/kbd.hack".split(" "));
        runCycles(cpuEmulator,100);
        assertNotEquals("7",cpuEmulator.getValue("RAM[25]"));
    }

    @Test
    public void testKBDAndScreen() throws VariableException, CommandException, ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));
        cpuEmulator.doCommand("load src/test/java/edu/uob/Fill.hack".split(" "));
        // if any key is pressed, screen is black (-1). Otherwise screen is white (0)
        runCycles(cpuEmulator,100000);
        // check screen white
        screenIs(cpuEmulator,"0");
        // simulate pressing a key
        cpuEmulator.doCommand("set RAM[24576] 96".split(" "));
        runCycles(cpuEmulator,100000);
        // stop pressing key
        cpuEmulator.doCommand("set RAM[24576] 0".split(" "));
        // check screen black
        screenIs(cpuEmulator,"-1");
        runCycles(cpuEmulator,100000);
        // check screen white again
        screenIs(cpuEmulator,"0");
    }
}
