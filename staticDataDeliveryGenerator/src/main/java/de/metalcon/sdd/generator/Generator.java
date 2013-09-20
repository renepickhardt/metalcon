package de.metalcon.sdd.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Generator {

    private List<Detail> details;

    private List<Entity> entities;

    public Generator() {
        details = new LinkedList<Detail>();
        details.add(new Detail("full"));
        entities = new LinkedList<Entity>();

        xmlLoad();
        javaWrite();
    }

    private void xmlLoad() {
        File file = new File("input/meta.xml");

        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(file);
        } catch (IOException e) {
            // TODO: handle this
            throw new RuntimeException();
        } catch (SAXException e) {
            // TODO: handle this
            throw new RuntimeException();
        } catch (ParserConfigurationException e) {
            // TODO: handle this
            throw new RuntimeException();
        }

        doc.normalize();
        
        Element xmlRoot = doc.getDocumentElement();
        if (!xmlRoot.getNodeName().equals("sdd-meta")) {
            // TODO: handle this
            throw new RuntimeException();
        }

        Element xmlDetails = (Element) xmlRoot.getElementsByTagName("details")
                .item(0);
        xmlLoadDetails(xmlDetails);

        Element xmlEntities = (Element) xmlRoot
                .getElementsByTagName("entities").item(0);
        xmlLoadEntities(xmlEntities);
    }

    private void xmlLoadDetails(Element xmlDetails) {
        NodeList xmlDetailsChilds = xmlDetails.getChildNodes();
        for (int i = 0; i != xmlDetailsChilds.getLength(); ++i) {
            Node node = xmlDetailsChilds.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element xmlDetail = (Element) node;
                if (!xmlDetail.getNodeName().equals("detail")) {
                    // TODO: handle this
                    throw new RuntimeException();
                }
                
                String name = xmlDetail.getAttribute("name");
                details.add(new Detail(name));
            }
        }
    }

    private void xmlLoadEntities(Element xmlEntities) {
        NodeList xmlEntitiesChilds = xmlEntities.getChildNodes();
        for (int i = 0; i != xmlEntitiesChilds.getLength(); ++i) {
            Node node = xmlEntitiesChilds.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element xmlEntity = (Element) node;
                if (!xmlEntity.getNodeName().equals("entity")) {
                    // TODO: handle this
                    throw new RuntimeException();
                }

                String name = xmlEntity.getAttribute("name");
                Entity entity = new Entity(name);

                NodeList xmlAttrs = xmlEntity
                        .getElementsByTagName("attr");
                for (int j = 0; j != xmlAttrs.getLength(); ++j) {
                    Element xmlAttr = (Element) xmlAttrs.item(j);
                    xmlLoadEntityAttr(entity, xmlAttr);
                }

                NodeList xmlOutputs = xmlEntity
                        .getElementsByTagName("output");
                for (int j = 0; j != xmlOutputs.getLength(); ++j) {
                    Element xmlOutput = (Element) xmlOutputs.item(j);
                    xmlLoadEntityOutput(entity, xmlOutput);
                }

                entities.add(entity);
            }
        }
    }

    private void xmlLoadEntityAttr(Entity entity, Element xmlAttr) {
        String name = xmlAttr.getAttribute("name");
        String type = xmlAttr.getAttribute("type");
        entity.addAttribute(new EntityAttribute(name, type));
    }

    private void xmlLoadEntityOutput(Entity entity, Element xmlOutput) {
        EntityOutput output = new EntityOutput();

        NodeList xmlOattrs = xmlOutput.getChildNodes();
        for (int i = 0; i != xmlOattrs.getLength(); ++i) {
            Node node = xmlOattrs.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element xmlOattr = (Element) node;
                if (!xmlOattr.getNodeName().equals("oattr")) {
                    // TODO: handle this
                    throw new RuntimeException();
                }

                String oattrName   = xmlOattr.getAttribute("name");
                String oattrDetail = xmlOattr.getAttribute("detail");
                
                EntityAttribute attr = null;
                for (EntityAttribute a : entity.getAttributes()) {
                    if (a.getName().equals(oattrName)) {
                        attr = a;
                        break;
                    }
                }
                if (attr == null) {
                    // TODO: handle this
                    throw new RuntimeException();
                }
                
                Detail detail = null;
                if (!oattrDetail.isEmpty()) {
                    for (Detail d : details) {
                        if (d.getName().equals(oattrDetail)) {
                            detail = d;
                            break;
                        }
                    }
                    if (detail == null) {
                        // TODO: handle this
                        throw new RuntimeException();
                    }
                }
                
                output.addOattrs(attr, detail);
            }
        }
        
        String detailName = xmlOutput.getAttribute("detail");
        Detail detail = null;
        for (Detail d : details) {
            if (d.getName().equals(detailName)) {
                detail = d;
                break;
            }
        }
        if (detail == null) {
            // TODO: handle this
            throw new RuntimeException();
        }
        
        entity.addOutput(detail, output);
    }

    private void javaWrite() {
        new File("output").mkdir();
        new File("output/entity").mkdir();
        
        javaWriteDetail();
        for (Entity entity : entities)
            javaWriteEntity(entity);
        javaWriteEntityByType();
    }
    
    private void javaWriteDetail() {
        ST stDetail = new STGroupFile("template/Detail.stg")
                .getInstanceOf("detail");
        for (Detail d : details)
            stDetail.addAggr("lits.{name,constname}",
                           d.getName(), d.getConstname());
        
        writeStringToFile("output/Detail.java", stDetail.render());
    }
    
    private void javaWriteEntity(Entity entity) {
        STGroup stgEntity = new STGroupFile("template/Entity.stg");
        ST stEntity = stgEntity.getInstanceOf("entity");
        stEntity.add("name", entity.getName());
        
        Boolean hasArray = false;
        
        for (EntityAttribute attr : entity.getAttributes()) {
            hasArray = hasArray || attr.hasArray();
            
            stEntity.addAggr("attrs.{name,type,primitive,array}",
                             attr.getName(), attr.getType(),
                             attr.hasPrimitiveType(), attr.hasArray());
        }
        
        stEntity.add("hasArray", hasArray);
        
        ST stGenerate;
        for (Detail detail : details) {
            stGenerate = stgEntity.getInstanceOf("generate");
            stGenerate.add("detail", detail.getConstname());
            
            if (detail.getName().equals("full")) {
                stGenerate.add("full", true);
                for (EntityAttribute attr : entity.getAttributes())
                    stGenerate.addAggr(
                            "oattrs.{name,type,primitive,array,detail}",
                            attr.getName(), attr.getType(),
                            attr.hasPrimitiveType(), attr.hasArray(),
                            "");
            } else {
                EntityOutput output = entity.getOutput(detail);
                if (output != null) {
                    for (Map.Entry<EntityAttribute, Detail> oattr :
                            output.getOattrs().entrySet()) {
                        EntityAttribute attr = oattr.getKey();
                        String dtl = "";
                        if (oattr.getValue() != null)
                            dtl = oattr.getValue().getConstname();
                        
                        stGenerate.addAggr(
                                "oattrs.{name,type,primitive,array,detail}",
                                attr.getName(), attr.getType(),
                                attr.hasPrimitiveType(), attr.hasArray(),
                                dtl);
                    }
                }
            }
            stEntity.add("jsons", stGenerate.render());
        }
        
        writeStringToFile("output/entity/" + entity.getName() + ".java",
                          stEntity.render());
    }
    
    private void javaWriteEntityByType() {
        ST stEntityByType = new STGroupFile("template/EntityByType.stg")
                .getInstanceOf("entityByType");
        for (Entity entity: entities)
            stEntityByType.add("entities", entity.getName());
        
        writeStringToFile("output/entity/EntityByType.java",
                          stEntityByType.render());
    }

    private static void writeStringToFile(String file, String output) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
            out.println(output);
        } catch(FileNotFoundException e) {
            // TODO: handle this
            throw new RuntimeException();
        } finally {
            if (out != null)
                out.close();
        }
    }

    public static void main(String[] args) {
        new Generator();
    }

}
