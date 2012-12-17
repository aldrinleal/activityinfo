package org.activityinfo.shared.command;

import java.util.ArrayList;
import java.util.List;

import org.activityinfo.shared.command.GetLocations.GetLocationsResult;
import org.activityinfo.shared.command.result.CommandResult;
import org.activityinfo.shared.dto.LocationDTO;

import com.google.common.collect.Lists;

public class GetLocations implements Command<GetLocationsResult> {
	private List<Integer> locationIds;

	public GetLocations() {
		 locationIds = new ArrayList<Integer>();
	}
	
	public GetLocations(int id) {
		locationIds = new ArrayList<Integer>();
		locationIds.add(id);
	}

	public GetLocations(List<Integer> ids) {
		locationIds = ids;
	}

	public int getLocationId() {
		return locationIds.get(0);
	}

	public List<Integer> getLocationIds() {
		return locationIds;
	}

	public void setLocationId(int locationId) {
		locationIds = new ArrayList<Integer>();
		locationIds.add(locationId);
	}
	
	public void setLocationIds(List<Integer> ids) {
		locationIds = ids;
	}
	
	public static class GetLocationsResult implements CommandResult {
		private List<LocationDTO> locations = Lists.newArrayList();

		public GetLocationsResult() {
		}

		public GetLocationsResult(List<LocationDTO> locations) {
			if (locations != null) {
				this.locations = locations;
			}
		}

		public List<LocationDTO> getLocations() {
			return locations;
		}
		
		public LocationDTO getLocation() {
			return locations.get(0);
		}
	}
}
