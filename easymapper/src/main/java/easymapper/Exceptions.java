package easymapper;

import static java.lang.System.lineSeparator;

final class Exceptions {
    public static IllegalArgumentException argumentNullException(String paramName) {
        String message = "Value cannot be null." + lineSeparator() + "Parameter name: " + paramName;
        return new IllegalArgumentException(message);
    }
}
