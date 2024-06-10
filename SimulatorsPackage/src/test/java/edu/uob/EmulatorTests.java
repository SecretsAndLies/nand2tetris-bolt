package edu.uob;


import Hack.CPUEmulator.CPUEmulator;
import Hack.Controller.CommandException;
import Hack.Controller.ProgramException;
import Hack.Controller.VariableException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


public class EmulatorTests {


    public void runCycles(CPUEmulator cpuEmulator, int numCycles) throws VariableException, CommandException, ProgramException {
        for (int i = 0; i < numCycles; i++) {
            cpuEmulator.doCommand("ticktock".split(" "));
        }
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


    public boolean screenIs(CPUEmulator cpuEmulator, String value) throws VariableException {
        for (int i = 16384; i < 24576; i++) {
            if(!cpuEmulator.getValue("RAM["+i+"]").equals(value)){
                return false;
            }
        }
        return true;
    }

    @Test
    public void testSimpleScreen() throws VariableException, CommandException,
            ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));
        cpuEmulator.doCommand("load src/test/java/edu/uob/SimpleScreen.hack".split(" "));
        assertEquals("0", cpuEmulator.getValue("RAM[16384]"));
        runCycles(cpuEmulator,2);
        assertEquals("-1", cpuEmulator.getValue("RAM[16384]"));
    }

    @Test
    public void testKBDAndScreen() throws VariableException, CommandException, ProgramException {
        CPUEmulator cpuEmulator = new CPUEmulator();
        cpuEmulator.setWorkingDir(new File(System.getProperty("user.dir")));
        cpuEmulator.doCommand("load src/test/java/edu/uob/Fill.hack".split(" "));
        // if a key is pressed, screen is black (-1). Otherwise, screen is white (0)
        runCycles(cpuEmulator,1000000);
        // check screen white
        assertTrue(screenIs(cpuEmulator,"0"));
        // simulate pressing a key
        cpuEmulator.doCommand("set RAM[24576] 96".split(" "));
        assertEquals("96", cpuEmulator.getValue("RAM[24576]"));
        runCycles(cpuEmulator,1000000);
        // check key is still being pressed
        assertEquals("96", cpuEmulator.getValue("RAM[24576]"));
        // check screen black
        assertTrue(screenIs(cpuEmulator,"-1"));
        // stop pressing key
        cpuEmulator.doCommand("set RAM[24576] 0".split(" "));
        runCycles(cpuEmulator,1000000);
        // check screen white again
        assertTrue(screenIs(cpuEmulator,"0"));
    }
}
