package org.activityinfo.client.page.entry.sitehistory;

import java.util.Map;

import org.activityinfo.client.i18n.I18N;
import org.activityinfo.shared.dto.AttributeDTO;
import org.activityinfo.shared.dto.IndicatorDTO;
import org.activityinfo.shared.dto.SchemaDTO;

class ItemDetail {
	private String stringValue;
	
	static ItemDetail create(RenderContext ctx, Map.Entry<String, Object> entry) {
		ItemDetail d = new ItemDetail();
		
		Map<String, Object> state = ctx.getState();
		SchemaDTO schema = ctx.getSchema();
		
		String key = entry.getKey();
		final Object oldValue = state.get(key);
		final Object newValue = entry.getValue();
		state.put(key, newValue);
		
		final StringBuilder sb = new StringBuilder();
		
		// basic
		if (key.equals("date1")) {
			addValues(sb, I18N.CONSTANTS.startDate(), oldValue, newValue);
		}

		else if (key.equals("date2")) {
			addValues(sb, I18N.CONSTANTS.endDate(), oldValue, newValue);
		}
		
		else if (key.equals("comments")) {
			addValues(sb, I18N.CONSTANTS.comments(), oldValue, newValue);
		}
		
		// schema lookups
		else if (key.equals("locationId")) {
			String oldName = null;
			if (oldValue != null) {
				oldName = ctx.getLocation(toInt(oldValue)).getName();
			}
			String newName = ctx.getLocation(toInt(newValue)).getName();
			addValues(sb, I18N.CONSTANTS.location(), oldName, newName);
		}
		
		else if (key.equals("projectId")) {
			String oldName = null;
			if (oldValue != null) {
				oldName = schema.getProjectById(toInt(oldValue)).getName();
			}
			String newName = schema.getProjectById(toInt(newValue)).getName();
			addValues(sb, I18N.CONSTANTS.project(), oldName, newName);
		}		

		else if (key.equals("partnerId")) {
			String oldName = null;
			if (oldValue != null) {
				oldName = schema.getPartnerById(toInt(oldValue)).getName();
			}
			String newName = schema.getPartnerById(toInt(newValue)).getName();
			addValues(sb, I18N.CONSTANTS.partner(), oldName, newName);
		}		

		// custom
		else if (key.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
			int id = IndicatorDTO.indicatorIdForPropertyName(key);
			IndicatorDTO dto = schema.getIndicatorById(id);
			addValues(sb, dto.getName(), oldValue, newValue, dto.getUnits());
		}
				
		else if (key.startsWith(AttributeDTO.PROPERTY_PREFIX)) {
			int id = AttributeDTO.idForPropertyName(key);
			AttributeDTO dto = schema.getAttributeById(id);
			if (Boolean.parseBoolean(newValue.toString())) {
				sb.append(I18N.MESSAGES.siteHistoryAttrAdd(dto.getName()));
			} else {
				sb.append(I18N.MESSAGES.siteHistoryAttrRemove(dto.getName()));
			}
		}
		
		// fallback
		else {
			addValues(sb, key, oldValue, newValue);
		}
		
		d.stringValue = sb.toString();
		
		return d;
	}

	private static void addValues(StringBuilder sb, String key, Object oldValue, Object newValue) {
		addValues(sb, key, oldValue, newValue, null);
	}
	
	private static void addValues(StringBuilder sb, String key, Object oldValue, Object newValue, String units) {
		sb.append(key);
		sb.append(": ");
		sb.append(newValue);
		
		if (units != null) {
			sb.append(" ");
			sb.append(units);
		}

		if (!equals(oldValue, newValue)) {
			sb.append(" (");
			if (oldValue == null) {
				sb.append(I18N.MESSAGES.siteHistoryOldValueBlank());
			} else {
				sb.append(I18N.MESSAGES.siteHistoryOldValue(oldValue));
			}
			sb.append(")");
		}
	}

	private static boolean equals(Object oldValue, Object newValue) {
        if (oldValue == newValue) {
            return true;
        }
        if ((oldValue == null) || (newValue == null)) {
            return false;
        }
        return oldValue.equals(newValue);
    }
    
    private static int toInt(Object val) {
    	return Integer.parseInt(val.toString());
    }

    @Override
    public String toString() {
    	return stringValue;
    }
}
