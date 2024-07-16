/********************************************************************************
 * The contents of this file are subject to the GNU General Public License      *
 * (GPL) Version 2 or later (the "License"); you may not use this file except   *
 * in compliance with the License. You may obtain a copy of the License at      *
 * http://www.gnu.org/copyleft/gpl.html                                         *
 *                                                                              *
 * Software distributed under the License is distributed on an "AS IS" basis,   *
 * without warranty of any kind, either expressed or implied. See the License   *
 * for the specific language governing rights and limitations under the         *
 * License.                                                                     *
 *                                                                              *
 * This file was originally developed as part of the software suite that        *
 * supports the book "The Elements of Computing Systems" by Nisan and Schocken, *
 * MIT Press 2005. If you modify the contents of this file, please document and *
 * mark your changes clearly, for the benefit of others.                        *
 ********************************************************************************/

package Hack.CPUEmulator;

import Hack.Utilities.*;
import Hack.ComputerParts.*;

import java.util.HashSet;
import java.util.Set;

/**
 * A Random Access Memory, which is mapped to a screen, and enables a segmented view on it.
 */
public class RAM extends PointedMemory implements Cloneable
{
    // The amount of miliseconds that a label should flash.
    private static final int LABEL_FLASH_TIME = 500;

    private static final short[] emptyScreen = new short[Definitions.SCREEN_SIZE_IN_WORDS];

    // The gui of the screen
    private ScreenGUI screen;

    // memory segments mapping
    private MemorySegment[][] segments;

    // The list of RAM locations that have been accessed in this execution.
    private Set<Integer> accessedRAMLocations;

    /**
     * Constructs a new RAM with the given optional GUI components:
     * mainGUI - the main GUI of the ram.
     * segments - an array of the memory's size, where each entry contains an array of
     *            memorySegments (or null). If a memorySegment is an instance of
     *            PointedMemorySegment, its pointer address will be set according to
     *            the memory value at the entry's location.
     *            If a memorySegment is an instance of memorySegment, its start
     *            address will be set according to the memory value at the entry's location.
     * screenGUI - the GUI of the screen.
     */
    public RAM(PointedMemoryGUI mainGUI, MemorySegment[][] segments, ScreenGUI screenGUI) {
        super(Definitions.RAM_SIZE, mainGUI);
        this.segments = segments;
        this.screen = screenGUI;
        this.accessedRAMLocations=new HashSet<>();
    }

    /**
     * Returns the value stored at the given address
     */
    public void setValueAt(int address, short value, boolean quiet) {
        super.setValueAt(address, value, quiet);
        accessedRAMLocations.add(address);

        // if screen area changed, update its GUI
        if (screen != null && address >= Definitions.SCREEN_START_ADDRESS
             && address < Definitions.SCREEN_START_ADDRESS + Definitions.SCREEN_SIZE_IN_WORDS)
            screen.setValueAt((short)(address - Definitions.SCREEN_START_ADDRESS), value);

        // if a memory segment pointer changed, update its GUI
        if (segments != null && segments[address] != null) {

            for (int i = 0; i < segments[address].length; i++) {
                // check if the relevant memory segment is a pointed one.
                if (segments[address][i] instanceof PointedMemorySegment)
                    ((PointedMemorySegment)segments[address][i]).setPointerAddress(value);
                else
                    segments[address][i].setStartAddress(value);
            }
        }
    }

    public int getNumberOfRAMLocationsAccessed(){
        return accessedRAMLocations.size();
    }

    /**
     * Sets a name for the label at the given address
     */
    public synchronized void setLabel(int address, String name, boolean quiet) {
        if (hasGUI && gui instanceof LabeledPointedMemoryGUI) {
            ((LabeledPointedMemoryGUI)gui).setLabel(address, name);
            if (!quiet) {
                ((LabeledPointedMemoryGUI)gui).labelFlash(address);
                try {
                    wait(LABEL_FLASH_TIME);
                } catch (InterruptedException ie) {}
                ((LabeledPointedMemoryGUI)gui).hideLabelFlash();
            }
        }
    }


    /**
     * Clears all labels.
     */
    public void clearLabels() {
        if (hasGUI && gui instanceof LabeledPointedMemoryGUI)
            ((LabeledPointedMemoryGUI)gui).clearLabels();
    }

    /**
     * Resets the contents of the computer part.
     */
    public void reset() {
        super.reset();
        if (screen != null)
            screen.reset();
    }

    /**
     * Clears the contenets of the screen.
     */
    public void clearScreen() {
        setContents(emptyScreen, Definitions.SCREEN_START_ADDRESS);

        if (screen != null)
            screen.reset();
    }

    public void refreshGUI() {
        super.refreshGUI();

        // Update segments
        if (segments != null)
            for (int address = 0; address < size; address++) {
                if (segments[address] != null) {
                    for (int i = 0; i < segments[address].length; i++) {
                        // check if the relevant memory segment is a pointed one.
                        if (segments[address][i] instanceof PointedMemorySegment)
                            ((PointedMemorySegment)segments[address][i]).setPointerAddress(mem[address]);
                        else
                            segments[address][i].setStartAddress(mem[address]);
                    }
                }
            }
    }

    @Override
    public RAM clone() {
        try {
            RAM clone = (RAM) super.clone();
            clone.screen=screen; // I don't think I need to change this as it's a gui.
            if(segments!=null) {
                clone.segments = new MemorySegment[segments.length][]; // Create a new array
                for (int i = 0; i < segments.length; i++) {
                    clone.segments[i] = new MemorySegment[segments[i].length]; // Initialize sub-array
                    for (int j = 0; j < segments[i].length; j++) {
                        clone.segments[i][j] = segments[i][j].clone();
                    }
                }
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
