package org.sigmah.client.page.orgunit.details;

import java.util.ArrayList;

import org.sigmah.client.CountriesList;
import org.sigmah.client.UsersList;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.orgunit.OrgUnitPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitDetailsDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.handler.ValueEvent;
import org.sigmah.shared.dto.element.handler.ValueHandler;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;

public class OrgUnitDetailsPresenter implements SubPresenter {

    /**
     * Description of the view managed by this presenter.
     */
    public static abstract class View extends ContentPanel {

        public abstract Button getSaveButton();

        public abstract ContentPanel getMainPanel();
    }

    /**
     * This presenter view.
     */
    private View view;

    /**
     * The dispatcher.
     */
    private final Dispatcher dispatcher;

    private final Authentication authentication;

    private final CountriesList countriesList;

    private final UsersList usersList;

    /**
     * The main presenter.
     */
    private final OrgUnitPresenter mainPresenter;

    /**
     * List of values changes.
     */
    private ArrayList<ValueEvent> valueChanges = new ArrayList<ValueEvent>();

    /**
     * The counter before the main panel is unmasked.
     */
    private int maskCount;

    public OrgUnitDetailsPresenter(Dispatcher dispatcher, Authentication authentication, OrgUnitPresenter mainPrsenter,
            CountriesList countriesList, UsersList usersList) {
        this.dispatcher = dispatcher;
        this.mainPresenter = mainPrsenter;
        this.authentication = authentication;
        this.countriesList = countriesList;
        this.usersList = usersList;
    }

    @Override
    public Component getView() {

        if (view == null) {
            view = new OrgUnitDetailsView();
            addListeners();
        }

        valueChanges.clear();
        view.getSaveButton().disable();

        load(mainPresenter.getCurrentOrgUnitDTO().getOrgUnitModel().getDetails());

        return view;
    }

    @Override
    public void discardView() {
        this.view = null;
    }

    @Override
    public void viewDidAppear() {
        // nothing to do.
    }

