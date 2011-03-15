/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;


import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.widget.MappingComboBox;
import org.sigmah.client.page.common.widget.MappingComboBoxBinding;
import org.sigmah.shared.dto.IndicatorDTO;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;

class IndicatorForm extends AbstractDesignForm {

    private FormBinding binding;
	private NumberField idField;
	private TextField<String> categoryField;

    public IndicatorForm()  {
        super();
        binding = new FormBinding(this);

        setScrollMode(Scroll.AUTOY);
        
        this.setLabelWidth(150);
        this.setFieldWidth(200);

        idField = new NumberField();
        idField.setFieldLabel("ID");
        idField.setReadOnly(true);
        binding.addFieldBinding(new FieldBinding(idField, "id"));
        add(idField);
        
        TextField<String> codeField = new TextField<String>();
        codeField.setFieldLabel(I18N.CONSTANTS.indicatorCode());
        binding.addFieldBinding(new FieldBinding(codeField,"code"));
        this.add(codeField);

        TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel(I18N.CONSTANTS.name());
        nameField.setAllowBlank(false);
        nameField.setMaxLength(128);
        binding.addFieldBinding(new FieldBinding(nameField, "name"));
        this.add(nameField);

        categoryField = new TextField<String>();
        categoryField.setName("category");
        categoryField.setFieldLabel(I18N.CONSTANTS.category());
        categoryField.setMaxLength(50);
        categoryField.setReadOnly(true);
        binding.addFieldBinding(new FieldBinding(categoryField, "category"));
        this.add(categoryField);

        TextField<String> unitsField = new TextField<String>();
        unitsField.setName("units");
        unitsField.setFieldLabel(I18N.CONSTANTS.units());
        unitsField.setAllowBlank(false);
        unitsField.setMaxLength(15);
        binding.addFieldBinding(new FieldBinding(unitsField, "units"));
        this.add(unitsField);
        
        NumberField objectiveField = new NumberField();
        objectiveField.setName("objective");
        objectiveField.setFieldLabel(I18N.CONSTANTS.objecive());
        binding.addFieldBinding(new FieldBinding(objectiveField, "objective"));
        this.add(objectiveField);

        MappingComboBox aggregationCombo = new MappingComboBox();
        aggregationCombo.setFieldLabel(I18N.CONSTANTS.aggregationMethod());
        aggregationCombo.add(IndicatorDTO.AGGREGATE_SUM, I18N.CONSTANTS.sum());
        aggregationCombo.add(IndicatorDTO.AGGREGATE_AVG, I18N.CONSTANTS.average());
        aggregationCombo.add(IndicatorDTO.AGGREGATE_SITE_COUNT, I18N.CONSTANTS.siteCount());
        binding.addFieldBinding(new MappingComboBoxBinding(aggregationCombo, "aggregation"));
        this.add(aggregationCombo);

        TextArea descField = new TextArea();
        descField.setFieldLabel(I18N.CONSTANTS.description());
        binding.addFieldBinding(new FieldBinding(descField, "description"));
        this.add(descField);
    }
    
    public void setIdVisible(boolean visible) {
    	idField.setVisible(visible);
    }
    
    public void setCategoryVisible(boolean visible) {
    	categoryField.setVisible(visible);
    }

    @Override
    public FormBinding getBinding() {
        return binding;
    }
}
