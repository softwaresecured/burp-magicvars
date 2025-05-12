package burp_magicvars.view;

import burp_magicvars.enums.EditorState;
import burp_magicvars.enums.MagicVariableType;
import burp_magicvars.event.MagicVarsConfigControllerEvent;
import burp_magicvars.event.MagicVarsConfigModelEvent;
import burp_magicvars.model.MagicVarsConfigModel;
import burp_magicvars.mvc.AbstractView;
import burp_magicvars.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
public class MagicVarsConfigView extends AbstractView<MagicVarsConfigControllerEvent, MagicVarsConfigModel, MagicVarsConfigModelEvent> {

    public final JTable jtblCustomMagicVariables;

    public final JCheckBox jchkEnabled = new JCheckBox("Enabled");
    public final JTextField jtxtName = new JTextField("");
    public final JComboBox<String> jcmbType = new JComboBox<String>(new String[] {"Static","Dynamic"});
    public final JTextField jtxtPathScope = new JTextField("");
    public final JTextField jtxtReadRegex = new JTextField("");
    public final JTextField jtxtWriteRegex = new JTextField("");

    public final JTextField jtxtLeftVariableMarker = new JTextField("");
    public final JTextField jtxtRightVariableMarker = new JTextField("");

    public final JSpinner jspnReadRegexCaptureGroup = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    public final JSpinner jspnWriteRegexCaptureGroup = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

    public final JTextField jtxtInitialValue = new JTextField("");
    public final JTextField jtxtDescription = new JTextField("");

    public final JButton jbtnMoveUp = new JButton("Up");
    public final JButton jbtnMoveDown = new JButton("Down");
    public final JButton jbtnNew = new JButton("New");
    public final JButton jbtnSave = new JButton("Save");
    public final JButton jbtnCancel = new JButton("Cancel");
    public final JButton jbtnDelete = new JButton("Delete");
    public final JButton jbtnImportVariables = new JButton("Import");
    public final JButton jbtnExportVariables = new JButton("Export");

    public final JCheckBox jchkToolSourceProxy = new JCheckBox("Proxy");
    public final JCheckBox jchkToolSourceRepeater = new JCheckBox("Repeater");
    public final JCheckBox jchkToolSourceIntruder = new JCheckBox("Intruder");
    public final JCheckBox jchkToolSourceExtensions = new JCheckBox("Extensions");
    public final JCheckBox jchkToolSourceScanner = new JCheckBox("Scanner");

    public final JTextPane jtxtUpdateAvailableMessage = new JTextPane();

    private Component parentComponent;

    public MagicVarsConfigView(MagicVarsConfigModel model) {
        super(model);
        jtblCustomMagicVariables = new JTable(getModel().getCustomVariablesModel());
    }

    @Override
    public void attachListeners() {
        attach(jbtnNew, MagicVarsConfigControllerEvent.NEW);
        attach(jbtnSave, MagicVarsConfigControllerEvent.SAVE);
        attach(jbtnDelete, MagicVarsConfigControllerEvent.DELETE);
        attach(jbtnCancel, MagicVarsConfigControllerEvent.CANCEL);
        attach(jbtnMoveUp, MagicVarsConfigControllerEvent.MOVE_UP);
        attach(jbtnMoveDown, MagicVarsConfigControllerEvent.MOVE_DOWN);

        attach(jbtnImportVariables, MagicVarsConfigControllerEvent.IMPORT_VARIABLES);
        attach(jbtnExportVariables, MagicVarsConfigControllerEvent.EXPORT_VARIABLES);

        attach(jcmbType, MagicVarsConfigControllerEvent.UPDATE_VARIABLE_TYPE);
        attach(jtxtName, MagicVarsConfigControllerEvent.UPDATE_NAME);
        attach(jtxtInitialValue, MagicVarsConfigControllerEvent.UPDATE_INITIAL_VALUE);
        attach(jtxtDescription, MagicVarsConfigControllerEvent.UPDATE_DESCRIPTION);
        attach(jchkEnabled, MagicVarsConfigControllerEvent.TOGGLE_ENABLED);
        attach(jspnReadRegexCaptureGroup, MagicVarsConfigControllerEvent.UPDATE_READ_CAPTURE_GROUP);
        attach(jspnWriteRegexCaptureGroup, MagicVarsConfigControllerEvent.UPDATE_WRITE_CAPTURE_GROUP);

        attach(jchkToolSourceIntruder,MagicVarsConfigControllerEvent.TOGGLE_SOURCE_INTRUDER);
        attach(jchkToolSourceRepeater,MagicVarsConfigControllerEvent.TOGGLE_SOURCE_REPEATER);
        attach(jchkToolSourceScanner,MagicVarsConfigControllerEvent.TOGGLE_SOURCE_SCANNER);
        attach(jchkToolSourceProxy,MagicVarsConfigControllerEvent.TOGGLE_SOURCE_PROXY);
        attach(jchkToolSourceExtensions,MagicVarsConfigControllerEvent.TOGGLE_SOURCE_EXTENSIONS);

        attachSelection(jtblCustomMagicVariables,MagicVarsConfigControllerEvent.ROW_SELECTION_UPDATE);
        attachTableModelChangeListener(jtblCustomMagicVariables.getModel(),MagicVarsConfigControllerEvent.VARIABLES_TABLE_MODEL_CHANGED);

        attach(jtxtPathScope, MagicVarsConfigControllerEvent.UPDATE_PATH_SCOPE);
        checkRegex(jtxtPathScope);

        attach(jtxtReadRegex, MagicVarsConfigControllerEvent.UPDATE_READ_REGEX);
        checkRegex(jtxtReadRegex);

        attach(jtxtWriteRegex, MagicVarsConfigControllerEvent.UPDATE_WRITE_REGEX);
        checkRegex(jtxtWriteRegex);

        attach(jtxtLeftVariableMarker,MagicVarsConfigControllerEvent.LEFT_VARIABLE_MARKER_CHANGED);
        attach(jtxtRightVariableMarker,MagicVarsConfigControllerEvent.RIGHT_VARIABLE_MARKER_CHANGED);
        attachClick(jtxtUpdateAvailableMessage, MagicVarsConfigControllerEvent.DISMISS_UPDATE);
    }

