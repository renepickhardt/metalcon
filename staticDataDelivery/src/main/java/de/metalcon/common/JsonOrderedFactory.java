package de.metalcon.common;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;

public class JsonOrderedFactory implements ContainerFactory {

    @SuppressWarnings("rawtypes")
    @Override
    public Map createObjectContainer() {
        return new LinkedHashMap();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List creatArrayContainer() {
        return new LinkedList();
    }

}
