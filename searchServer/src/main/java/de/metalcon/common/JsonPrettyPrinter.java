package de.metalcon.common;

public class JsonPrettyPrinter {

    private static String strrepeat(String str, int times) {
        assert(times >= 0);
        
        String result = "";
        for (int i = 0; i != times; ++i) {
            result += str;
        }
        return result;
    }
    
    public static String prettyPrintJson(String json) {
        int tabWidth = 4;
        
        int     indent   = 0;
        boolean inString = false;
        
        String result = "";
        
        // We could change this method of iterating over string by using
        // reflection, this would improve performance for string longer than
        // 500 chars. See:
        // http://stackoverflow.com/questions/8894258/fastest-way-to-iterate-over-all-the-chars-in-a-string
        for (int i = 0; i != json.length(); ++i) {
            char c = json.charAt(i);
            
            switch (c) {
                case '}':
                case ']':
                    --indent;
                    result += "\n" + strrepeat(" ", indent * tabWidth);
                    break;
            }
            
            result += c;
            
            switch (c) {
                case '{':
                case '[':
                    ++indent;
                    result += "\n" + strrepeat(" ", indent * tabWidth);
                    break;
                    
                case ',':
                    result += "\n" + strrepeat(" ", indent * tabWidth);
                    break;
            }
        }
        
        return result;
    }
    
}
