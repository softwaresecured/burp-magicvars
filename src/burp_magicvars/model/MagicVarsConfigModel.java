package burp_magicvars.model;

import burp_magicvars.MagicVariable;
import burp_magicvars.config.MagicVariableListExport;
import burp_magicvars.enums.ConfigKey;
import burp_magicvars.enums.EditorState;
import burp_magicvars.enums.MagicVariableType;
import burp_magicvars.config.AbstractConfig;
import burp_magicvars.event.MagicVarsConfigModelEvent;
import burp_magicvars.mvc.AbstractModel;
import burp_magicvars.util.Logger;
import burp_magicvars.util.RegexUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

public class MagicVarsConfigModel extends AbstractModel<MagicVarsConfigModelEvent> {
    public static final String DEFAULT_LEFT_MARKER = "__";
    public static final String DEFAULT_RIGHT_MARKER = "__";

    // Editor settigns
    private EditorState editorState = EditorState.INITIAL;
    private String lastVariableId = null;
    private int currentSelectedIdx = -1;

    // Common settings
    private String currentVariableId = null;
    private String currentVariableName = "";
    private String currentVariableDescription = "";
    private MagicVariableType currentVariableMagicVariableType = null;
    private String currentVariableInitialValue = "";
    private String currentVariablePathScopeRegex = "";
    // Dynamic specific settings
    private boolean currentVariableEnabled = false;
    private String currentVariableReadRegex = "";
    private String currentVariableWriteRegex = "";
    private int currentVariableReadCaptureGroup = 0;
    private int currentVariableWriteCaptureGroup = 0;

    private ArrayList<MagicVariable> magicVariables = new ArrayList<MagicVariable>();
    private String leftVariableMarker = null;
    private String rightVariableMarker = null;


    private final DefaultTableModel customVariablesModel;
    public MagicVarsConfigModel() {
        super();
        this.customVariablesModel = new DefaultTableModel();
        for (String col : new String[] {
                "ID",
                "Enabled",
                "Name",
                "Type",
                "Path scope regex",
                "Read regex",
                "Write regex",
                "Initial value",
                "Current value",
                "Last updated",
                "Description"}
        ) {
            this.customVariablesModel.addColumn(col);
        }

    }
    @Override
    public void load(AbstractConfig config) {
        setLeftVariableMarker(config.getString(ConfigKey.LEFT_TEMPLATE_STRING, DEFAULT_LEFT_MARKER));
        setRightVariableMarker(config.getString(ConfigKey.RIGHT_TEMPLATE_STRING, DEFAULT_RIGHT_MARKER));
        try {
            if ( config.getString(ConfigKey.CUSTOM_VARIABLES) != null && config.getString(ConfigKey.CUSTOM_VARIABLES).length() > 0 ) {
                importVariablesFromJSON(config.getString(ConfigKey.CUSTOM_VARIABLES));
            }
        } catch (JsonProcessingException e) {
            Logger.log("DEBUG", String.format("Error while importing variables: %s", e.getMessage()));
        }
    }

    @Override
    public void save(AbstractConfig config) {
        config.setString(ConfigKey.LEFT_TEMPLATE_STRING, getLeftVariableMarker());
        config.setString(ConfigKey.RIGHT_TEMPLATE_STRING, getRightVariableMarker());
        try {
            config.setString(ConfigKey.CUSTOM_VARIABLES,exportVariablesAsJSON());
        } catch (JsonProcessingException e) {
            Logger.log("DEBUG", String.format("Error while exporting variables: %s", e.getMessage()));
        }
    }

    public String exportVariablesAsJSON() throws JsonProcessingException {
        MagicVariableListExport exportDataObject = exportVariables();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(exportDataObject);
    }

    public void importVariablesFromJSON( String jsonStr ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MagicVariableListExport variableListImport = mapper.readValue(new String(jsonStr), MagicVariableListExport.class);
        if ( variableListImport.variables != null ) {
            for (MagicVariable magicVariable : variableListImport.variables ) {
                addVariable(magicVariable);
            }
        }
    }

    public void clearCurrentVariable() {
        // Common settings
        currentVariableName = "";
        currentVariableDescription = "";
        currentVariableMagicVariableType = null;
        currentVariableInitialValue = "";
        currentVariablePathScopeRegex = "";
        // Dynamic specific settings
        currentVariableEnabled = false;
        currentVariableReadRegex = "";
        currentVariableWriteRegex = "";
        currentVariableReadCaptureGroup = 1;
        currentVariableWriteCaptureGroup = 1;
        setCurrentVariableId(null);
    }

