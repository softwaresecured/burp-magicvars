package burp_magicvars.ui;

import burp_magicvars.enums.EditorState;
import burp_magicvars.view.MagicVarsConfigView;

import javax.swing.*;
import java.awt.*;

public class MagicVarsTab extends JPanel {
    private MagicVarsConfigView magicVarsConfigView;
    public MagicVarsTab(MagicVarsConfigView magicVarsConfigView) {
        this.magicVarsConfigView = magicVarsConfigView;
        initComponents();
        initLayout();
    }

    private void initLayout() {

        // Current variable common settings & buttons
        JPanel pnlVariableCommonSettings = new JPanel();
        pnlVariableCommonSettings.setLayout(new GridBagLayout());
        int idx = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(new JLabel("Name"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jtxtName,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(new JLabel("Type"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jcmbType,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(new JLabel("Value"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jtxtInitialValue,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(new JLabel("Path scope"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jtxtPathScope,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(new JLabel("Description"),gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jtxtDescription,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,5,0,0);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jchkEnabled,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,5,0,0);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnNew,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,5,0,0);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnSave,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,5,0,0);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnCancel,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,5,0,0);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnDelete,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,5,0,0);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnMoveUp,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,5,0,0);
        gbc.gridx = idx;
        gbc.gridy = 0;
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnMoveDown,gbc);

        // Dynamic config options
        JPanel pnlVariableDynamicSettings = new JPanel();
        pnlVariableDynamicSettings.setLayout(new GridBagLayout());
        idx = 0;

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableDynamicSettings.add(new JLabel("Read regex"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableDynamicSettings.add(magicVarsConfigView.jtxtReadRegex,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableDynamicSettings.add(new JLabel("Capture group"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableDynamicSettings.add(magicVarsConfigView.jspnReadRegexCaptureGroup,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableDynamicSettings.add(new JLabel("Write regex"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableDynamicSettings.add(magicVarsConfigView.jtxtWriteRegex,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlVariableDynamicSettings.add(new JLabel("Capture group"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx;
        gbc.gridy = 0;
        pnlVariableDynamicSettings.add(magicVarsConfigView.jspnWriteRegexCaptureGroup,gbc);

        // Current variable settings container
        JPanel pnlVariableConfig = new JPanel();
        pnlVariableConfig.setLayout(new GridBagLayout());
        pnlVariableConfig.setBorder(BorderFactory.createTitledBorder("Variable configuration"));

        int idy = 0;
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 5;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        pnlVariableConfig.add(pnlVariableCommonSettings,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 5;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        pnlVariableConfig.add(pnlVariableDynamicSettings,gbc);

        // Variables list
        JScrollPane jscrollVariables = new JScrollPane(magicVarsConfigView.jtblCustomMagicVariables);
        jscrollVariables.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jscrollVariables.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        magicVarsConfigView.jtblCustomMagicVariables.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel pnlVariableList = new JPanel();
        pnlVariableList.setLayout(new GridBagLayout());
        pnlVariableList.setBorder(BorderFactory.createTitledBorder("Current variables"));

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlVariableList.add(jscrollVariables,gbc);

        // Enabled sources
        JPanel pnlEnabledSourcesSettings = new JPanel();
        pnlEnabledSourcesSettings.setLayout(new GridBagLayout());
        idx = 0;

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlEnabledSourcesSettings.add(magicVarsConfigView.jchkToolSourceProxy,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlEnabledSourcesSettings.add(magicVarsConfigView.jchkToolSourceProxy,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlEnabledSourcesSettings.add(magicVarsConfigView.jchkToolSourceRepeater,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlEnabledSourcesSettings.add(magicVarsConfigView.jchkToolSourceExtensions,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlEnabledSourcesSettings.add(magicVarsConfigView.jchkToolSourceIntruder,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlEnabledSourcesSettings.add(magicVarsConfigView.jchkToolSourceScanner,gbc);

        idx = 0;
        // General settings ( at the bottom )
        JPanel pnlGeneralSettings = new JPanel();
        pnlGeneralSettings.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(new JLabel("Enabled sources"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(pnlEnabledSourcesSettings,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(new JLabel("Left variable marker"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(magicVarsConfigView.jtxtLeftVariableMarker,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(new JLabel("Right variable marker"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(magicVarsConfigView.jtxtRightVariableMarker,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(magicVarsConfigView.jbtnImportVariables,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        pnlGeneralSettings.add(magicVarsConfigView.jbtnExportVariables,gbc);


        // Main layout
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(pnlVariableConfig,gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipady = 5;
        add(pnlVariableList,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.ipady = 5;
        add(pnlGeneralSettings,gbc);
    }

    private void initComponents() {
        setPreferredWidth(magicVarsConfigView.jtxtName, 100);
        setPreferredWidth(magicVarsConfigView.jtxtInitialValue, 200);
        setPreferredWidth(magicVarsConfigView.jtxtPathScope, 200);
        setPreferredWidth(magicVarsConfigView.jtxtDescription, 300);
        setPreferredWidth(magicVarsConfigView.jtxtReadRegex, 300);
        setPreferredWidth(magicVarsConfigView.jtxtWriteRegex, 300);

        setPreferredWidth(magicVarsConfigView.jspnReadRegexCaptureGroup, 50);
        setPreferredWidth(magicVarsConfigView.jspnWriteRegexCaptureGroup, 50);

        setPreferredWidth(magicVarsConfigView.jtxtLeftVariableMarker, 40);
        setPreferredWidth(magicVarsConfigView.jtxtRightVariableMarker, 40);




        int[] colWidths = { 0, 80,200,100,200,200,200,200,200,200 };
        for ( int i = 0; i < colWidths.length; i++ ) {
            magicVarsConfigView.jtblCustomMagicVariables.getColumnModel().getColumn(i).setMinWidth(colWidths[i]);
            magicVarsConfigView.jtblCustomMagicVariables.getColumnModel().getColumn(i).setMaxWidth(colWidths[i]);
            magicVarsConfigView.jtblCustomMagicVariables.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }
        magicVarsConfigView.jtblCustomMagicVariables.setRowSelectionAllowed(true);
        magicVarsConfigView.jtblCustomMagicVariables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // All buttons except new disabled
        magicVarsConfigView.jbtnNew.setEnabled(true);
        magicVarsConfigView.jbtnSave.setEnabled(false);
        magicVarsConfigView.jbtnCancel.setEnabled(false);
        magicVarsConfigView.jbtnDelete.setEnabled(false);
        magicVarsConfigView.jbtnMoveUp.setEnabled(false);
        magicVarsConfigView.jbtnMoveDown.setEnabled(false);

        // All inputs disabled
        magicVarsConfigView.jtxtName.setEnabled(false);
        magicVarsConfigView.jcmbType.setEnabled(false);
        magicVarsConfigView.jtxtDescription.setEnabled(false);
        magicVarsConfigView.jtxtInitialValue.setEnabled(false);
        magicVarsConfigView.jtxtPathScope.setEnabled(false);
        magicVarsConfigView.jchkEnabled.setEnabled(false);
        magicVarsConfigView.jtxtReadRegex.setEnabled(false);
        magicVarsConfigView.jspnReadRegexCaptureGroup.setEnabled(false);
        magicVarsConfigView.jtxtWriteRegex.setEnabled(false);
        magicVarsConfigView.jspnWriteRegexCaptureGroup.setEnabled(false);

        magicVarsConfigView.jchkToolSourceExtensions.setSelected(true);
        magicVarsConfigView.jchkToolSourceIntruder.setSelected(true);
        magicVarsConfigView.jchkToolSourceProxy.setSelected(true);
        magicVarsConfigView.jchkToolSourceScanner.setSelected(true);
        magicVarsConfigView.jchkToolSourceRepeater.setSelected(true);

    }

    private void setPreferredWidth(JComponent field, int width) {
        field.setPreferredSize(new Dimension(width, (int)field.getPreferredSize().getHeight()));
    }
}
