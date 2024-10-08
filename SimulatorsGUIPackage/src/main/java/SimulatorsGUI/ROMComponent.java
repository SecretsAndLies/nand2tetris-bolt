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

package SimulatorsGUI;

import Hack.Controller.*;
import Hack.Translators.HackCommand;
import HackGUI.*;
import Hack.CPUEmulator.*;
import Hack.Events.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.event.*;
import java.awt.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import Hack.Assembler.*;


/**
 * This class represents the GUI of a ROM.
 */
public class ROMComponent extends PointedMemoryComponent implements ROMGUI {

    // A vector containing the listeners to this object.
    private Vector programEventListeners;

    private ArrayList<BreakpointChangedListener> breakpointChangedListeners = new ArrayList<>();

    // The ASM format.
    private final static int ASM_FORMAT = ROM.ASM_FORMAT;

    // The Symbolic format (ie: @i instead of @16)
    private final static int SYM_FORMAT = ROM.SYM_FORMAT;

    // The load file button.
    protected MouseOverJButton loadButton = new MouseOverJButton();

    // The icon on the load file button.
    private ImageIcon loadIcon = new ImageIcon(Utilities.imagesDir + "open2.gif");

    // The file filter of this component.
    private FileFilter filter;

    // The file chooser component.
    private JFileChooser fileChooser;

    // The hack assembler translator.
    private HackAssemblerTranslator translator = HackAssemblerTranslator.getInstance();

    // The text field containing the message (for example "Loading...").
    private JTextField messageTxt = new JTextField();

    // The possible numeric formats.
    private String[] format = {"Asm", "Dec", "Hex", "Bin", "Sym"};

    // The combo box for choosing the numeric format.
    protected JComboBox romFormat = new JComboBox(format);

    // The name of the current program.
    private String programFileName;
    private ArrayList<Integer> breakpointRows = new ArrayList<>();

    private ArrayList<HackCommand> hackCommands;

    private File currentFile;

