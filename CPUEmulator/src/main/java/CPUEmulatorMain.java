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

import Hack.Controller.*;
import Hack.CPUEmulator.*;
import HackGUI.*;
import SimulatorsGUI.*;
import javax.swing.*;

/**
 * The CPU Emulator.
 */
public class CPUEmulatorMain
{
  /**
   * The command line CPU Emulator program.
   */
  public static void main(String[] args) {
        if (args.length > 2)
            System.err.println("Usage: java CPUEmulatorMain [script name] [timeout (0) for infinite]");
        else if (args.length == 0) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
            }

            CPUEmulatorGUI simulatorGUI = new CPUEmulatorComponent();

            ControllerGUI controllerGUI = new ControllerComponent();
            CPUEmulatorApplication application =
                new CPUEmulatorApplication(controllerGUI, simulatorGUI, "bin/scripts/defaultCPU.txt",
                                           "bin/help/cpuUsage.html", "bin/help/cpuAbout.html");
        }
        else {
            CPUEmulator cpuEmulator = new CPUEmulator();
            cpuEmulator.setCPUTimeout(Long.valueOf(args[1]));
            new HackController(cpuEmulator, args[0]);
            // the rest of this will not run if Hack Controller fails, it exits in failure -1 with a message.
            // otherwise, sucess message is printed, followed by this.
            org.json.JSONObject jsonResponse = new org.json.JSONObject();
            try {
                jsonResponse.put("cycles_used", Integer.valueOf(cpuEmulator.getValue("time")));
            } catch (VariableException e) {
                throw new RuntimeException(e);
            }
            jsonResponse.put("ram_used", cpuEmulator.getNumberOfRAMLocationsAccessed());
            System.out.println(jsonResponse);
        }
    }
}
