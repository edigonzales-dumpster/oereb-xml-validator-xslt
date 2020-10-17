package ch.so.agi.oereb.html4oereb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Extract2Html {
    static Logger log = LoggerFactory.getLogger(App.class);

    private String xmlFileName;
    private String outputDirectory;
    
    public Extract2Html(String xmlFileName, String outputDirectory) {
        this.xmlFileName = xmlFileName;
        this.outputDirectory = outputDirectory;
    }
    
    public void convert() {
        
    }
}