    /**
     * Constructs a new ROMComponent.
     */
    public ROMComponent() {
        dataFormat = ASM_FORMAT;
        programEventListeners = new Vector();
        filter = new ROMFileFilter();
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);
        jbInit();
        memoryTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int row = memoryTable.rowAtPoint(e.getPoint());
                int column = memoryTable.columnAtPoint(e.getPoint());
                if (row > -1 && column == 1 && dataFormat==SYM_FORMAT) {
                    Object value = memoryTable.getValueAt(row, column);
                    if(value!=null){
                        memoryTable.setToolTipText(value.toString());
                    }
                }
            }
        });
    }


    public void setNumericFormat(int formatCode) {
        super.setNumericFormat(formatCode);
        switch(formatCode) {
            case ASM_FORMAT:
                romFormat.setSelectedIndex(0);
                break;
            case Format.DEC_FORMAT:
                romFormat.setSelectedIndex(1);
                break;
            case Format.HEX_FORMAT:
                romFormat.setSelectedIndex(2);
                break;
            case Format.BIN_FORMAT:
                romFormat.setSelectedIndex(3);
                break;
            case SYM_FORMAT:
                romFormat.setSelectedIndex(4);
                break;

        }
    }

    public void setHackCommandContents(ArrayList<HackCommand> hackCommands){
        this.hackCommands=hackCommands;
    }

    // The listener for changes in breakpoints.
    private BreakpointsChangedListener breakpointsChangedListener= new BreakpointsChangedListener() {
        @Override
        public void breakpointsChanged(BreakpointsChangedEvent event) {
            breakpointRows = new ArrayList<>();
            for (int i=0; i<event.getBreakpoints().size(); i++){
                Breakpoint breakpoint = (Breakpoint) event.getBreakpoints().get(i);
                if(breakpoint.getVarName().equals("PC")){ // todo: magic variable name
                    // in theory this should also work with ROM, but that's buggy atm.
                    breakpointRows.add(Integer.valueOf(breakpoint.getValue()));
                }
            }
            repaint();
            revalidate();
        }
    };


    public BreakpointsChangedListener getBreakpointsChangedListener() {
        return breakpointsChangedListener;
    }

    /**
     * Registers the given ProgramEventListener as a listener to this GUI.
     */
    public void addProgramListener(ProgramEventListener listener) {
        programEventListeners.addElement(listener);
    }

    /**
     * Un-registers the given ProgramEventListener from being a listener to this GUI.
     */
    public void removeProgramListener(ProgramEventListener listener) {
        programEventListeners.removeElement(listener);
    }

    public void notifyBreakpointListeners(BreakpointChangedEvent breakpointChangedEvent) {
        for (BreakpointChangedListener b : breakpointChangedListeners){
            b.breakpointChanged(breakpointChangedEvent);
        }
    }

    @Override
    public void addBreakpointChangedListener(BreakpointChangedListener listener) {
        breakpointChangedListeners.add(listener);
    }

    @Override
    public void removeBreakpointChangedListener(BreakpointChangedListener listener) {
        breakpointChangedListeners.remove(listener);
    }

    /**
     * Notifies all the ProgramEventListeners on a change in the ROM's program by
     * creating a ProgramEvent (with the new event type and program's file name) and sending it
     * using the programChanged method to all the listeners.
     */
    public void notifyProgramListeners(byte eventType, String programFileName) {
        ProgramEvent event = new ProgramEvent(this, eventType, programFileName);
        for (int i=0; i<programEventListeners.size(); i++)
            ((ProgramEventListener)programEventListeners.elementAt(i)).programChanged(event);
    }

    /**
     * Overrides to add the program clear functionality.
     */
    public void notifyClearListeners() {
        super.notifyClearListeners();
        notifyProgramListeners(ProgramEvent.CLEAR, null);
    }

    protected DefaultTableCellRenderer getCellRenderer() {
        return new ROMTableCellRenderer();
    }

    /**
     * Sets the current program file name with the given name.
     */
    public void setProgram(String programFileName) {
        this.programFileName = programFileName;
    }

    /**
     * Returns the value at the given index in its string representation.
     */
    public String getValueAsString(int index) {
        if (dataFormat != ASM_FORMAT)
            return super.getValueAsString(index);
        else
            return Format.translateValueToString(values[index], Format.DEC_FORMAT);
    }

    /**
     * Hides the displayed message.
     */
    public void hideMessage(){
        messageTxt.setText("");
        messageTxt.setVisible(false);
        loadButton.setVisible(true);
        searchButton.setVisible(true);
        romFormat.setVisible(true);
    }

    /**
     * Displays the given message.
     */
    public void showMessage(String message){
        messageTxt.setText(message);
        loadButton.setVisible(false);
        searchButton.setVisible(false);
        romFormat.setVisible(false);
        messageTxt.setVisible(true);
    }

    /**
     * Translates a given string to a short according to the current format.
     */
    protected short translateValueToShort(String data) throws TranslationException {
        short result = 0;
        if(dataFormat != ASM_FORMAT)
            result = super.translateValueToShort(data);
        else {
            try {
                result = translator.textToCode(data);
            } catch (AssemblerException ae) {
                throw new TranslationException(ae.getMessage());
            }
        }
        return result;
    }

    /**
     * Translates a given short to a string according to the current format.
     */
    protected String translateValueToString(short value, int index) {
        String result = null;
         if (dataFormat==ASM_FORMAT) {
            try {
                result = translator.codeToText(value);
            } catch (AssemblerException ae) {}
        }
        else if (dataFormat==SYM_FORMAT){
             try {
                 if(hackCommands!=null) {
                     result = hackCommands.get(index).getCommandString();
                 }
             }
             catch (IndexOutOfBoundsException e){}
            if(result!=null && result.equals("unknown")){
                try {
                    result = translator.codeToText(value);
                } catch (AssemblerException e) {
                }
            }
        }
        else{
                result = super.translateValueToString(value, index);
         }
        return result;
    }

    // Initializes this rom.
    private void jbInit()  {
        loadButton.setIcon(loadIcon);
        loadButton.setBounds(new Rectangle(97, 2, 31, 25));
        loadButton.setToolTipText("Load Program");
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadButton_actionPerformed(e);
            }
        });
        messageTxt.setBackground(SystemColor.info);
        messageTxt.setEnabled(false);
        messageTxt.setFont(Utilities.labelsFont);
        messageTxt.setPreferredSize(new Dimension(70, 20));
        messageTxt.setDisabledTextColor(Color.red);
        messageTxt.setEditable(false);
        messageTxt.setHorizontalAlignment(SwingConstants.CENTER);
        messageTxt.setBounds(new Rectangle(37, 3, 154, 22));
        messageTxt.setVisible(false);
        romFormat.setPreferredSize(new Dimension(125, 23));
        romFormat.setBounds(new Rectangle(39, 3, 56, 23));
        romFormat.setFont(Utilities.thinLabelsFont);
        romFormat.setToolTipText("Display Format");
        romFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                romFormat_actionPerformed(e);
            }
        });
        // todo: this is kinda messy - can you put this somewhere else?
        super.memoryTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    int rowNumber = ROMComponent.super.memoryTable.rowAtPoint(e.getPoint());
                    Breakpoint breakpoint = new Breakpoint("PC",Integer.toString(rowNumber));
                    BreakpointChangedEvent breakpointChangedEvent =  new BreakpointChangedEvent(this,breakpoint);
                    notifyBreakpointListeners(breakpointChangedEvent);
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.add(messageTxt, null);
        this.add(loadButton);
        this.add(romFormat, null);
    }

    /**
     * Sets the current working dir.
     */
    public void setWorkingDir(File file) {
        fileChooser.setCurrentDirectory(file);
    }

    /**
     * Opens the file chooser for loading a new program.
     */
    public void loadProgram() {
        int returnVal = fileChooser.showDialog(this, "Load ROM");
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            notifyProgramListeners(ProgramEvent.LOAD,
                    currentFile.getAbsolutePath());
        }
    }

    public void forceLoadProgram(){
        int choice = JOptionPane.showConfirmDialog(null,
                "Your assembly file has been updated. Would you like to load this change?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            notifyProgramListeners(ProgramEvent.UPDATED,
                    currentFile.getAbsolutePath());
        }
    }

    /**
     * Implements the action of clicking the load button.
     */
    public void loadButton_actionPerformed(ActionEvent e) {
        loadProgram();
    }


    /**
     * Implemeting the action of changing the selected item in the combo box
     */
    public void romFormat_actionPerformed(ActionEvent e) {
        String newFormat = (String)romFormat.getSelectedItem();
        if(newFormat.equals(format[0])) {
            setNumericFormat(ASM_FORMAT);
        }
        else if (newFormat.equals(format[1])) {
            setNumericFormat(Format.DEC_FORMAT);
        }
        else if (newFormat.equals(format[2])) {
            setNumericFormat(Format.HEX_FORMAT);
        }
        else if (newFormat.equals(format[3])) {
            setNumericFormat(Format.BIN_FORMAT);
        }
        else if (newFormat.equals(format[4])) {
            setNumericFormat(SYM_FORMAT);
        }

    }

    /**
     * An inner class which implemets the cell renderer of the rom table,
     * giving the feature of coloring the background of a specific cell.
     */
    public class ROMTableCellRenderer extends PointedMemoryTableCellRenderer {

        public void setRenderer(int row, int column) {
            super.setRenderer(row, column);
            if(breakpointRows.contains(row)) {
                setBackground(new Color(255, 102, 102));
            }
            if((dataFormat==ASM_FORMAT || dataFormat==SYM_FORMAT) && column == 1)
                setHorizontalAlignment(SwingConstants.LEFT);
        }
    }
}
