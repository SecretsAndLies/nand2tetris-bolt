package Hack.Controller;

public interface BreakpointWindowGUI {
    public void addBreakpointListener (BreakpointsChangedListener listener);
    public BreakpointChangedListener getBreakpointChangedListener();

}
