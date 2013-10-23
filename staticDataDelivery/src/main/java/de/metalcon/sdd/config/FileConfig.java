package de.metalcon.sdd.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class FileConfig extends Config {

    public FileConfig(Path configPath) {
        super();
        
        xmlLoad(configPath);
        validateSemantics();
    }
    
    private void xmlLoad(Path configPath) {
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
        leveldbPath = domLeveldb.getAttribute("path");
    }
    
    private void xmlLoadNeo4j(Element domNeo4j) {
        xmlAssertHasAttribute(domNeo4j, "path");
        neo4jPath = domNeo4j.getAttribute("path");
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
        details.add(name);
    }
    
    private void xmlLoadEntities(Element domEntities) {
        for (Node domNode = domEntities.getFirstChild(); domNode != null;
                domNode = domNode.getNextSibling())
            if (domNode.getNodeType() == Node.ELEMENT_NODE)
                xmlLoadEntity((Element) domNode);
    }
    
    private void xmlLoadEntity(Element domEntity) {
        xmlAssertNodeName(domEntity, "entity");
        xmlAssertHasAttribute(domEntity, "name");
        
        String name = domEntity.getAttribute("name");
        
        if (entities.containsKey(name))
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
        
        entities.put(name, entity);
    }
    
    private void xmlLoadEntityAttr(MetaEntity entity, Element domAttr) {
        xmlAssertNodeName(domAttr, "attr");
        xmlAssertHasAttribute(domAttr, "name");
        xmlAssertHasAttribute(domAttr, "type");

        String name = domAttr.getAttribute("name");
        String type = domAttr.getAttribute("type");
        entity.attrs.put(name, new MetaType(type));
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
        
        entity.output.put(detail, output);
    }
    
    private void xmlLoadEntityOutputOattr(MetaEntityOutput output,
                                          Element domOattr) {
        xmlAssertNodeName(domOattr, "oattr");
        xmlAssertHasAttribute(domOattr, "attr");
        
        String attr = domOattr.getAttribute("attr");
        
        if (domOattr.hasAttribute("detail")) {
            String detail = domOattr.getAttribute("detail");

            output.oattrs.put(attr, detail);
        } else
            output.oattrs.put(attr, "");
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
    
    private void validateSemantics() {
        if (leveldbPath == null)
            // TODO: handle this
            throw new RuntimeException();
        
        if (neo4jPath == null)
            // TODO: handle this
            throw new RuntimeException();
        
        for (MetaEntity entity : entities.values()) {
            for (MetaType attrType : entity.attrs.values())
                if (!attrType.isPrimitive() &&
                    !entities.containsKey(attrType.getType()))
                    // TODO: handle this (invalid attr)
                    throw new RuntimeException();
                
            for (Map.Entry<String, MetaEntityOutput> output :
                    entity.output.entrySet()) {
                if (!details.contains(output.getKey()))
                    // TODO: handle this
                    throw new RuntimeException();
                
                for (Map.Entry<String, String> oattr :
                        output.getValue().oattrs.entrySet()) {
                    String   attr     = oattr.getKey();
                    String   detail   = oattr.getValue();
                    MetaType attrType = entity.attrs.get(attr);
                    
                    if (attrType == null)
                        // TODO: handle this (oattr is with invlid attr)
                        throw new RuntimeException();
                    
                    if (attrType.isPrimitive()) {
                        if (detail != "")
                            // TODO: handle this (primitive with detail)
                            throw new RuntimeException();
                    } else if (!details.contains(detail))
                        // TODO: handle this (non primitive with invalid detail)
                        throw new RuntimeException();
                }
            }
        }
    }
    
}
