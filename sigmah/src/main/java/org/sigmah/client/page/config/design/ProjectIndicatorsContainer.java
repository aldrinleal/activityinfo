package org.sigmah.client.page.config.design;

import java.util.Map;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.entry.SiteMap;
import org.sigmah.client.page.entry.editor.SiteForm;
import org.sigmah.client.page.entry.editor.SiteFormDialog;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;
import org.sigmah.shared.report.model.DimensionType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class ProjectIndicatorsContainer extends LayoutContainer implements SubPresenter {

	//private SiteEditor siteEditor;
	private DesignPanel designPanel;
	private SchemaDTO schema;
	private UserDatabaseDTO db;
	private final Dispatcher service;
	private final EventBus eventBus;
	
	private ContentPanel mapContainer;
	private Button newIndicatorButton;
	private Button newGroupButton;
	private Button reloadButton;
	private TreeStore<ModelData> treeStore;	
	private TabPanel tabPanel;
	private TabItem mapTabItem;
	private TabItem sitesTabItem;
	
	private ProjectSiteGridPanel siteEditor;
	private SiteMap siteMap;
	
	private ProjectPresenter projectPresenter;
		
	@Inject
	public ProjectIndicatorsContainer(
			ProjectSiteGridPanel siteEditor,
			SiteMap siteMap,
			final DesignPanel designPanel, 
			Dispatcher service, EventBus eventBus) {
		
		this.siteEditor = siteEditor;
		this.siteMap = siteMap;
		this.designPanel = designPanel;
		this.service = service;
		this.eventBus = eventBus;
		
		
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setContainerStyle("x-border-layout-ct main-background");
		setLayout(borderLayout);

		ContentPanel mainPanel = new ContentPanel();
		mainPanel.setIcon(null);
		mainPanel.setLayout(new FitLayout());  
		mainPanel.setSize(600, 300);  

		// setIcon(IconImageBundle.ICONS.design());
		// map tab panel
		tabPanel = new TabPanel();
		tabPanel.setPlain(true);

		// map tab item
		mapTabItem = new TabItem("map");
		mapTabItem.setLayout(new FitLayout());
		mapTabItem.setEnabled(false);
		mapTabItem.setAutoHeight(true);
		mapTabItem.setEnabled(true);
		mapTabItem.add(siteMap);
		tabPanel.add(mapTabItem);

		// sites tab item
		sitesTabItem = new TabItem("sites");
		sitesTabItem.setLayout(new FitLayout());
		sitesTabItem.setEnabled(false);
		sitesTabItem.setAutoHeight(true);
		sitesTabItem.setEnabled(true);
		sitesTabItem.add(siteEditor);
		tabPanel.add(sitesTabItem);

		// buttons for indicator view
		newIndicatorButton = new Button("new indicator");
		newGroupButton = new Button("new group");
		reloadButton = new Button("reload button");

		mainPanel.add(newIndicatorButton);
		mainPanel.add(newGroupButton);
		mainPanel.add(reloadButton);

		BorderLayoutData centerLayout = new BorderLayoutData(
				Style.LayoutRegion.CENTER);
		centerLayout.setMargins(new Margins(0, 0, 0, 0));
		centerLayout.setSplit(true);
		centerLayout.setCollapsible(true);

		BorderLayoutData layout = new BorderLayoutData(Style.LayoutRegion.EAST);
		layout.setSplit(true);
		layout.setCollapsible(true);
		layout.setSize(375);
		layout.setMargins(new Margins(0, 0, 0, 5));

		add(designPanel, centerLayout);
		designPanel.getMappedIndicator().addValueChangeHandler(new ValueChangeHandler<IndicatorDTO>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<IndicatorDTO> event) {
				onMappedIndicatorChanged(event.getValue());
			}
		});
		
		add(tabPanel, layout);
		// setHeading(I18N.CONSTANTS.design() + " - " );
		
		siteEditor.addActionListener(new Listener<ComponentEvent>() {
			
			@Override
			public void handleEvent(ComponentEvent be) {
				if(UIActions.add.equals(be.getComponent().getItemId() )) {
					addSite();
				}
			}
		});
	}
	


	public void setProjectPresenter(ProjectPresenter projectPresenter) {
		this.projectPresenter = projectPresenter;
	}
	

	public void loadProject(ProjectDTO projectDTO) {
		
		// load design panel
		designPanel.load(projectDTO.getId());
		
		// load site grid
		Filter siteFilter = new Filter();
		siteFilter.addRestriction(DimensionType.Database, projectDTO.getId());
		siteEditor.load(siteFilter);			
	}
	
	private void onMappedIndicatorChanged(IndicatorDTO value) {
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Indicator, value.getId());
		
		siteMap.loadSites(projectPresenter.getCurrentProjectDTO().getCountry(), 
				filter);
	}

	protected void addSite() {

		final int projectId = projectPresenter.getCurrentProjectDTO().getId();

		SiteDTO site = new SiteDTO();
		site.setDatabaseId(projectId);
		//site.setPartner(projectPresenter.getCurrentProjectDTO().getOrgUnitId());
		
		final SiteForm form = new SiteForm(service, projectPresenter.getCurrentProjectDTO().getCountry());
		form.setSite(site);
		
		final SiteFormDialog dialog = new SiteFormDialog(form);
		dialog.show(new FormDialogCallback() {

			@Override
			public void onValidated() {
				Map<String, Object> props = form.getPropertyMap();
				props.put("databaseId", projectId);
								 
				service.execute(new CreateEntity("Site", props), dialog, new AsyncCallback<CreateResult>() {

					@Override
					public void onFailure(Throwable caught) {						
					}

					@Override
					public void onSuccess(CreateResult result) {
						dialog.hide();
					}
				});
			}
			
		});
	}
	
	@Override
	public Component getView() {
		this.designPanel.setProjectPresenter(this.projectPresenter);
		return (Component) this;
	}

	@Override
	public void viewDidAppear() {
		// TODO Auto-generated method stub
	}

	@Override
	public void discardView() {
		// TODO Auto-generated method stub
		
	}


}
