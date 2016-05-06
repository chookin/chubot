package cmri.utils.configuration;

import org.apache.commons.lang3.Validate;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * read xml resouce to get keyValue. FileHelper format is:

     <configuration>
         <property>
             <name>myName</name>
             <value>myValue</value>
             <description>descriptions</description>
         </property>
     </configuration>
 */
public class XmlConfigFile {
    private final Object file;

    public XmlConfigFile(Object file) {
        Validate.notNull(file, "file");
        this.file = file;
    }

    public SortedMap<String, String> getProperties()
            throws ParserConfigurationException, SAXException, IOException {
        return this.getProperties(this.file);
    }

    SortedMap<String, String> getProperties(Object file)
            throws ParserConfigurationException, SAXException, IOException {
        SortedMap<String, String> rst = new TreeMap<>();
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        // ignore all comments inside the xml file
        docBuilderFactory.setIgnoringComments(true);
        DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
        Document doc = null;
        Element root = null;

        if (file instanceof URL) { // an URL resource
            URL url = (URL) file;
            doc = builder.parse(url.toString());
        } else if (file instanceof String) {
            doc = builder.parse(new File((String) file));
        } else if (file instanceof InputStream) {
            try {
                doc = builder.parse((InputStream) file);
            } finally {
                ((InputStream) file).close();
            }
        } else if (file instanceof Element) {
            root = (Element) file;
        }

        if (doc == null && root == null) {
            throw new IOException(file + " not found");
        }

        if (root == null) {
            root = doc.getDocumentElement();
        }
        if (!"configuration".equals(root.getTagName()))
            throw new IOException(
                    "bad configuration file: top-level element not <configuration>");
        NodeList props = root.getChildNodes();
        for (int i = 0; i < props.getLength(); i++) {
            Node propNode = props.item(i);
            if (!(propNode instanceof Element))
                continue;
            Element prop = (Element) propNode;
            if ("configuration".equals(prop.getTagName())) {
                rst.putAll(getProperties(prop));
                continue;
            }
            if (!"property".equals(prop.getTagName()))
                throw new RuntimeException(
                        "bad conf file: element not <property>");
            NodeList fields = prop.getChildNodes();
            String attr = null;
            String value = null;
            for (int j = 0; j < fields.getLength(); j++) {
                Node fieldNode = fields.item(j);
                if (!(fieldNode instanceof Element))
                    continue;
                Element field = (Element) fieldNode;
                if ("name".equals(field.getTagName()) && field.hasChildNodes()) {
                    attr = ((Text) field.getFirstChild()).getData().trim();
                }
                if ("value".equals(field.getTagName()) && field.hasChildNodes()) {
                    value = ((Text) field.getFirstChild()).getData();
                }
            }
            if (attr == null || value == null) {
                continue;
            }
            rst.put(attr.toLowerCase(), value);
        }
        return rst;
    }
}