    @Override
    protected void handleEvent(MagicVarsConfigModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case EDITOR_STATE_CHANGED:
                updateEditorButtonsState((EditorState)next);
                updateInputsState(getModel().getEditorState(),getModel().getCurrentVariableMagicVariableType());
                break;
            case CURRENT_VARIABLE_SAVE_ERROR:
                JOptionPane.showMessageDialog(parentComponent, (String)next,"Error saving Magic Variable",JOptionPane.ERROR_MESSAGE);
                break;
            case VARIABLE_LEFT_VARIABLE_MARKER_UPDATED:
                jtxtLeftVariableMarker.setText((String)next);
                break;
            case VARIABLE_RIGHT_VARIABLE_MARKER_UPDATED:
                jtxtRightVariableMarker.setText((String)next);
                break;
            case CURRENT_VARIABLE_ID_UPDATED:
                int row = getModel().getTableRowIndexById((String)next);
                if ( row >=0 ) {
                    jtblCustomMagicVariables.getSelectionModel().setSelectionInterval(row,row);
                    Logger.log("DEBUG", String.format("Setting ROW TO %d", row));
                }
                jtxtName.setText(getModel().getCurrentVariableName());
                jtxtDescription.setText(getModel().getCurrentVariableDescription());
                jtxtInitialValue.setText(getModel().getCurrentVariableInitialValue());
                jtxtPathScope.setText(getModel().getCurrentVariablePathScopeRegex());
                jchkEnabled.setSelected(getModel().isCurrentVariableEnabled());
                jtxtReadRegex.setText(getModel().getCurrentVariableReadRegex());
                jtxtWriteRegex.setText(getModel().getCurrentVariableWriteRegex());

                jspnReadRegexCaptureGroup.setValue(getModel().getCurrentVariableReadCaptureGroup());
                jspnWriteRegexCaptureGroup.setValue(getModel().getCurrentVariableWriteCaptureGroup());

                updateInputsState(getModel().getEditorState(),getModel().getCurrentVariableMagicVariableType());
                updateDynamicVariableInputsState(getModel().getCurrentVariableMagicVariableType());
                break;

            case CURRENT_VARIABLE_TYPE_UPDATED:
                jcmbType.setSelectedItem(MagicVariableType.getPrettyName(getModel().getCurrentVariableMagicVariableType()));
                updateDynamicVariableInputsState((MagicVariableType)next);
                break;

            case VARIABLE_REMOVED:
                // remove from table
                for ( int i = 0; i < getModel().getCustomVariablesModel().getRowCount(); i++ ) {
                    String curRowId = (String) getModel().getCustomVariablesModel().getValueAt(i,0);
                    if ( curRowId.equals((String)next)) {
                        getModel().getCustomVariablesModel().removeRow(i);
                        getModel().getCustomVariablesModel().fireTableDataChanged();
                        break;
                    }
                }

                // Select a new row
                int currentIndex = getModel().getCurrentSelectedIdx();
                if ( currentIndex >= jtblCustomMagicVariables.getRowCount()) {
                    currentIndex -= 1;
                }
                if ( currentIndex >= 0 && jtblCustomMagicVariables.getRowCount() > 0 ) {
                    jtblCustomMagicVariables.getSelectionModel().setSelectionInterval(currentIndex,currentIndex);
                }
                break;

            case VARIABLE_ORDER_UPDATED:
                jtblCustomMagicVariables.getSelectionModel().setSelectionInterval((Integer)next,(Integer)next);
                break;
            case ENABLED_SOURCES_UPDATED:
                jchkToolSourceProxy.setSelected(getModel().getEnabledToolSources().contains("Proxy"));
                jchkToolSourceRepeater.setSelected(getModel().getEnabledToolSources().contains("Repeater"));
                jchkToolSourceExtensions.setSelected(getModel().getEnabledToolSources().contains("Extensions"));
                jchkToolSourceIntruder.setSelected(getModel().getEnabledToolSources().contains("Intruder"));
                jchkToolSourceScanner.setSelected(getModel().getEnabledToolSources().contains("Scanner"));
                break;
            case UPDATE_AVAILABLE_MESSAGE_UPDATED:
                if ( getModel().getUpdateAvailableMessage() != null ) {
                    jtxtUpdateAvailableMessage.setVisible(true);
                    jtxtUpdateAvailableMessage.setText(getModel().getUpdateAvailableMessage());
                }
                else {
                    jtxtUpdateAvailableMessage.setVisible(false);
                }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        handleEvent(MagicVarsConfigModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    private void updateInputsState(EditorState editorState, MagicVariableType magicVariableType ) {

        jtxtName.setEnabled(editorState == EditorState.INITIAL ? false : true);
        jcmbType.setEnabled(editorState == EditorState.INITIAL ? false : true);
        jtxtDescription.setEnabled(editorState == EditorState.INITIAL ? false : true);
        jtxtInitialValue.setEnabled(editorState == EditorState.INITIAL ? false : true);
        jtxtPathScope.setEnabled(editorState == EditorState.INITIAL ? false : true);
        jchkEnabled.setEnabled(editorState == EditorState.INITIAL ? false : true);

        jtxtReadRegex.setEnabled(editorState == EditorState.INITIAL ? false : true);
        jspnReadRegexCaptureGroup.setEnabled(editorState == EditorState.INITIAL ? false : true);
        jtxtWriteRegex.setEnabled(editorState == EditorState.INITIAL ? false : true);
        jspnWriteRegexCaptureGroup.setEnabled(editorState == EditorState.INITIAL ? false : true);

        jbtnMoveUp.setEnabled(editorState == EditorState.EDIT ? true : false);
        jbtnMoveDown.setEnabled(editorState == EditorState.EDIT ? true : false);

        jtxtUpdateAvailableMessage.setVisible(false);


        if ( editorState.equals(EditorState.EDIT) ) {
            if ( getModel().getCurrentVariableOrder() < 1 ) {
                jbtnMoveUp.setEnabled(false);
            }
            if ( getModel().getCurrentVariableOrder() >= getModel().getCustomVariablesModel().getRowCount()-1 ) {
                jbtnMoveDown.setEnabled(false);
            }
        }

        if ( getModel().getCustomVariablesModel().getRowCount() == 0 ) {
            jbtnMoveUp.setEnabled(false);
            jbtnMoveDown.setEnabled(false);
        }

        updateDynamicVariableInputsState(magicVariableType);
    }

    private void updateDynamicVariableInputsState( MagicVariableType magicVariableType ) {
        jtxtReadRegex.setEnabled(magicVariableType == MagicVariableType.STATIC ? false : true);
        jspnReadRegexCaptureGroup.setEnabled(magicVariableType == MagicVariableType.STATIC ? false : true);
        jtxtWriteRegex.setEnabled(magicVariableType == MagicVariableType.STATIC ? false : true);
        jspnWriteRegexCaptureGroup.setEnabled(magicVariableType == MagicVariableType.STATIC ? false : true);
    }

    private void updateEditorButtonsState(EditorState editorState) {
        switch ( editorState ) {
            case EDIT:
                jbtnNew.setEnabled(true);
                jbtnSave.setEnabled(true);
                jbtnCancel.setEnabled(false);
                jbtnDelete.setEnabled(true);
                break;
            case CREATE:
                jbtnNew.setEnabled(false);
                jbtnSave.setEnabled(true);
                jbtnCancel.setEnabled(true);
                jbtnDelete.setEnabled(false);
                break;
            case INITIAL:
                jbtnNew.setEnabled(true);
                jbtnSave.setEnabled(false);
                jbtnDelete.setEnabled(false);
                jbtnCancel.setEnabled(false);
                break;
        }
    }
}
