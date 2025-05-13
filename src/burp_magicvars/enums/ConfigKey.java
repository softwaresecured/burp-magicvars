package burp_magicvars.enums;

public enum ConfigKey {
    LEFT_TEMPLATE_STRING,
    RIGHT_TEMPLATE_STRING,
    CUSTOM_VARIABLES,
    ENABLED_SOURCES;

    public static final String KEY_PREFIX = "BurpMagicVars";

    public String resolve() {
        return KEY_PREFIX + name();
    }
}
