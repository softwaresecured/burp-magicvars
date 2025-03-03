package burp_magicvars.enums;

public enum MagicVariableType {
    STATIC,
    DYNAMIC;

    public static MagicVariableType byName(String name) {
        switch ( name.toUpperCase() ) {
            case "STATIC":
                return STATIC;
            case "DYNAMIC":
                return DYNAMIC;
        }
        return null;
    }

    public static String getPrettyName( MagicVariableType magicVariableType ) {
        switch ( magicVariableType ) {
            case STATIC:
                return "Static";
            case DYNAMIC:
                return "Dynamic";
        }
        return "";
    }
}
