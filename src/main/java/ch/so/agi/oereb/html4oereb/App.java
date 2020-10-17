/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ch.so.agi.oereb.html4oereb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    static Logger log = LoggerFactory.getLogger(App.class);

    private static String xmlFileName;
    private static String outputDirectory;

    public static void main(String[] args) {
        int argi = 0;
        for(;argi<args.length;argi++) {
            String arg = args[argi];
            
            if(arg.equals("--xml")) {
                argi++;
                xmlFileName = args[argi];
            } else if (arg.equals("--out")) {
                argi++;
                outputDirectory = args[argi];
            } else if (arg.equals("--help")) {
                System.err.println();
                System.err.println("--xml       The input xml file (required).");
                System.err.println("--out       The output directory (required).");
                System.err.println();
                return;
            }
        }

        if (xmlFileName == null) {
            log.error("Input xml file is required.");
            System.exit(2);
        }
        
        if (outputDirectory == null) {
            log.error("Output directory is required.");
            System.exit(2);
        }
        
        log.info("xml file: " + xmlFileName);
        
        Extract2Html extract2Html = new Extract2Html(xmlFileName, outputDirectory);
        extract2Html.convert();
        
        
        System.out.println("Hallo Welt.");
    }
}