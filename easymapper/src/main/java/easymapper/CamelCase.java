package easymapper;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

class CamelCase {

    public static String camelize(String s) {
        char head = s.charAt(0);
        return isUpperCase(head) ? toLowerCase(head) + s.substring(1) : s;
    }
}
