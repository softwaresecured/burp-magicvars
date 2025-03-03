package burp_magicvars.mvc;

import burp_magicvars.config.AbstractConfig;
import burp_magicvars.event.EventEmitter;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;

public abstract class AbstractModel<TEvent extends Enum<TEvent>> implements EventEmitter<TEvent> {
    private final SwingPropertyChangeSupport eventEmitter = new SwingPropertyChangeSupport(this);

    public abstract void load(AbstractConfig config);
    public abstract void save(AbstractConfig config);

    public void addListener(PropertyChangeListener listener) {
        this.eventEmitter.addPropertyChangeListener(listener);
    }

    public void emit(TEvent event, Object old, Object value) {
        eventEmitter.firePropertyChange(event.name(), old, value);
    }
}
