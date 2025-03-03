package burp_magicvars.mvc;



import burp_magicvars.event.EventEmitter;
import burp_magicvars.util.RegexValidityDocumentListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Callable;

public abstract class AbstractView<TEvent extends Enum<TEvent>, TModel extends AbstractModel<TModelEvent>, TModelEvent extends Enum<TModelEvent>> implements PropertyChangeListener, EventEmitter<TEvent> {
    private final SwingPropertyChangeSupport eventEmitter = new SwingPropertyChangeSupport(this);

    private final TModel model;

    public AbstractView(TModel model) {
        this.model = model;
    }

    public abstract void attachListeners();
    protected abstract void handleEvent(TModelEvent event, Object previous, Object next);

    public void addListener(PropertyChangeListener listener) {
        this.eventEmitter.addPropertyChangeListener(listener);
    }

    public void emit(TEvent event, Object old, Object value) {
        eventEmitter.firePropertyChange(event.name(), old, value);
    }

    protected void attachTextChangeEvent(JTextField field, Callable modelGetter, TEvent event) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkChanged();
            }

            public void checkChanged() {
                try {
                    if (!field.getText().equals((String)modelGetter.call())) {
                        emit(event, null, (String)modelGetter.call());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    protected  void attach (JList field, TEvent event ) {
        field.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if ( field.getSelectedValue() != null ) {
                    emit(event, null,field.getSelectedValue());
                }
            }
        });
    }

    protected void attachSelection( JTable table, TEvent event ) {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() ) {
                emit(event,null,table.getSelectedRow());
            }
        });
    }

    protected void attachTableModelChangeListener(TableModel model, TEvent event ) {
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                emit(event,null,e.getType());
            }
        });
    }

    protected void attach(JTextField field, TEvent event) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            private void update(DocumentEvent e) {
                emit(event, null, field.getText());
            }
            @Override
            public void changedUpdate(DocumentEvent e) { update(e); }
            @Override
            public void insertUpdate(DocumentEvent e) { update(e); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(e); }
        });
    }

    protected void attach(JButton field, TEvent event) {
        field.addActionListener(e -> emit(event, null, true));
    }

    protected void attach(JCheckBox field, TEvent event) {
        field.addActionListener(e -> emit(event, !field.isSelected(), field.isSelected()));
    }

    protected void attach(JComboBox field, TEvent event) {
        field.addActionListener(e -> emit(event, null, field.getSelectedItem().toString()));
    }

    protected void attach(JSpinner field, TEvent event) {
        field.addChangeListener(e -> emit(event, null, (int)field.getValue()));
    }

    protected void attachKey(JTextField field, int key, TEvent event) {
        field.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() != key) return;

                emit(event, null, true);
            }

            @Override
            public void keyTyped(KeyEvent keyEvent) {}

            @Override
            public void keyReleased(KeyEvent keyEvent) {}
        });
    }

    protected void attachKeyPress(JTextField field, TEvent event ) {
        field.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                emit(event, null, e.getKeyCode());
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    protected void checkRegex(JTextField field) {
        Color backGround = field.getBackground();
        Color foreGround = field.getForeground();
        field.getDocument().addDocumentListener(new RegexValidityDocumentListener(field, backGround, foreGround));
    }

    protected void update(JTextField field, Object next) {
        if (!field.getText().equals((String)next)) field.setText((String)next);
    }

    protected void update(JSpinner field, Object next) {
        if ((int)field.getValue() != (int)next) field.setValue(Integer.valueOf((int)next));
    }

    protected void update(JCheckBox field, Object next) {
        if (field.isSelected() != (boolean)next) field.setSelected((boolean)next);
    }

    public TModel getModel() {
        return model;
    }
}
