package burp_magicvars.util;

import burp.api.montoya.collaborator.Collaborator;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp_magicvars.MagicVariable;
import burp_magicvars.enums.MagicVariableType;
import burp_magicvars.event.MagicVarsReplacementEvent;

import java.time.Instant;
import java.util.ArrayList;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;

public class MagicVarsReplacer {
    private Collaborator collaborator;
    private SwingPropertyChangeSupport eventEmitter = new SwingPropertyChangeSupport(this);
    private String leftVariableMarker = "__";
    private String rightVariableMarker = "__";

    public MagicVarsReplacer( Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public String getLeftVariableMarker() {
        return leftVariableMarker;
    }

    public void setLeftVariableMarker(String leftVariableMarker) {
        this.leftVariableMarker = leftVariableMarker;
    }

    public String getRightVariableMarker() {
        return rightVariableMarker;
    }

    public void setRightVariableMarker(String rightVariableMarker) {
        this.rightVariableMarker = rightVariableMarker;
    }

    public String getFName() {
        Random r = new Random();
        return ReplacementConstants.PERSON_FIRST_NAMES[r.nextInt(ReplacementConstants.PERSON_FIRST_NAMES.length)];
    }

    public String getLName() {
        Random r = new Random();
        return ReplacementConstants.PERSON_LAST_NAMES[r.nextInt(ReplacementConstants.PERSON_LAST_NAMES.length)];
    }

    public String getLoremIpsum() {
        Random r = new Random();
        return ReplacementConstants.LOREM_IPSUM_WORDS[r.nextInt(ReplacementConstants.LOREM_IPSUM_WORDS.length)];
    }

    public String getLoremIpsumSentence() {
        Random r = new Random();
        int sentenceLength = r.nextInt(10 - 5 + 1) + 5;
        boolean hasComma = r.nextBoolean();
        int commaPosition = r.nextInt(sentenceLength-2 - 2 + 1) + 2;
        ArrayList<String> sentence = new ArrayList<>();
        while ( sentence.size() < sentenceLength ) {
            String word = getLoremIpsum();
            if ( sentence.size() == 0 ) {
                word = word.substring(0,1).toUpperCase() + word.substring(1);
            }

            if ( sentence.size() == commaPosition && hasComma) {
                word += ",";
            }

            if ( sentence.size() == sentenceLength-1) {
                word += ".";
            }

            if ( !sentence.contains(word)) {
                sentence.add(word);
            }
        }
        return String.join(" ", sentence);
    }

    public String getRandStr() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String randCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for ( int i = 0; i < 8; i++ ) {
            sb.append(randCharSet.charAt(random.nextInt(randCharSet.length()-1)));
        }
        return sb.toString();
    }

