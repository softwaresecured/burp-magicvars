package burp_magicvars.ui;

import burp_magicvars.view.MagicVarsConfigView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MagicVarsTab extends JPanel {
    private MagicVarsConfigView magicVarsConfigView;
    public MagicVarsTab(MagicVarsConfigView magicVarsConfigView) {
        this.magicVarsConfigView = magicVarsConfigView;
        initComponents();
        initToolTips();
        initLayout();
    }

    private void initLayout() {
        // Current variable common settings & buttons
        JPanel pnlVariableCommonSettings = new JPanel(new WrapLayout(FlowLayout.LEFT));
        pnlVariableCommonSettings.add(new JLabel("Name"));
        pnlVariableCommonSettings.add(magicVarsConfigView.jtxtName);
        pnlVariableCommonSettings.add(new JLabel("Type"));
        pnlVariableCommonSettings.add(magicVarsConfigView.jcmbType);
        pnlVariableCommonSettings.add(new JLabel("Value"));
        pnlVariableCommonSettings.add(magicVarsConfigView.jtxtInitialValue);
        pnlVariableCommonSettings.add(new JLabel("Path scope"));
        pnlVariableCommonSettings.add(magicVarsConfigView.jtxtPathScope);
        pnlVariableCommonSettings.add(new JLabel("Description"));
        pnlVariableCommonSettings.add(magicVarsConfigView.jtxtDescription);
        pnlVariableCommonSettings.add(magicVarsConfigView.jchkEnabled);
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnNew);
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnSave);
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnCancel);
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnDelete);
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnMoveUp);
        pnlVariableCommonSettings.add(magicVarsConfigView.jbtnMoveDown);
        pnlVariableCommonSettings.add(Box.createHorizontalGlue());

        // Dynamic config options
        JPanel pnlVariableDynamicSettings = new JPanel(new WrapLayout(FlowLayout.LEFT));
        pnlVariableDynamicSettings.add(new JLabel("Read regex"));
        pnlVariableDynamicSettings.add(magicVarsConfigView.jtxtReadRegex);
        pnlVariableDynamicSettings.add(new JLabel("Capture group"));
        pnlVariableDynamicSettings.add(magicVarsConfigView.jspnReadRegexCaptureGroup);
        pnlVariableDynamicSettings.add(new JLabel("Write regex"));
        pnlVariableDynamicSettings.add(magicVarsConfigView.jtxtWriteRegex);
        pnlVariableDynamicSettings.add(new JLabel("Capture group"));
        pnlVariableDynamicSettings.add(magicVarsConfigView.jspnWriteRegexCaptureGroup);
        pnlVariableCommonSettings.add(Box.createHorizontalGlue());

        // Current variable settings container
        JPanel pnlVariableConfig = new JPanel();
        pnlVariableConfig.setLayout(new BorderLayout());
        pnlVariableConfig.setBorder(BorderFactory.createTitledBorder("Variable configuration"));
        pnlVariableConfig.add(pnlVariableCommonSettings,BorderLayout.NORTH);
        pnlVariableConfig.add(pnlVariableDynamicSettings,BorderLayout.SOUTH);


        // Variables list
        JScrollPane jscrollVariables = new JScrollPane(magicVarsConfigView.jtblCustomMagicVariables);
        jscrollVariables.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jscrollVariables.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        magicVarsConfigView.jtblCustomMagicVariables.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel pnlCurrentVariables = new JPanel();
        pnlCurrentVariables.setLayout(new BorderLayout());
        pnlCurrentVariables.setBorder(BorderFactory.createTitledBorder("Current variables"));
        pnlCurrentVariables.add(jscrollVariables,BorderLayout.CENTER);




        // General settings ( at the bottom )
        JPanel pnlGeneralSettings = new JPanel(new WrapLayout(FlowLayout.LEFT)) ;
        pnlGeneralSettings.add(new JLabel("Enabled sources"));
        pnlGeneralSettings.add(magicVarsConfigView.jchkToolSourceProxy);
        pnlGeneralSettings.add(magicVarsConfigView.jchkToolSourceProxy);
        pnlGeneralSettings.add(magicVarsConfigView.jchkToolSourceRepeater);
        pnlGeneralSettings.add(magicVarsConfigView.jchkToolSourceExtensions);
        pnlGeneralSettings.add(magicVarsConfigView.jchkToolSourceIntruder);
        pnlGeneralSettings.add(magicVarsConfigView.jchkToolSourceScanner);
        pnlGeneralSettings.add(new JLabel("Left variable marker"));
        pnlGeneralSettings.add(magicVarsConfigView.jtxtLeftVariableMarker);
        pnlGeneralSettings.add(new JLabel("Right variable marker"));
        pnlGeneralSettings.add(magicVarsConfigView.jtxtRightVariableMarker);
        pnlGeneralSettings.add(magicVarsConfigView.jbtnImportVariables);
        pnlGeneralSettings.add(magicVarsConfigView.jbtnExportVariables);

        setLayout(new BorderLayout());
        add(pnlVariableConfig,BorderLayout.NORTH);
        add(pnlCurrentVariables,BorderLayout.CENTER);
        add(pnlGeneralSettings,BorderLayout.SOUTH);

        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                for ( Component component : new Component[]{pnlVariableConfig,pnlCurrentVariables,pnlGeneralSettings}) {
                    component.revalidate();
                }
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {

            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {

            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {

            }
        });

    }

    public void initToolTips() {
        magicVarsConfigView.jchkEnabled.setToolTipText("If the variable is enabled it will be included in requests otherwise it will be skipped when processing.");
        magicVarsConfigView.jtxtName.setToolTipText("The name of the variable. Must be unique.");
        magicVarsConfigView.jcmbType.setToolTipText("Variables can be static or dynamc. Static variables remain the same and dynamic variables can change over time.");
        magicVarsConfigView.jtxtPathScope.setToolTipText("The regex used to match the path of the current request. If it does not match, the variable will not be applied to the request.");
        magicVarsConfigView.jtxtReadRegex.setToolTipText("The regex used to read the value from the traffic. This regex should have one capture group identifying where the value is to be read from.");
        magicVarsConfigView.jtxtWriteRegex.setToolTipText("");
        magicVarsConfigView.jtxtLeftVariableMarker.setToolTipText("Variables are enclosed with one or more characters on either side. Pick a variable marker that does not collide with whatever you're testing. Avoid character sequences used in common templating languages.");
        magicVarsConfigView.jtxtRightVariableMarker.setToolTipText("Variables are enclosed with one or more characters on either side. Pick a variable marker that does not collide with whatever you're testing. Avoid character sequences used in common templating languages.");
        magicVarsConfigView.jspnReadRegexCaptureGroup.setToolTipText("The capture group for the regex expression.");
        magicVarsConfigView.jspnWriteRegexCaptureGroup.setToolTipText("The capture group for the regex expression.");
        magicVarsConfigView.jtxtInitialValue.setToolTipText("The initial value of the variable. For dynamic variables this value will update and for static variables this value will remain the same.");
        magicVarsConfigView.jtxtDescription.setToolTipText("A brief description of the variable.");
        magicVarsConfigView.jbtnMoveUp.setToolTipText("Move the variable up. Variables are processed in the order that they appear in this table.");
        magicVarsConfigView.jbtnMoveDown.setToolTipText("Move the variable down. Variables are processed in the order that they appear in this table.");
        magicVarsConfigView.jbtnNew.setToolTipText("Create a new variable.");
        magicVarsConfigView.jbtnSave.setToolTipText("Save the current variable.");
        magicVarsConfigView.jbtnCancel.setToolTipText("Cancel editing the current variable.");
        magicVarsConfigView.jbtnDelete.setToolTipText("Delete the current variable.");
        magicVarsConfigView.jbtnImportVariables.setToolTipText("Import variables from a JSON file.");
        magicVarsConfigView.jbtnExportVariables.setToolTipText("Export variables to a JSON file.");
        magicVarsConfigView.jchkToolSourceProxy.setToolTipText("Apply replacements to requests originating from the proxy.");
        magicVarsConfigView.jchkToolSourceRepeater.setToolTipText("Apply replacements to requests originating from the repeater.");
        magicVarsConfigView.jchkToolSourceIntruder.setToolTipText("Apply replacements to requests originating from the intruder.");
        magicVarsConfigView.jchkToolSourceExtensions.setToolTipText("Apply replacements to requests originating from extensions.");
        magicVarsConfigView.jchkToolSourceScanner.setToolTipText("Apply replacements to requests originating from the scanner.");
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

        magicVarsConfigView.jtxtUpdateAvailableMessage.setVisible(false);
        magicVarsConfigView.jtxtUpdateAvailableMessage.setBorder(BorderFactory.createEmptyBorder());
        magicVarsConfigView.jtxtUpdateAvailableMessage.setEditable(false);
        magicVarsConfigView.jtxtUpdateAvailableMessage.setHighlighter(null);
        magicVarsConfigView.jtxtUpdateAvailableMessage.setContentType("text/html");
    }

    private void setPreferredWidth(JComponent field, int width) {
        field.setPreferredSize(new Dimension(width, (int)field.getPreferredSize().getHeight()));
    }
}
