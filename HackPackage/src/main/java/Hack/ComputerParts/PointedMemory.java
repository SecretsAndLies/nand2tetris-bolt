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

package Hack.ComputerParts;

/**
 * A memory which enables pointing at addresses.
 */
public class PointedMemory extends Memory implements Cloneable {

    /**
     * Constructs a new PointedMemory of the given size with the given optional GUI.
     */
    public PointedMemory(int size, PointedMemoryGUI gui) {
        super(size, gui);
    }

    /**
     * Constructs a new PointedMemory of the given size with the given optional GUI and
     * the given legal values range.
     */
    public PointedMemory(int size, PointedMemoryGUI gui, short minValue, short maxValue) {
        super(size, gui, minValue, maxValue);
    }

    /**
     * Set the pointer to point at the given address.
     */
    public void setPointerAddress(int address) {
        if (displayChanges)
            ((PointedMemoryGUI)gui).setPointer(address);
    }

    public PointedMemory clone() throws CloneNotSupportedException {
        PointedMemory clone = (PointedMemory) super.clone();
        return clone;
    }

    public void reset() {
        setPointerAddress(0);
        super.reset();
    }
}
