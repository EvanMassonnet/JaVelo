package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

/**
 * représente un générateur d'itinéraire au format GPX.
 *
 * @author Evan Massonnet (346642)
 */


public class GpxGenerator {
    private GpxGenerator(){

    }

    /**
     * prend en arguments un itinéraire et le profil de cet itinéraire et
     * retourne le document GPX correspondant
     * @return
     */
    public static Document createGpx(Route route, ElevationProfile profile){
        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        List<Edge> edges = route.edges();
        double currentPosition = 0;

        Element rtept = doc.createElement("rtept");
        rtept.setAttribute("lat", Double.toString(Math.toDegrees(route.pointAt(0).lat())));
        rtept.setAttribute("lon", Double.toString(Math.toDegrees(route.pointAt(0).lon())));
        rte.appendChild(rtept);

        Element ele = doc.createElement("ele");
        ele.setTextContent(Double.toString(profile.elevationAt(0)));
        rtept.appendChild(ele);


        for(Edge e : edges){
            currentPosition += e.length();

            rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", Double.toString(Math.toDegrees(route.pointAt(currentPosition).lat())));
            rtept.setAttribute("lon", Double.toString(Math.toDegrees(route.pointAt(currentPosition).lon())));
            rte.appendChild(rtept);

            ele = doc.createElement("ele");
            ele.setTextContent(Double.toString(profile.elevationAt(currentPosition)));
            rtept.appendChild(ele);
        }
        return doc;
    }

    /**
     * prend en arguments un nom de fichier, un itinéraire et le profil
     * de cet itinéraire et écrit le document GPX correspondant dans le fichier
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile){
        Document newDocument = createGpx(route, profile);
        try{
            OutputStream out = new FileOutputStream(Path.of(".") + fileName);

            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(newDocument),
                    new StreamResult(out));

            out.close();
        } catch (TransformerException e){
            throw new Error(e); // Should never happen
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * utilisée pour créer un nouveau document
     * @return
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }


}
