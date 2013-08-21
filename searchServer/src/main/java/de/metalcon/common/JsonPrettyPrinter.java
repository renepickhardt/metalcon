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
        int     indent   = 0;
        boolean inString = false;
        
        String result = "";
        
        // We could change this method of iterating over string by using
        // reflection, this would improve performance for string longer than
        // 500 chars. See:
        // http://stackoverflow.com/questions/8894258/fastest-way-to-iterate-over-all-the-chars-in-a-string
        int i = 0;
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
                    
                case ',':
                    if (!inString)
                        result += "\n" + strrepeat(" ", indent * tabWidth);
                    break;
                    
                case '"':
                    if (i == 0 || json.charAt(i - 1) != '\\')
                        // actually we want to check (i > 0) but since we know
                        // i starts at 0, we can check (i != 0) which is faster.
                        inString = !inString;
                    break;
            }
            
            ++i;
        }
        
        return result;
    }
    
}
