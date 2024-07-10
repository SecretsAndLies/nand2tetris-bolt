import Hack.Assembler.HackAssembler;
import Hack.Assembler.HackAssemblerTranslator;
import Hack.Translators.HackTranslatorException;

import static Hack.Utilities.Definitions.ROM_SIZE;

public class AssemblerCommandLineMain {
    public static void main(String[] args) {
        try {
            new HackAssembler("/Users/alijardine/Documents/Code/nand2tetris-bolt/SimulatorsPackage/src/test/java/edu/uob/invalid.asm",
                    ROM_SIZE,
                    HackAssemblerTranslator.NOP,
                    false);
            System.exit(0);
        } catch (HackTranslatorException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}