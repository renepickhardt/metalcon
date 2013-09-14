package de.metalcon.common;

public class JsonPrettyPrinter {
    
    final public static int tabWidth = 4;

    private static String strrepeat(String str, int times) {
        assert(times >= 0);
        
        String result = "";
        for (int i = 0; i != times; ++i) {
            result += str;
        }
        return result;
    }
    
    public static String prettyPrintJson(String json) {
        int indent = 0;
        boolean inString = false;
        boolean inEscape = false;
        
        String result = "";
        
        // We could change this method of iterating over string by using
        // reflection, this would improve performance for string longer than
        // 500 chars. See:
        // http://stackoverflow.com/questions/8894258/fastest-way-to-iterate-over-all-the-chars-in-a-string
        for (char c : json.toCharArray()) {
            switch (c) {
                case '}':
                case ']':
                    if (!inString) {
                        --indent;
                        result += "\n" + strrepeat(" ", indent * tabWidth);
                    }
                    break;
            }
            
            result += c;
            
            switch (c) {
                case '{':
                case '[':
                    if (!inString) {
                        ++indent;
                        result += "\n" + strrepeat(" ", indent * tabWidth);
                    }
                    break;
                   
                case ':':
                    if (!inString)
                        result += " ";
                    break;
                    
                case ',':
                    if (!inString)
                        result += "\n" + strrepeat(" ", indent * tabWidth);
                    break;
                    
                case '"':
                    if (!inEscape)
                        inString = !inString;
                    break;
            }
            
            if (inEscape)
                inEscape = false;
            else if (c == '\\')
                inEscape = true;
        }
        
        return result;
    }

}
