/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry.editor;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.widget.RemoteComboBox;
import org.sigmah.shared.dto.AdminEntityDTO;
import org.sigmah.shared.dto.AdminLevelDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFieldSet extends AbstractFieldSet implements AdminFieldSetPresenter.View  {

    private AdminFieldSetPresenter presenter;
    private Map<Integer, ComboBox<AdminEntityDTO>> comboBoxes =
            new HashMap<Integer, ComboBox<AdminEntityDTO>>();

    public AdminFieldSet(List<AdminLevelDTO> adminLevels) {
        super(I18N.CONSTANTS.location(), 100, 200);

        for(final AdminLevelDTO level : adminLevels) {
            final int levelId = level.getId();

            final ComboBox<AdminEntityDTO> comboBox = new RemoteComboBox<AdminEntityDTO>();
            comboBox.setFieldLabel(level.getName());
            comboBox.setStore(new ListStore<AdminEntityDTO>());
            comboBox.setTypeAhead(false);
           // comboBox.setQueryDelay(0);
            comboBox.setForceSelection(true);
            comboBox.setEditable(false);
            comboBox.setValueField("id");
            comboBox.setDisplayField("name");
            comboBox.setEnabled(false);
            comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);

            comboBox.addListener(Events.Select, new Listener<FieldEvent>() {
                @Override
                public void handleEvent(FieldEvent be) {
                    AdminEntityDTO selected = (AdminEntityDTO) be.getField().getValue();

                    presenter.onSelectionChanged(levelId, selected);

                }
            });
            comboBox.addListener(Events.BrowserEvent, new Listener<FieldEvent>() {
                public void handleEvent(FieldEvent be) {
                    if(be.getEventTypeInt() == Event.ONKEYUP) {
                        if(be.getEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                            comboBox.setValue(null);
                            presenter.onSelectionChanged(levelId, null);
                        }
                    }
                }
            });

            comboBoxes.put(levelId, comboBox);
            add(comboBox);
        }

    }

    private AdminEntityDTO findEntity(String name, List<AdminEntityDTO> models) {
        for(AdminEntityDTO entity : models) {
            if(entity.getName().equalsIgnoreCase(name)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void bindPresenter(AdminFieldSetPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setStore(int levelId, ListStore<AdminEntityDTO> store) {
        comboBoxes.get(levelId).setStore(store);
    }

    @Override
    public void setEnabled(int levelId, boolean enabled) {
        comboBoxes.get(levelId).setEnabled(enabled);
    }

    @Override
    public void setValue(int levelId, AdminEntityDTO value) {
        comboBoxes.get(levelId).setValue(value);
    }


    public ComboBox<AdminEntityDTO> getCombo(int levelId) {
        return comboBoxes.get(levelId);
    }
}
