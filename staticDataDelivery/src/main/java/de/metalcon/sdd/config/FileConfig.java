package de.metalcon.sdd.config;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.metalcon.sdd.error.InvalidAttrNameException;

public class FileConfig extends Config {

    public FileConfig(Path configPath) throws InvalidAttrNameException {
        super();
        
        xmlLoad(configPath);
    }
    
    private void xmlLoad(Path configPath) throws
            InvalidAttrNameException {
        Document domDoc = null;
        try {
            domDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(configPath.toFile());
        } catch (SAXException | IOException | ParserConfigurationException
                | FactoryConfigurationError e) {
            // TODO: handle this
            throw new RuntimeException();
        }
        
        domDoc.normalize();
        
        Element domRoot = domDoc.getDocumentElement();
        xmlAssertNodeName(domRoot, "sdd-config");
        
        Element domLeveldb  = null;
        Element domNeo4j    = null;
        Element domDetails  = null;
        Element domEntities = null;
        
        for (Node domNode = domRoot.getFirstChild(); domNode != null;
                domNode = domNode.getNextSibling())
            if (domNode.getNodeType() == Node.ELEMENT_NODE) {
                Element domElement = (Element) domNode;
                switch (domElement.getNodeName()) {
                    case "leveldb":  domLeveldb  = domElement; break;
                    case "neo4j":    domNeo4j    = domElement; break;
                    case "details":  domDetails  = domElement; break;
                    case "entities": domEntities = domElement; break;
                        
                    default:
                        // TODO: handle this (malformed xml)
                        throw new RuntimeException();
                }
            }
        
        xmlLoadLeveldb(domLeveldb);
        xmlLoadNeo4j(domNeo4j);
        xmlLoadDetails(domDetails);
        xmlLoadEntities(domEntities);
    }
    
    private void xmlLoadLeveldb(Element domLeveldb) {
        xmlAssertHasAttribute(domLeveldb, "path");
        setLeveldbPath(domLeveldb.getAttribute("path"));
    }
    
    private void xmlLoadNeo4j(Element domNeo4j) {
        xmlAssertHasAttribute(domNeo4j, "path");
        setNeo4jPath(domNeo4j.getAttribute("path"));
    }
    
    private void xmlLoadDetails(Element domDetails) {
        for (Node domNode = domDetails.getFirstChild(); domNode != null;
                domNode = domNode.getNextSibling())
            if (domNode.getNodeType() == Node.ELEMENT_NODE)
                xmlLoadDetail((Element) domNode);
    }
    
    private void xmlLoadDetail(Element domDetail) {
        xmlAssertNodeName(domDetail, "detail");
        xmlAssertHasAttribute(domDetail, "name");

        String name = domDetail.getAttribute("name");
        addDetail(name);
    }
    
    private void xmlLoadEntities(Element domEntities) throws
            InvalidAttrNameException {
        for (Node domNode = domEntities.getFirstChild(); domNode != null;
                domNode = domNode.getNextSibling())
            if (domNode.getNodeType() == Node.ELEMENT_NODE)
                xmlLoadEntity((Element) domNode);
    }
    
    private void xmlLoadEntity(Element domEntity) throws
            InvalidAttrNameException {
        xmlAssertNodeName(domEntity, "entity");
        xmlAssertHasAttribute(domEntity, "type");
        
        String type = domEntity.getAttribute("type");
        
        if (isValidEntityType(type))
            // TODO: handle this (duplicate entities)
            throw new RuntimeException();
        
        MetaEntity entity = new MetaEntity();
        
        for (Node domNode = domEntity.getFirstChild(); domNode != null;
                domNode = domNode.getNextSibling())
            if (domNode.getNodeType() == Node.ELEMENT_NODE) {
                Element domElement = (Element) domNode;
                switch (domElement.getNodeName()) {
                    case "attr":
                        xmlLoadEntityAttr(entity, domElement);
                        break;
                        
                    case "output":
                        xmlLoadEntityOutput(entity, domElement);
                        break;
                        
                    default:
                        // TODO: handle this (malformed xml)
                        throw new RuntimeException();
                }
            }
        
        addEntity(type, entity);
    }
    
    private void xmlLoadEntityAttr(MetaEntity entity, Element domAttr) throws
            InvalidAttrNameException {
        xmlAssertNodeName(domAttr, "attr");
        xmlAssertHasAttribute(domAttr, "name");
        xmlAssertHasAttribute(domAttr, "type");

        String name = domAttr.getAttribute("name");
        String type = domAttr.getAttribute("type");
        entity.addAttr(name, new MetaType(type));
    }
    
    private void xmlLoadEntityOutput(MetaEntity entity, Element domOutput) {
        xmlAssertNodeName(domOutput, "output");
        xmlAssertHasAttribute(domOutput, "detail");
        
        String detail = domOutput.getAttribute("detail");
        MetaEntityOutput output = new MetaEntityOutput();
        
        for (Node domNode = domOutput.getFirstChild(); domNode != null;
                domNode = domNode.getNextSibling())
            if (domNode.getNodeType() == Node.ELEMENT_NODE)
                xmlLoadEntityOutputOattr(output, (Element) domNode);
        
        entity.addOutput(detail, output);
    }
    
    private void xmlLoadEntityOutputOattr(MetaEntityOutput output,
                                          Element domOattr) {
        xmlAssertNodeName(domOattr, "oattr");
        xmlAssertHasAttribute(domOattr, "attr");
        
        String attr = domOattr.getAttribute("attr");
        
        if (domOattr.hasAttribute("detail")) {
            String detail = domOattr.getAttribute("detail");

            output.addOattr(attr, detail);
        } else
            output.addOattr(attr, "");
    }
    
    private void xmlAssertNodeName(Element domElement, String nodeName) {
        if (!domElement.getNodeName().equals(nodeName))
            // TODO: handle this (malformed xml)
            throw new RuntimeException();
    }
    
    private void xmlAssertHasAttribute(Element domElement, String attribute) {
        if (!domElement.hasAttribute(attribute))
            // TODO: handle this (malformed xml)
            throw new RuntimeException();
    }
    
}
