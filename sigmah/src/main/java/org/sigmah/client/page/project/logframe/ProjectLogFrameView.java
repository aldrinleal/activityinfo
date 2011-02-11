package org.sigmah.client.page.project.logframe;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

/**
 * The view manipulated by the log frame presenter.
 * 
 * @author tmi
 * 
 */
public class ProjectLogFrameView extends ProjectLogFramePresenter.View {

    // Toolbar buttons.
    private Button saveButton;
    private Button copyButton;
    private Button pasteButton;
    private Button excelButton;
    private FormPanel excelForm;

    // Textboxes.
    private TextField<String> titleTextBox;
    private TextField<String> mainObjectiveTextBox;

    // Grid.
    private final ProjectLogFrameGrid logFrameGrid;

    /**
     * Builds the log frame main component.
     * 
     * @return The log frame main component.
     */
    public ProjectLogFrameView() {

        // Configuration
        VBoxLayout layout = new VBoxLayout() {
            @Override
            protected void onLayout(Container<?> container, El target) {
                super.onLayout(container, target);
                innerCt.addStyleName("logframe-body");
            }
        };
        layout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
        layout.setPadding(new Padding(0, 20, 0, 0));
        setLayout(layout);
        setHeaderVisible(true);
        setBorders(true);
        setHeading(I18N.CONSTANTS.logFrame());

        addStyleName("logframe-grid-main-panel");

        // setScrollMode(Scroll.AUTOY);

        // Title panel.
        final ContentPanel titlePanel = buildTitlePanel();

        // Main objective panel.
        final ContentPanel mainObjectivePanel = buildMainObjectivePanel();

        // Flex table.
        logFrameGrid = new ProjectLogFrameGrid();

        // Toolbar.
        final ToolBar toolBar = buildToolbar();

        setTopComponent(toolBar);
        add(titlePanel, new VBoxLayoutData(4, 8, 0, 8));
        add(mainObjectivePanel, new VBoxLayoutData(4, 8, 4, 8));
        add(logFrameGrid.getWidget(), new VBoxLayoutData(0, 8, 0, 8));
    }

    /**
     * Builds the log frame title panel.
     * 
     * @return The title panel.
     */
    private ContentPanel buildTitlePanel() {

        // Title label.
        final Label titleLabel = new Label(I18N.CONSTANTS.logFrameActionTitle());
        titleLabel.addStyleName("flexibility-element-label");
        titleLabel.setLabelFor("logFrameTitleBox-input");
        titleLabel.setWidth(100);

        // Title box.
        titleTextBox = new TextField<String>();
        titleTextBox.addStyleName("flexibility-text-field");
        titleTextBox.setId("logFrameTitleBox");

        // Title panel.
        final ContentPanel titlePanel = new ContentPanel();
        titlePanel.setBodyBorder(false);
        titlePanel.setHeaderVisible(false);
        titlePanel.setLayout(new HBoxLayout());

        titlePanel.add(titleLabel, new HBoxLayoutData(new Margins(4, 0, 0, 0)));
        final HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, 0, 5));
        flex.setFlex(1);
        titlePanel.add(titleTextBox, flex);

        return titlePanel;
    }

    /**
     * Builds the log frame main objective panel.
     * 
     * @return The main objective panel.
     */
    private ContentPanel buildMainObjectivePanel() {

        // Main objective label.
        final Label mainObjectiveLabel = new Label(I18N.CONSTANTS.logFrameMainObjective());
        mainObjectiveLabel.addStyleName("flexibility-element-label");
        mainObjectiveLabel.setLabelFor("logFrameMainObjectiveBox-input");
        mainObjectiveLabel.setWidth(100);

        // Main objective box.
        mainObjectiveTextBox = new TextField<String>();
        mainObjectiveTextBox.addStyleName("flexibility-text-field");
        mainObjectiveTextBox.setId("logFrameMainObjectiveBox");

        // Main objective panel.
        final ContentPanel mainObjectivePanel = new ContentPanel();
        mainObjectivePanel.setBodyBorder(false);
        mainObjectivePanel.setHeaderVisible(false);
        mainObjectivePanel.setLayout(new HBoxLayout());

        mainObjectivePanel.add(mainObjectiveLabel, new HBoxLayoutData(new Margins(4, 0, 0, 0)));
        final HBoxLayoutData flex2 = new HBoxLayoutData(new Margins(0, 0, 0, 5));
        flex2.setFlex(1);
        mainObjectivePanel.add(mainObjectiveTextBox, flex2);

        return mainObjectivePanel;
    }

    /**
     * Builds the actions toolbar.
     * 
     * @return The actions toolbar.
     */
    private ToolBar buildToolbar() {

        // Save button.
        saveButton = new Button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
        saveButton.setEnabled(false);

        // Copy button.
        copyButton = new Button(I18N.CONSTANTS.copy());
        copyButton.setEnabled(true);

        // Paste button.
        pasteButton = new Button(I18N.CONSTANTS.paste());
        pasteButton.setEnabled(false);

        // Export to Excel button.
        excelButton = new Button(I18N.CONSTANTS.exportToExcel(), IconImageBundle.ICONS.excel());

        // Export form.
        excelForm = new FormPanel();
        excelForm.setBodyBorder(false);
        excelForm.setHeaderVisible(false);
        excelForm.setPadding(0);
        excelForm.setEncoding(Encoding.URLENCODED);
        excelForm.setMethod(Method.POST);
        excelForm.setAction(GWT.getModuleBaseURL() + "export");

        // Actions toolbar.
        final ToolBar toolBar = new ToolBar();
        toolBar.setAlignment(HorizontalAlignment.RIGHT);
        toolBar.setBorders(false);

        toolBar.add(saveButton);
        toolBar.add(copyButton);
        toolBar.add(pasteButton);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(excelButton);
        toolBar.add(excelForm);

        return toolBar;
    }

    @Override
    public ProjectLogFrameGrid getLogFrameGrid() {
        return logFrameGrid;
    }

    @Override
    public Button getSaveButton() {
        return saveButton;
    }

    @Override
    public Button getCopyButton() {
        return copyButton;
    }

    @Override
    public Button getPasteButton() {
        return pasteButton;
    }

    @Override
    public Button getExcelExportButton() {
        return excelButton;
    }

    @Override
    public FormPanel getExcelExportForm() {
        return excelForm;
    }

    @Override
    public TextField<String> getLogFrameTitleTextBox() {
        return titleTextBox;
    }

    @Override
    public TextField<String> getLogFrameMainObjectiveTextBox() {
        return mainObjectiveTextBox;
    }
}
