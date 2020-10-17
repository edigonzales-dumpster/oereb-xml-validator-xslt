package ch.so.agi.oereb.html4oereb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.so.agi.oereb.html4oereb.saxon.ext.URLDecoder;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SAXDestination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

public class Extract2Html {
    static Logger log = LoggerFactory.getLogger(App.class);

    private String xmlFileName;
    private String outputDirectory;
    private String xsltFileName = "xml2html.xslt";
    
    public Extract2Html(String xmlFileName, String outputDirectory) {
        this.xmlFileName = xmlFileName;
        this.outputDirectory = outputDirectory;
    }
    
    public void convert() throws IOException, SaxonApiException {
        String baseFileName = xmlFileName.substring(xmlFileName.lastIndexOf('/')+1);

        int responseCode = 204;
        URL url = new URL(xmlFileName);
        HttpURLConnection connection = null;
        connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(4000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");
        responseCode = connection.getResponseCode();
        log.debug("response code: " + String.valueOf(responseCode));

        if (responseCode != 200) {
            throw new IOException("response code: " + String.valueOf(responseCode));
        }
        
        Path tempDirWithPrefix = Files.createTempDirectory("oereb-xml-validator-");
        File xmlFile = Paths.get(tempDirWithPrefix.toFile().getAbsolutePath(), baseFileName+".xml").toFile();
        InputStream initialStream = connection.getInputStream();
        java.nio.file.Files.copy(initialStream, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        initialStream.close();
        log.debug("file downloaded: " + xmlFile.getAbsolutePath());
        
        Path outputPath = Paths.get(outputDirectory);
        File htmlFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), baseFileName + ".html").toFile().getAbsolutePath());
        log.debug("html file: " + htmlFile);
        
        // Copy xslt file from resources to output directory.
        File xsltFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), xsltFileName).toFile().getAbsolutePath());
        InputStream xsltFileInputStream = Extract2Html.class.getResourceAsStream("/"+xsltFileName); 
        Files.copy(xsltFileInputStream, xsltFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        xsltFileInputStream.close();

        // Do the xml2html transformation.
        Processor proc = new Processor(false);
        proc.registerExtensionFunction(new URLDecoder());

        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(xsltFile));
        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlFile));
        Serializer outHtml = proc.newSerializer(htmlFile);
        XsltTransformer trans = exp.load();
        trans.setInitialContextNode(source);
        trans.setDestination(outHtml);
        trans.transform();
        trans.close();

        
        
    }
}
