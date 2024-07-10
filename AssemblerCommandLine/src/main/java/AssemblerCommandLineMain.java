import Hack.Assembler.HackAssembler;
import Hack.Assembler.HackAssemblerTranslator;
import Hack.Translators.HackTranslatorException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static Hack.Utilities.Definitions.ROM_SIZE;

public class AssemblerCommandLineMain {
    public static void main(String[] args) {
        if(args.length<1){
            System.exit(2);
        }
        try {
            File tempFile  = File.createTempFile("user_code",".asm");
            FileWriter writer = new FileWriter(tempFile);
            writer.write(args[0]);
            writer.close();

            HackAssembler assembler = new HackAssembler(tempFile.getAbsolutePath(),
                    ROM_SIZE,
                    HackAssemblerTranslator.NOP,
                    false);
            System.out.print(assembler.getCompiledProgram());
            System.exit(0);
        } catch (HackTranslatorException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}