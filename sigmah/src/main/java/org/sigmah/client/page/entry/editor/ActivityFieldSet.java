/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry.editor;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.PartnerDTO;

import com.extjs.gxt.ui.client.binding.Bindings;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class ActivityFieldSet extends AbstractFieldSet {
    private DateField dateField1;
    private DateField dateField2;

    public ActivityFieldSet(ActivityDTO activity, ListStore<PartnerDTO> partnerStore) {
        super(I18N.CONSTANTS.activity(), 100, 200);

        TextField<String> databaseField = new TextField<String>();
		databaseField.setValue(activity.getDatabase().getName());
		databaseField.setFieldLabel(I18N.CONSTANTS.database());
		databaseField.setReadOnly(true);
		add(databaseField);

		TextField<String> activityField = new TextField<String>();
		activityField.setValue(activity.getName());
		activityField.setFieldLabel(I18N.CONSTANTS.activity());
		activityField.setReadOnly(true);
		add(activityField);


		ComboBox<PartnerDTO> partnerCombo = new ComboBox<PartnerDTO>();
		partnerCombo.setName("partner");
		partnerCombo.setDisplayField("name");
		partnerCombo.setEditable(false);
		partnerCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
		partnerCombo.setStore(partnerStore);
		partnerCombo.setFieldLabel(I18N.CONSTANTS.partner());
		partnerCombo.setForceSelection(true);
		partnerCombo.setAllowBlank(false);
		add(partnerCombo);

		if(activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
			
            dateField1 = new DateField();
			dateField1.setName("date1");
			dateField1.setAllowBlank(false);
			dateField1.setFieldLabel(I18N.CONSTANTS.startDate());
			add(dateField1);

            dateField2 = new DateField();
			dateField2.setName("date2");
			dateField2.setAllowBlank(false);
			dateField2.setFieldLabel(I18N.CONSTANTS.endDate());
            dateField2.setValidator(new Validator() {
                public String validate(Field<?> field, String value) {
                    if(dateField1.getValue()!=null && dateField2.getValue()!=null) {
                        if(dateField2.getValue().before(dateField1.getValue())) {
                            return I18N.CONSTANTS.inconsistentDateRangeWarning();
                        }
                    }
                    return null;
                }
            });
			add(dateField2);

		}
    }

}
