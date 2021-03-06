package org.activityinfo.client.report.editor.map.layerOptions;

import org.activityinfo.client.i18n.I18N;
import org.activityinfo.shared.report.model.layers.PolygonMapLayer;

import com.extjs.gxt.ui.client.event.ColorPaletteEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ColorPalette;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.common.base.Objects;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class PolygonLayerOptions extends LayoutContainer implements LayerOptionsWidget<PolygonMapLayer> {
		private PolygonMapLayer layer;
		private ColorPalette colorPicker = new ColorPalette();
		
		public PolygonLayerOptions() {
			super();
			setStyleAttribute("padding", "5px");
			createColorPicker();
		}

		private void createColorPicker() {
			colorPicker.setValue("000000");
			
			// Set the selected color to the maplayer
			colorPicker.addListener(Events.Select, new Listener<ColorPaletteEvent>() {
				@Override
				public void handleEvent(ColorPaletteEvent be) {
					if(!Objects.equal(layer.getMaxColor(), colorPicker.getValue())) {
						layer.setMaxColor(colorPicker.getValue());
						ValueChangeEvent.fire(PolygonLayerOptions.this, layer);
					}
			}});

			LabelField labelColor = new LabelField(I18N.CONSTANTS.color());
			add(labelColor);
			add(colorPicker);
		}
		

		@Override
		public PolygonMapLayer getValue() {
			return layer;
		}

		private void updateUI() {
			colorPicker.setValue(layer.getMaxColor());
		}
		
		// TODO: fireevent
		@Override
		public void setValue(PolygonMapLayer value, boolean fireEvents) {
			setValue(value);
		}
		 
		@Override
		public void setValue(PolygonMapLayer value) {
			this.layer=value;
			updateUI();
		}
		
		@Override
		public HandlerRegistration addValueChangeHandler(
				ValueChangeHandler<PolygonMapLayer> handler) {
			return this.addHandler(handler, ValueChangeEvent.getType());
		}
	}
