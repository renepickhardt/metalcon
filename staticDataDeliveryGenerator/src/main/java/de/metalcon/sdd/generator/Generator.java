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
        Boolean hasClass = false;
        
        for (EntityAttribute attr : entity.getAttributes()) {
            
            hasArray = hasArray || attr.hasArray();
            hasClass = hasArray || (!attr.hasPrimitiveType() && !attr.hasArray());
            
            stEntity.addAggr("attrs.{name,type,primitive,array}",
                             attr.getName(), attr.getType(),
                             attr.hasPrimitiveType(), attr.hasArray());
        }
        
        stEntity.addAggr("has.{array,class}", hasArray, hasClass);
        
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

    /*private void javaWriteEntityOld(Entity entity) {
        String replaceClass =
                "public class " + entity.getName() + " extends Entity {\n";
        String replaceConstructor =
                in(1) + "public " + entity.getName() + "(Server server) {\n";

        String replaceAttributes = "";
        String replaceLoadFromJson = "";
        String replaceLoadFromCreateParams = "";
        String replaceLoadFromUpdateParams = "";
        String replaceGenerateJson = "";
        
        List<EntityAttribute> attrs = entity.getAttributes();
        
        for (EntityAttribute attr : attrs) {
            String name = attr.getName();
            String type = attr.getType();
            
            if (!attr.hasArray())
                replaceAttributes +=
                        in(1) + type + " " + name + ";\n";
            else
                replaceAttributes +=
                        in(1) + "List<" + type + "> " + name + ";\n";
            
            if (attr.hasPrimitiveType()) {
                replaceLoadFromJson +=
                        in(2) + name + " = entity.get(\"" + name + "\");\n";
                replaceLoadFromCreateParams +=
                        in(2) + name + " = getParam(params, \"" + name + "\");\n";
            } else if (attr.hasArray()) {
                replaceLoadFromJson +=
                        in(2) + name + " = new LinkedList<" + type + ">();\n" +
                        in(2) + "for (String id : entity.get(\"" + name + "\").split(\",\")) {\n" +
                        in(2) + "    " + type + " o = new " + type + "(server);\n" +
                        in(2) + "    o.loadFromId(new Muid(id));\n" +
                        in(2) + "    " + name + ".add(o);\n" +
                        in(2) + "}\n";
                replaceLoadFromCreateParams +=
                        in(2) + name + " = new LinkedList<" + type + ">();\n" +
                        in(2) + "for (String id : getParam(params, \"" + name + "\").split(\",\")) {\n" +
                        in(2) + "    " + type + " o = new " + type + "(server);\n" +
                        in(2) + "    o.loadFromId(new Muid(id));\n" +
                        in(2) + "    " + name + ".add(o);\n" +
                        in(2) + "}\n";
            } else {
                replaceLoadFromJson +=
                        in(2) + "oid = entity.get(\"" + name + "\");\n" +
                        in(2) + "if (oid == null)\n" +
                        in(2) + "    " + name + " = null;\n" +
                        in(2) + "else {\n" +
                        in(2) + "    " + name + " = new " + type + "(server);\n" +
                        in(2) + "    " + name + ".loadFromId(new Muid(oid));\n" +
                        in(2) + "};\n";
                replaceLoadFromCreateParams +=
                        in(2) + "oid = getParam(params, \"" + name + "\", true);\n" +
                        in(2) + "if (oid == null)\n" +
                        in(2) + "    " + name + " = null;\n" +
                        in(2) + "else {\n" +
                        in(2) + "    " + name + " = new " + type + "(server);\n" +
                        in(2) + "    " + name + ".loadFromId(new Muid(oid));\n" +
                        in(2) + "};\n";
            }
            replaceLoadFromJson += "\n";
            replaceLoadFromCreateParams += "\n";
        }
            
        replaceGenerateJson +=
                in(2) + "// FULL\n" +
                in(2) + "j = new LinkedHashMap<String, Object>();\n" +
                in(2) + "j.put(\"id\", getId().toString()                );\n";
        for (EntityAttribute attr : attrs) {
            String name = attr.getName();
            String type = attr.getType();
            
            if (attr.hasPrimitiveType())
                replaceGenerateJson +=
                        in(2) + "j.put(\"" + name + "\", " + name + ");\n";
            else if (attr.hasArray())
                replaceGenerateJson +=
                        in(2) + "ids = new LinkedList<Muid>();\n" +
                        in(2) + "for (" + type + " o : " + name + ")\n" +
                        in(2) + "    ids.add(o.getId());\n" +
                        in(2) + "j.put(\"" + name + "\", joinIds(ids));\n";
            else
                replaceGenerateJson +=
                        in(2) + "if (" + name + " == null)\n" +
                        in(2) + "    j.put(\"" + name + "\", null);\n" +
                        in(2) + "else\n" +
                        in(2) + "    j.put(\"" + name + "\", " + name + ".getId().toString());\n";
        }
        replaceGenerateJson +=
                in(2) + "json.put(Detail.FULL, JSONValue.toJSONString(j));\n";
        
        for (Detail detail : details) {
            String constname = Detail.toConstname(detail.getName());
            
            replaceGenerateJson +=
                    "\n" +
                    in(2) + "// " + constname + "\n" +
                    in(2) + "j = new LinkedHashMap<String, Object>();\n" +
                    in(2) + "j.put(\"id\", getId().toString());\n";
            
            EntityOutput output = entity.getOutput(detail);
            if (output != null) {
                //Map<String, String> oattrs = output.getOattrs();
                Map<String, String> oattrs = null;
                for (EntityAttribute attr : attrs) {
                    String name = attr.getName();
                    String type = attr.getType();
                    
                    String odetail;
                    if ((odetail = oattrs.get(attr.getName())) != null) {
                        String odetailconst = Detail.toConstname(odetail);
                        
                        if (attr.hasPrimitiveType())
                            replaceGenerateJson +=
                                    in(2) + "j.put(\"" + name + "\", " + name + ");\n";
                        else if (attr.hasArray())
                            replaceGenerateJson +=
                                    in(2) + "os = new LinkedList<JsonString>();\n" +
                                    in(2) + "for (" + type + " o : " + name + ")\n" +
                                    in(2) + "    os.add(new JsonString(o.getJson(Detail." + odetailconst + ")));\n" +
                                    in(2) + "j.put(\"" + name + "\", os);\n";
                        else
                            replaceGenerateJson +=
                                    in(2) + "if (" + name + " == null)\n" +
                                    in(2) + "    j.put(\"" + name + "\", null);\n" +
                                    in(2) + "else\n" +
                                    in(2) + "    j.put(\"" + name + "\", new JsonString(" + name + ".getJson(Detail." + odetailconst + ")));\n";
                    }
                }
            }
            
            replaceGenerateJson +=
                    in(2) + "json.put(Detail." + constname + ", JSONValue.toJSONString(j));\n";
        }
        
        Map<String, String> replace = new HashMap<String, String>();
        replace.put("CLASS", replaceClass);
        replace.put("ATTRIBUTES", replaceAttributes);
        replace.put("CONSTRUCTOR", replaceConstructor);
        replace.put("LOAD_FROM_JSON", replaceLoadFromJson);
        replace.put("LOAD_FROM_CREATE_PARAMS", replaceLoadFromCreateParams);
        replace.put("LOAD_FROM_UPDATE_PARAMS", replaceLoadFromUpdateParams);
        replace.put("GENERATE_JSON", replaceGenerateJson);
        Template.replace("EntityTemplate.java",
                         "entity/" + entity.getName() + ".java",
                         replace);
    }*/
    
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