    public String getCurrentVariableId() {
        return currentVariableId;
    }

    public void setCurrentVariableId(String currentVariableId) {
        var old = this.currentVariableId;
        this.currentVariableId = currentVariableId;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_ID_UPDATED, old, currentVariableId);
    }

    public String getCurrentVariableName() {
        return currentVariableName;
    }

    public void setCurrentVariableName(String currentVariableName) {
        var old = this.currentVariableName;
        this.currentVariableName = currentVariableName;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_NAME_UPDATED, old, currentVariableName);
    }

    public String getCurrentVariableDescription() {
        return currentVariableDescription;
    }

    public void setCurrentVariableDescription(String currentVariableDescription) {
        var old = this.currentVariableDescription;
        this.currentVariableDescription = currentVariableDescription;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_DESCRIPTION_UPDATED, old, currentVariableDescription);
    }

    public MagicVariableType getCurrentVariableMagicVariableType() {
        return currentVariableMagicVariableType;
    }

    public void setCurrentVariableMagicVariableType(MagicVariableType currentVariableMagicVariableType) {
        var old = this.currentVariableMagicVariableType;
        this.currentVariableMagicVariableType = currentVariableMagicVariableType;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_TYPE_UPDATED, old, currentVariableMagicVariableType);
    }

    public String getCurrentVariableInitialValue() {
        return currentVariableInitialValue;
    }

    public void setCurrentVariableInitialValue(String currentVariableInitialValue) {
        var old = this.currentVariableInitialValue;
        this.currentVariableInitialValue = currentVariableInitialValue;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_INITIAL_VALUE_UPDATED, old, currentVariableInitialValue);
    }

    public String getCurrentVariablePathScopeRegex() {
        return currentVariablePathScopeRegex;
    }

    public void setCurrentVariablePathScopeRegex(String currentVariablePathScopeRegex) {
        var old = this.currentVariablePathScopeRegex;
        this.currentVariablePathScopeRegex = currentVariablePathScopeRegex;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_PATH_SCOPE_UPDATED, old, currentVariablePathScopeRegex);
    }

    public boolean isCurrentVariableEnabled() {
        return currentVariableEnabled;
    }

    public void setCurrentVariableEnabled(boolean currentVariableEnabled) {
        var old = this.currentVariableEnabled;
        this.currentVariableEnabled = currentVariableEnabled;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_ENABLED_TOGGLE, old, currentVariableEnabled);
    }

    public String getCurrentVariableReadRegex() {
        return currentVariableReadRegex;
    }

    public void setCurrentVariableReadRegex(String currentVariableReadRegex) {
        var old = this.currentVariableReadRegex;
        this.currentVariableReadRegex = currentVariableReadRegex;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_READ_REGEX_UPDATED, old, currentVariableReadRegex);
    }

    public String getCurrentVariableWriteRegex() {
        return currentVariableWriteRegex;
    }

    public void setCurrentVariableWriteRegex(String currentVariableWriteRegex) {
        var old = this.currentVariableWriteRegex;
        this.currentVariableWriteRegex = currentVariableWriteRegex;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_WRITE_REGEX_UPDATED, old, currentVariableWriteRegex);
    }

    public int getCurrentVariableReadCaptureGroup() {
        return currentVariableReadCaptureGroup;
    }

    public void setCurrentVariableReadCaptureGroup(int currentVariableReadCaptureGroup) {
        var old = this.currentVariableReadCaptureGroup;
        this.currentVariableReadCaptureGroup = currentVariableReadCaptureGroup;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_READ_CAPTURE_GROUP_UPDATED, old, currentVariableReadCaptureGroup);
    }

    public int getCurrentVariableWriteCaptureGroup() {
        return currentVariableWriteCaptureGroup;
    }

    public void setCurrentVariableWriteCaptureGroup(int currentVariableWriteCaptureGroup) {
        var old = this.currentVariableWriteCaptureGroup;
        this.currentVariableWriteCaptureGroup = currentVariableWriteCaptureGroup;
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_WRITE_CAPTURE_GROUP_UPDATED, old, currentVariableWriteCaptureGroup);
    }

    public String getLeftVariableMarker() {
        return leftVariableMarker;
    }

    public void setLeftVariableMarker(String leftVariableMarker) {
        var old = this.leftVariableMarker;
        this.leftVariableMarker = leftVariableMarker;
        emit(MagicVarsConfigModelEvent.VARIABLE_LEFT_VARIABLE_MARKER_UPDATED, old, leftVariableMarker);
        Logger.log("DEBUG", String.format("Model emitted %s event", MagicVarsConfigModelEvent.VARIABLE_LEFT_VARIABLE_MARKER_UPDATED.toString()));
    }

    public String getRightVariableMarker() {
        return rightVariableMarker;
    }

    public void setRightVariableMarker(String rightVariableMarker) {
        var old = this.rightVariableMarker;
        this.rightVariableMarker = rightVariableMarker;
        emit(MagicVarsConfigModelEvent.VARIABLE_RIGHT_VARIABLE_MARKER_UPDATED, old, rightVariableMarker);
    }

    public EditorState getEditorState() {
        return editorState;
    }

    public void setEditorState(EditorState editorState) {
        var old = this.editorState;
        this.editorState = editorState;
        emit(MagicVarsConfigModelEvent.EDITOR_STATE_CHANGED, old, editorState);
    }

    private boolean nameIsUnique(String name ) {
        for ( MagicVariable magicVariable : magicVariables ) {
            if ( magicVariable.name.equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    public String getLastVariableId() {
        return lastVariableId;
    }

    public void setLastVariableId(String lastVariableId) {
        var old = this.lastVariableId;
        this.lastVariableId = lastVariableId;
        emit(MagicVarsConfigModelEvent.LAST_VARIABLE_ID_UPDATED, old, lastVariableId);
    }

    public void saveCurrentVariable() {

        // check the regexes
        if ( currentVariablePathScopeRegex.length() > 0 ) {
            if ( !RegexUtil.validateRegex(currentVariablePathScopeRegex)) {
                emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_SAVE_ERROR, null, "Path scope regex is invalid");
            }
        }
        if ( currentVariableReadRegex.length() > 0 ) {
            if ( !RegexUtil.validateRegex(currentVariableReadRegex)) {
                emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_SAVE_ERROR, null, "Variable read regex is invalid");
                return;
            }
            if ( currentVariableReadCaptureGroup == 0 || currentVariableReadCaptureGroup > RegexUtil.getMatchGroupCount(currentVariableReadRegex)) {
                emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_SAVE_ERROR, null, "Variable read regex capture group is out of bounds");
                return;
            }
        }

        if ( currentVariableWriteRegex.length() > 0 ) {
            if ( !RegexUtil.validateRegex(currentVariableWriteRegex)) {
                emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_SAVE_ERROR, null, "Variable write regex is invalid");
                return;
            }
            if ( currentVariableWriteCaptureGroup == 0 || currentVariableWriteCaptureGroup > RegexUtil.getMatchGroupCount(currentVariableWriteRegex)) {
                emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_SAVE_ERROR, null, "Variable write regex capture group is out of bounds");
                return;
            }
        }

        // Updating
        if ( currentVariableId != null ) {
            MagicVariable currentVariable = getVariableById(currentVariableId);
            if ( currentVariable != null ) {
                currentVariable.name = currentVariableName;
                currentVariable.description = currentVariableDescription;
                currentVariable.magicVariableType = currentVariableMagicVariableType;
                currentVariable.initialValue = currentVariableInitialValue;
                currentVariable.pathScopeRegex = currentVariablePathScopeRegex == null ? null : Pattern.compile(currentVariablePathScopeRegex);
                currentVariable.enabled = currentVariableEnabled;
                currentVariable.readRegex = currentVariableReadRegex == null ? null : Pattern.compile(currentVariableReadRegex);
                currentVariable.writeRegex = currentVariableWriteRegex == null ? null : Pattern.compile(currentVariableWriteRegex);
                currentVariable.readCaptureGroup = currentVariableReadCaptureGroup;
                currentVariable.writeCaptureGroup = currentVariableWriteCaptureGroup;
                addMagicVarToTable(currentVariable);
                emit(MagicVarsConfigModelEvent.VARIABLE_UPDATED, null, null);
            }
            else {
                // ERROR
            }
        }
        // Adding
        else {
            if (!nameIsUnique(currentVariableName)) {
                emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_SAVE_ERROR, null, String.format("A variable named %s already exists", currentVariableName));
                return;
            }
            MagicVariable newVariable = new MagicVariable(
                    UUID.randomUUID().toString(),
                    currentVariableName,
                    currentVariableDescription,
                    currentVariableMagicVariableType,
                    currentVariableInitialValue,
                    currentVariablePathScopeRegex == null ? null : Pattern.compile(currentVariablePathScopeRegex),
                    currentVariableEnabled,
                    currentVariableReadRegex == null ? null : Pattern.compile(currentVariableReadRegex,Pattern.MULTILINE|Pattern.DOTALL),
                    currentVariableWriteRegex == null ? null : Pattern.compile(currentVariableWriteRegex,Pattern.MULTILINE|Pattern.DOTALL),
                    currentVariableReadCaptureGroup,
                    currentVariableWriteCaptureGroup,
                    null,
                    null,
                    0
            );
            addVariable(newVariable);
        }
    }

    public void removeVariable( String id ) {
        for (int i = 0; i < magicVariables.size(); i++) {
            if (magicVariables.get(i).id.equals(id)) {
                magicVariables.remove(i);
                //syncOrder();
                clearCurrentVariable();
                emit(MagicVarsConfigModelEvent.VARIABLE_REMOVED, null, id);
                break;
            }
        }
    }

    public void addVariable( MagicVariable magicVariable ) {
        magicVariables.add(magicVariable);
        addMagicVarToTable(magicVariable);
        syncOrder();
        editMagicVariableById(magicVariable.id);
        emit(MagicVarsConfigModelEvent.VARIABLE_ADDED, null, magicVariable);
    }

    public void moveCurrentVariableUp() {
        int currentOrder = getCurrentVariableOrder();
        if ( currentOrder >= 1 ) {
            Logger.log("DEBUG", String.format("Moving row %d to %d", currentOrder,currentOrder-1));
            customVariablesModel.moveRow(currentOrder,currentOrder,currentOrder-1);
            syncOrder();
            emit(MagicVarsConfigModelEvent.VARIABLE_ORDER_UPDATED, currentOrder, currentOrder-1);
        }
        debugLogVariableOrders();
    }

    public void moveCurrentVariableDown() {
        int currentOrder = getCurrentVariableOrder();
        if ( currentOrder < customVariablesModel.getRowCount()-1) {
            Logger.log("DEBUG", String.format("Moving row %d to %d", currentOrder,currentOrder+1));
            customVariablesModel.moveRow(currentOrder,currentOrder,currentOrder+1);
            syncOrder();
            emit(MagicVarsConfigModelEvent.VARIABLE_ORDER_UPDATED, currentOrder, currentOrder+1);
        }
        debugLogVariableOrders();
    }

    public int getCurrentSelectedIdx() {
        return currentSelectedIdx;
    }

    public void setCurrentSelectedIdx(int currentSelectedIdx) {
        this.currentSelectedIdx = currentSelectedIdx;
    }

    /*
            Syncs the order of the jtable to the order property of the magic variable list
         */
    public void syncOrder() {
        for ( int i = 0; i < customVariablesModel.getRowCount(); i++ ) {
            String currentRowId = (String) customVariablesModel.getValueAt(i,0);
            getVariableById(currentRowId).order = i;
        }
    }

    public void debugLogVariableOrders() {
        StringBuilder variableOrder = new StringBuilder();
        for ( MagicVariable magicVariable : magicVariables ) {
            variableOrder.append(String.format("name(%s):order(%d), ", magicVariable.name,magicVariable.order));
        }
        Logger.log("DEBUG", String.format("ORDER = %s", variableOrder.toString()));
    }

    /*
        Gets a jtable row by magic variable id
     */
    public int getTableRowIndexById( String id ) {
        for ( int i = 0; i < customVariablesModel.getRowCount(); i++ ) {
            String currentRowId = (String) customVariablesModel.getValueAt(i,0);
            if ( currentRowId.equals(id) ) {
                return i;
            }
        }
        return -1;
    }

    /*
        Gets a custom variable by name
     */
    public MagicVariable getMagicVariableByName( String name ) {
        if ( name != null ) {
            for ( MagicVariable magicVariable : magicVariables ) {
                if ( magicVariable.name.equalsIgnoreCase(name)) {
                    return magicVariable;
                }
            }
        }
        return null;
    }

    /*
        Loads a current record by id
     */
    public void editMagicVariableById ( String id ) {
        MagicVariable magicVariable = getVariableById(id);
        if ( magicVariable != null ) {
            setCurrentVariableName(magicVariable.name);
            setCurrentVariableDescription(magicVariable.description);
            setCurrentVariableEnabled(magicVariable.enabled);
            setCurrentVariableMagicVariableType(magicVariable.magicVariableType);
            setCurrentVariableInitialValue(magicVariable.initialValue);
            setCurrentVariablePathScopeRegex(magicVariable.pathScopeRegex == null ? "" : magicVariable.pathScopeRegex.toString());
            setCurrentVariableReadCaptureGroup(magicVariable.readCaptureGroup);
            setCurrentVariableWriteCaptureGroup(magicVariable.writeCaptureGroup);
            setCurrentVariableReadRegex(magicVariable.readRegex == null ? "" : magicVariable.readRegex.toString());
            setCurrentVariableWriteRegex(magicVariable.writeRegex == null ? "" : magicVariable.writeRegex.toString());
            setCurrentVariableId(magicVariable.id);
        }
        else {
            Logger.log("DEBUG","MAGIC VARIABLE IS NULL :(");
        }
    }

    /*
        Adds or updates a row to the jtable
    */
    public void addMagicVarToTable( MagicVariable magicVariable ) {
        if ( magicVariable != null ) {
            int curIdx = getTableRowIndexById(magicVariable.id);
            // Update
            if ( curIdx >= 0 ) {
                customVariablesModel.setValueAt(magicVariable.id,curIdx,0);
                customVariablesModel.setValueAt(magicVariable.enabled,curIdx,1);
                customVariablesModel.setValueAt(magicVariable.name,curIdx,2);
                customVariablesModel.setValueAt(MagicVariableType.getPrettyName(magicVariable.magicVariableType),curIdx,3);
                customVariablesModel.setValueAt(magicVariable.pathScopeRegex == null ? "" : magicVariable.pathScopeRegex.toString(),curIdx,4);
                customVariablesModel.setValueAt(magicVariable.readRegex == null ? "" : magicVariable.readRegex.toString(),curIdx,5);
                customVariablesModel.setValueAt(magicVariable.writeRegex == null ? "" : magicVariable.writeRegex.toString(),curIdx,6);
                customVariablesModel.setValueAt(magicVariable.initialValue,curIdx,7);
                customVariablesModel.setValueAt(magicVariable.currentValue,curIdx,8);
                customVariablesModel.setValueAt(magicVariable.lastUpdated == null ? "" : magicVariable.lastUpdated.toString(),curIdx,9);
                customVariablesModel.setValueAt(magicVariable.description,curIdx,10);
            }
            // Add
            else {
                customVariablesModel.insertRow(0,new Object[] {
                        magicVariable.id,
                        magicVariable.enabled ? "true" : "false",
                        magicVariable.name,
                        MagicVariableType.getPrettyName(magicVariable.magicVariableType),
                        magicVariable.pathScopeRegex == null ? "" : magicVariable.pathScopeRegex.toString(),
                        magicVariable.readRegex == null ? "" : magicVariable.readRegex.toString(),
                        magicVariable.writeRegex == null ? "" : magicVariable.writeRegex.toString(),
                        magicVariable.initialValue,
                        magicVariable.currentValue,
                        magicVariable.lastUpdated == null ? "" : magicVariable.lastUpdated.toString(),
                        magicVariable.description
                });

            }
        }
    }

    public int getCurrentVariableOrder() {
        if ( currentVariableId != null ) {
            for ( int i = 0; i < customVariablesModel.getRowCount(); i++ ) {
                String currentRowId = (String) customVariablesModel.getValueAt(i,0);
                if ( currentRowId.equals(currentVariableId)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ArrayList<MagicVariable> getMagicVariables() {
        return magicVariables;
    }

    public MagicVariable getVariableById( String id ) {
        if ( id != null ) {
            for ( MagicVariable magicVariable : magicVariables ) {
                if ( magicVariable.id.equals(id)) {
                    return magicVariable;
                }
            }
        }
        return null;
    }

    public void updateMagicVariableCurrentValue ( String id, String currentValue ) {
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_CURRENT_VALUE_UPDATED, null, null);
        emit(MagicVarsConfigModelEvent.CURRENT_VARIABLE_CURRENT_VALUE_LAST_UPDATE_UPDATED, null, null);
    }

    public DefaultTableModel getCustomVariablesModel() {
        return customVariablesModel;
    }

    public MagicVariableListExport exportVariables() {
        MagicVariableListExport export = new MagicVariableListExport();
        export.variables = new MagicVariable[magicVariables.size()];
        export.variables = (MagicVariable[]) magicVariables.toArray(export.variables);
        return export;
    }

    public void importVariables( MagicVariableListExport variableListExport ) {
        if ( variableListExport.variables != null ) {
            for ( MagicVariable magicVariable : variableListExport.variables ) {
                if ( getMagicVariableByName(magicVariable.name) == null ) {
                    addVariable(magicVariable);
                }
            }
        }
    }

}
