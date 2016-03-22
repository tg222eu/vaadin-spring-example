package hello;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class PopupViewClosingExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private static final String[] cities = new String[] {"id", "first", "last"};

    public PopupViewClosingExample() {
        setSpacing(true);

        NativeSelect l = new NativeSelect();
        for (int i = 0; i < cities.length; i++) {
            l.addItem(cities[i]);
        }

        l.setNullSelectionAllowed(false);
        l.setValue("id");
        l.setImmediate(true);
        l.addListener(this);

        addComponent(l);
    }

    /*
     * Shows a notification when a selection is made.
     */
    public void valueChange(ValueChangeEvent event) {
        //getWindow().showNotification("Selected city: " + event.getProperty());

    }
}