package hello;

import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import java.util.Arrays;
import java.util.List;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

	private final CustomerRepository repo;
	private final CustomerEditor editor;
	private final Grid grid;
	private final TextField filter;
	private final Button addNewBtn;
	private final Button delBtn;
	private final Button undoBtn;
	private final Button searchBtn;
	private final Button applyBtn;
	private final TextArea area = new TextArea("Info about person goes here");


	@Autowired
	public VaadinUI(CustomerRepository repo, CustomerEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid();
		this.filter = new TextField();
		this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
		this.delBtn = new Button("Delete", FontAwesome.TRASH_O);
		this.undoBtn = new Button("Undo", FontAwesome.UNDO);
		this.searchBtn = new Button("Search", FontAwesome.SEARCH);
		this.applyBtn = new Button("Apply");
	}

	@Override
	protected void init(VaadinRequest request) {
		// build layout for interface 1
		HorizontalLayout actions = new HorizontalLayout(filter, searchBtn, addNewBtn);
		VerticalLayout editorArea = new VerticalLayout(editor, area);
		HorizontalLayout gridInfo = new HorizontalLayout(grid, editorArea);
		VerticalLayout personListLayout = new VerticalLayout(actions, gridInfo);
		HorizontalLayout listLayout = new HorizontalLayout(personListLayout);
		VerticalLayout root = new VerticalLayout(listLayout);

		// build layout for interface 2

		Table table = new Table("List of database");
		table.setStyleName("iso3166");
		table.setWidth("50%");
		table.setHeight("170px");
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setImmediate(true); // react at once when something is selected

		table.setContainerDataSource(new BeanItemContainer(Customer.class, repo.findAll()));

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		table.setColumnHeaders("id", "firstName", "lastName" );

		HorizontalLayout searchAndList = new HorizontalLayout(filter, new PopupViewClosingExample());
		VerticalLayout tableVertLay = new VerticalLayout(searchAndList, table);
		VerticalLayout buttonLayout = new VerticalLayout(searchBtn, delBtn, undoBtn);
		HorizontalLayout hLayout = new HorizontalLayout(buttonLayout, tableVertLay);
		VerticalLayout root2 = new VerticalLayout(hLayout, applyBtn);

		root2.setMargin(true);
		root2.setSpacing(true);
		root2.setComponentAlignment(hLayout, Alignment.MIDDLE_CENTER);
		root2.setComponentAlignment(applyBtn, Alignment.MIDDLE_CENTER);

		searchAndList.setSpacing(true);
		tableVertLay.setSpacing(true);
		buttonLayout.setSpacing(true);
		hLayout.setSpacing(true);
		root2.setSpacing(true);

		// Set interface 1 or 2 (root, root2)
		setContent(root2);

		// Configure layouts and components
		actions.setSpacing(true);
		editorArea.setSpacing(true);
		listLayout.setSpacing(true);
		personListLayout.setSpacing(true);
		gridInfo.setSpacing(true);

		root.setMargin(true);
		root.setSpacing(true);
		root.setComponentAlignment(listLayout, Alignment.MIDDLE_CENTER);


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
