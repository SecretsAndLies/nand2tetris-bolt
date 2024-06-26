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

import Hack.Controller.*;

/**
 * A CPU Emulator application.
 */
public class CPUEmulatorApplication extends HackApplication {

    /**
     * Constructs a new CPUEmulatorApplication with the given controller GUI
     * component, the simulator GUI component, the default script name and the names of
     * the help files.
     */
    public CPUEmulatorApplication(ControllerGUI controllerComponent,
                                  CPUEmulatorGUI simulatorComponent, String defaultScript,
                                  String contentsFileName, String aboutFileName) {
        super(new CPUEmulator(simulatorComponent), controllerComponent, simulatorComponent,
              defaultScript, contentsFileName, aboutFileName);
        controllerComponent.getBreakpointWindow().addBreakpointListener(simulatorComponent.getROM().getBreakpointsChangedListener());
        simulatorComponent.getROM().addBreakpointChangedListener(controllerComponent.getBreakpointWindow().getBreakpointChangedListener());
    }
}
