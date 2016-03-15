package hello;

import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

@SpringUI
@Theme("valo")
public class Interface2 extends UI {

    private final CustomerRepository repo;
    private final CustomerEditor editor;
    private final Grid grid;
    private final TextField filter;
    private final Button addNewBtn;
    private final Button searchBtn;
    private final TextArea area = new TextArea("Info about person goes here");

    @Autowired
    public Interface2(CustomerRepository repo, CustomerEditor editor) {
        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
        this.searchBtn = new Button("Search", FontAwesome.SEARCH);
    }

    @Override
    protected void init(VaadinRequest request) {

        //

        Table table = new Table("ISO-3166 Country Codes and flags");

        // build layout

        VerticalLayout mainLayout = new VerticalLayout();
        setContent(mainLayout);

        // Configure layouts and components

        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        //mainLayout.setComponentAlignment(listLayout, Alignment.MIDDLE_CENTER);


        grid.setHeight(600, Unit.PIXELS);
        grid.setColumns("id", "firstName", "lastName");

        filter.setInputPrompt("Filter by last name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.addTextChangeListener(e -> listCustomers(e.getText()));

        // Connect selected Customer to editor or hide if none is selected
        grid.addSelectionListener(e -> {
            if (e.getSelected().isEmpty()) {
                editor.setVisible(false);
            }
            else {
                editor.editCustomer((Customer) e.getSelected().iterator().next());
            }
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "")));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

        // Initialize listing
        listCustomers(null);
    }

    // tag::listCustomers[]
    private void listCustomers(String text) {
        if (StringUtils.isEmpty(text)) {
            grid.setContainerDataSource(
                    new BeanItemContainer(Customer.class, repo.findAll()));
        }
        else {
            grid.setContainerDataSource(new BeanItemContainer(Customer.class,
                    repo.findByLastNameStartsWithIgnoreCase(text)));
        }
    }
    // end::listCustomers[]

}