    /**
     * Initializes the presenter.
     */
    private void addListeners() {

        // Save action.
        view.getSaveButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {

                view.getSaveButton().disable();

                final UpdateProject updateProject = new UpdateProject(mainPresenter.getCurrentOrgUnitDTO().getId(),
                        valueChanges);

                dispatcher.execute(updateProject,
                        new MaskingAsyncMonitor(view.getMainPanel(), I18N.CONSTANTS.loading()),
                        new AsyncCallback<VoidResult>() {

                            @Override
                            public void onFailure(Throwable caught) {

                                MessageBox.alert(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError(), null);
                            }

                            @Override
                            public void onSuccess(VoidResult result) {

                                Notification.show(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.saveConfirm());

                                // Checks if there is any update needed to the
                                // local project instance.
                                boolean refreshBanner = false;
                                for (ValueEvent event : valueChanges) {
                                    if (event.getSource() instanceof DefaultFlexibleElementDTO) {
                                        updateCurrentProject(((DefaultFlexibleElementDTO) event.getSource()),
                                                event.getSingleValue());
                                        refreshBanner = true;
                                    }
                                }

                                valueChanges.clear();

                                if (refreshBanner) {
                                    mainPresenter.refreshBanner();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Loads the presenter with the org unit details.
     * 
     * @param details
     *            The details.
     */
    private void load(OrgUnitDetailsDTO details) {

        // Clear panel.
        view.getMainPanel().removeAll();

        // Layout.
        final LayoutDTO layout = details.getLayout();

        // Counts elements.
        int count = 0;
        for (final LayoutGroupDTO groupDTO : layout.getLayoutGroupsDTO()) {
            count += groupDTO.getLayoutConstraintsDTO().size();
        }

        // Executes layout.
        if (count != 0) {

            // Masks the main panel.
            mask(count);

            final Grid gridLayout = new Grid(layout.getRowsCount(), layout.getColumnsCount());
            gridLayout.setCellPadding(0);
            gridLayout.setCellSpacing(0);
            gridLayout.setWidth("100%");

            for (final LayoutGroupDTO groupLayout : layout.getLayoutGroupsDTO()) {

                // Creates the fieldset and positions it.
                final FieldSet formPanel = (FieldSet) groupLayout.getWidget();
                gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), formPanel);

                // For each constraint in the current layout group.
                if (groupLayout.getLayoutConstraintsDTO() != null) {
                    for (final LayoutConstraintDTO constraintDTO : groupLayout.getLayoutConstraintsDTO()) {

                        // Gets the element managed by this constraint.
                        final FlexibleElementDTO elementDTO = constraintDTO.getFlexibleElementDTO();

                        // --
                        // -- ELEMENT VALUE
                        // --

                        // Remote call to ask for this element value.
                        final GetValue command = new GetValue(mainPresenter.getCurrentOrgUnitDTO().getId(),
                                elementDTO.getId(), elementDTO.getEntityName());
                        dispatcher.execute(command, null, new AsyncCallback<ValueResult>() {

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.error("Error, element value not loaded.");
                                unmask();
                            }

                            @Override
                            public void onSuccess(ValueResult valueResult) {

                                if (Log.isDebugEnabled()) {
                                    Log.debug("Element value(s) object : " + valueResult);
                                }

                                // --
                                // -- ELEMENT COMPONENT
                                // --

                                // Configures the flexible element for the
                                // current application state before generating
                                // its component.
                                elementDTO.setService(dispatcher);
                                elementDTO.setAuthentication(authentication);
                                elementDTO.setCurrentContainerDTO(mainPresenter.getCurrentOrgUnitDTO());
                                elementDTO.setCountries(countriesList);
                                elementDTO.setUsers(usersList);
                                elementDTO.assignValue(valueResult);

                                // Generates element component (with the value).
                                elementDTO.init();
                                final Component elementComponent = elementDTO.getElementComponent(valueResult);

                                // Component width.
                                final FormData formData;
                                if (elementDTO.getPreferredWidth() == 0) {
                                    formData = new FormData("100%");
                                } else {
                                    formData = new FormData(elementDTO.getPreferredWidth(), -1);
                                }

                                if (elementComponent != null) {
                                    formPanel.add(elementComponent, formData);
                                }
                                formPanel.layout();

                                // --
                                // -- ELEMENT HANDLERS
                                // --

                                // Adds a value change handler to this element.
                                elementDTO.addValueHandler(new ValueHandlerImpl());

                                unmask();
                            }
                        });
                    }
                }
            }

            view.getMainPanel().add(gridLayout);
        }
        // Default details page.
        else {
            final Label l = new Label(I18N.CONSTANTS.projectDetailsNoDetails());
            l.addStyleName("project-label-10");
            view.getMainPanel().add(l);
        }

        view.layout();
    }

    /**
     * Mask the main panel and set the mask counter.
     * 
     * @param count
     *            The mask counter.
     */
    private void mask(int count) {

        if (count <= 0) {
            return;
        }

        maskCount = count;
        view.getMainPanel().mask(I18N.CONSTANTS.loading());
    }

    /**
     * Decrements the mask counter and unmask the main panel if the counter
     * reaches <code>0</code>.
     */
    private void unmask() {
        maskCount--;
        if (maskCount == 0) {
            view.getMainPanel().unmask();
        }
    }

    /**
     * Updates locally the DTO to avoid a remote server call.
     * 
     * @param element
     *            The default flexible element.
     * @param value
     *            The new value.
     */
    private void updateCurrentProject(DefaultFlexibleElementDTO element, String value) {

        final OrgUnitDTO currentOrgUnitDTO = mainPresenter.getCurrentOrgUnitDTO();

        switch (element.getType()) {
        case CODE:
            currentOrgUnitDTO.setName(value);
            break;
        case TITLE:
            currentOrgUnitDTO.setFullName(value);
            break;
        case BUDGET:
            try {

                final String[] budgets = value.split("\\|");
                final double plannedBudget = Double.parseDouble(budgets[0]);
                final double spendBudget = Double.parseDouble(budgets[1]);
                final double receivedBudget = Double.parseDouble(budgets[2]);

                currentOrgUnitDTO.setPlannedBudget(plannedBudget);
                currentOrgUnitDTO.setSpendBudget(spendBudget);
                currentOrgUnitDTO.setReceivedBudget(receivedBudget);

            } catch (Exception e) {
                // nothing, invalid budget.
            }
            break;
        default:
            // Nothing, non managed type.
            break;
        }
    }

    /**
     * Internal class handling the value changes of the flexible elements.
     */
    private class ValueHandlerImpl implements ValueHandler {

        @Override
        public void onValueChange(ValueEvent event) {

            // Stores the change to be saved later.
            valueChanges.add(event);
            // Enables the save action.
            view.getSaveButton().enable();
        }
    }
}
