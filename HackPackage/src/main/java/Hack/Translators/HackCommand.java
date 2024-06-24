package Hack.Translators;

public class HackCommand {
    private short commandShort;
    private String commandString;

    public HackCommand(String commandString, short commandShort) {
        this.commandShort=commandShort;
        this.commandString=commandString;
        // todo: potentially this class could eventually contain all the things you need (eg
        // the hex value, if it's a comment etc?
    }

    public short getCommandShort() {
        return commandShort;
    }

    public String getCommandString() {
        return commandString;
    }

    @Override
    public String toString() {
        return "HackCommand{" +
                "commandShort=" + commandShort +
                ", commandString='" + commandString + '\'' +
                '}';
    }
}
