package burp_magicvars;

import burp_magicvars.enums.MagicVariableType;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public class MagicVariable {
    // Common settings
    public String id = null;
    public String name = "";
    public String description = "";
    public boolean enabled = true;
    public MagicVariableType magicVariableType = null;
    public String initialValue = "";
    public Pattern pathScopeRegex = null;
    public int order = 0;

    // Dynamic specific settings
    public Pattern readRegex = null;
    public Pattern writeRegex = null;
    public int readCaptureGroup = 0;
    public int writeCaptureGroup = 0;
    public String currentValue = "";
    public Instant lastUpdated = null;

    public MagicVariable() {

    }

    public MagicVariable( String name, MagicVariableType magicVariableType) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.magicVariableType = magicVariableType;
    }

    public MagicVariable(String id, String name, String description, MagicVariableType magicVariableType, String initialValue, Pattern pathScopeRegex, boolean enabled, Pattern readRegex, Pattern writeRegex, int readCaptureGroup, int writeCaptureGroup, String currentValue, Instant lastUpdated, int order) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.magicVariableType = magicVariableType;
        this.initialValue = initialValue;
        this.pathScopeRegex = pathScopeRegex;
        this.enabled = enabled;
        this.readRegex = readRegex;
        this.writeRegex = writeRegex;
        this.readCaptureGroup = readCaptureGroup;
        this.writeCaptureGroup = writeCaptureGroup;
        this.currentValue = currentValue;
        this.lastUpdated = lastUpdated;
        this.order = order;
    }

    @Override
    public String toString() {
        return "MagicVariable{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", magicVariableType=" + magicVariableType +
                ", initialValue='" + initialValue + '\'' +
                ", pathScopeRegex=" + pathScopeRegex +
                ", order=" + order +
                ", enabled=" + enabled +
                ", readRegex=" + readRegex +
                ", writeRegex=" + writeRegex +
                ", readCaptureGroup=" + readCaptureGroup +
                ", writeCaptureGroup=" + writeCaptureGroup +
                ", currentValue='" + currentValue + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