    public String getNeRandStr() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String randCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        sb.append("XX");
        for ( int i = 0; i < 4; i++ ) {
            sb.append(randCharSet.charAt(random.nextInt(randCharSet.length()-1)));
        }
        sb.append("YY");
        return sb.toString();
    }

    public String getRint() {
        Random random = new Random();
        int rint = 10000000 + random.nextInt(89999999);
        return String.format("%d", rint);
    }

    public String getNeRint() {
        Random random = new Random();
        int rint = 1000 + random.nextInt(8999);
        return String.format("55%d66", rint);
    }

    public String getNeUUID() {
        String newUuid = UUID.randomUUID().toString();
        String uuid = newUuid.substring(0,11) + "de-adbe-ef" + newUuid.substring(21);
        return uuid;
    }

    public String getUnixTimestamp() {
        return String.format("%d", System.currentTimeMillis() / 1000L);
    }

    private String prepareVariableName( String variableName ) {
        return String.format("%s%s%s", getLeftVariableMarker(),variableName,getRightVariableMarker());
    }

    public String processDynamicVariables(ArrayList<MagicVariable> customMagicVariables, String data, ParameterEncoder parameterEncoder ) {
        if ( customMagicVariables != null ) {
            for ( MagicVariable magicVariable : customMagicVariables ) {
                if ( magicVariable.magicVariableType.equals(MagicVariableType.DYNAMIC)) {
                    if ( !magicVariable.enabled ) {
                        continue;
                    }
                    if ( magicVariable.readRegex != null && magicVariable.writeRegex != null ) {
                        // Read
                        try {
                            Matcher m = magicVariable.readRegex.matcher(data);
                            if ( m.find() ) {
                                String value = m.group(magicVariable.readCaptureGroup);
                                if ( value != null ) {
                                    magicVariable.currentValue = value;
                                    magicVariable.lastUpdated = Instant.now();
                                    emit(MagicVarsReplacementEvent.VALUE_READ,null,magicVariable.name);
                                }
                            }
                        } catch ( Exception e ) {
                            e.printStackTrace();
                            emit(
                                    MagicVarsReplacementEvent.PROCESSING_ERROR,
                                    null,
                                    String.format(
                                            "Error processing variable %s - %s",
                                            magicVariable.name,
                                            e.getMessage()
                                    )
                            );
                        }
                        // Write
                        if ( magicVariable.currentValue != null ) {
                            try {
                                Matcher m = magicVariable.writeRegex.matcher(data);
                                if ( m.find() ) {
                                    String value = m.group(magicVariable.writeCaptureGroup);
                                    if ( value != null ) {
                                        String newValue = String.format("%s%s%s",
                                                data.substring(0,m.start(magicVariable.writeCaptureGroup)),
                                                parameterEncoder.encodeParameter(magicVariable.currentValue),
                                                data.substring(m.end(magicVariable.writeCaptureGroup),data.length())
                                        );
                                        data = newValue;
                                        emit(MagicVarsReplacementEvent.VALUE_WRITE,null,magicVariable.name);
                                    }
                                }
                            } catch ( Exception e ) {
                                e.printStackTrace();
                                emit(
                                        MagicVarsReplacementEvent.PROCESSING_ERROR,
                                        null,
                                        String.format(
                                                "Error processing variable %s - %s",
                                                magicVariable.name,
                                                e.getMessage()
                                        )
                                );
                            }
                        }
                    }
                }
            }
        }
        return data;
    }

    public String processStaticVariables(ArrayList<MagicVariable> customMagicVariables, String param, ParameterEncoder parameterEncoder ) {
        if ( param != null ) {
            // Process custom
            if ( customMagicVariables != null ) {
                for ( MagicVariable magicVariable : customMagicVariables ) {
                    if ( magicVariable.magicVariableType.equals(MagicVariableType.STATIC)) {
                        if ( !magicVariable.enabled ) {
                            continue;
                        }
                        param = emitIfChanged(
                                prepareVariableName(magicVariable.name),
                                param,
                                param.replaceAll("(?i)%s".formatted(prepareVariableName(magicVariable.name)),
                                        parameterEncoder.encodeParameter(magicVariable.initialValue))
                        );
                    }
                }
            }

            // Process builtin

            param = emitIfChanged(prepareVariableName("RINT"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("RINT")), parameterEncoder.encodeParameter(getRint())));
            param = emitIfChanged(prepareVariableName("FNAME"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("FNAME")), parameterEncoder.encodeParameter(getFName())));
            param = emitIfChanged(prepareVariableName("LNAME"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("LNAME")), parameterEncoder.encodeParameter(getLName())));
            param = emitIfChanged(prepareVariableName("LOREMIPSUM"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("LOREMIPSUM")), parameterEncoder.encodeParameter(getLoremIpsum())));
            param = emitIfChanged(prepareVariableName("LOREMIPSUMSENTENCE"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("LOREMIPSUMSENTENCE")), parameterEncoder.encodeParameter(getLoremIpsumSentence())));
            param = emitIfChanged(prepareVariableName("NERINT"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("NERINT")), parameterEncoder.encodeParameter(getNeRint())));
            param = emitIfChanged(prepareVariableName("NERINT"), param, param.replaceAll("(?i)55\\d{4}66", parameterEncoder.encodeParameter(getNeRint())));
            param = emitIfChanged(prepareVariableName("RANDSTR"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("RANDSTR")), parameterEncoder.encodeParameter(getRandStr())));
            param = emitIfChanged(prepareVariableName("NERANDSTR"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("NERANDSTR")), parameterEncoder.encodeParameter(getNeRandStr())));
            param = emitIfChanged(prepareVariableName("NERANDSTR"), param, param.replaceAll("(?i)(?i)XX[a-z]{4}YY", parameterEncoder.encodeParameter(getNeRandStr())));
            param = emitIfChanged(prepareVariableName("UUID"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("UUID")), parameterEncoder.encodeParameter(UUID.randomUUID().toString())));
            param = emitIfChanged(prepareVariableName("NEUUID"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("NEUUID")), parameterEncoder.encodeParameter(getNeUUID())));
            param = emitIfChanged(prepareVariableName("NEUUID"), param, param.replaceAll("(?i)(?i)[a-f0-9]{8}-[a-f0-9]{2}de-adbe-ef[a-f0-9]{2}-[a-f0-9]{12}", parameterEncoder.encodeParameter(getNeUUID())));
            param = emitIfChanged(prepareVariableName("TIMESTAMP"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("TIMESTAMP")), parameterEncoder.encodeParameter(getUnixTimestamp())));
            if ( collaborator != null ) {
                param = emitIfChanged(prepareVariableName("OOB"), param, param.replaceAll("(?i)%s".formatted(prepareVariableName("OOB")), parameterEncoder.encodeParameter(collaborator.defaultPayloadGenerator().generatePayload().toString())));
            }
        }

        return param;
    }

    private String emitIfChanged(String variableName, String prev, String next ) {
        if ( prev != next ) {
            emit(MagicVarsReplacementEvent.REPLACEMENT_MADE,null,variableName);
        }
        return next;
    }

    public void addListener(PropertyChangeListener listener) {
        this.eventEmitter.addPropertyChangeListener(listener);
    }

    public void emit(MagicVarsReplacementEvent event, Object old, Object value) {
        eventEmitter.firePropertyChange(event.name(), old, value);
    }
}
