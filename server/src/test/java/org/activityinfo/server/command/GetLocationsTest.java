package org.activityinfo.server.command;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.shared.command.GetLocations;
import org.activityinfo.shared.dto.LocationDTO;
import org.activityinfo.test.InjectionSupport;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GetLocationsTest extends CommandTestCase2 {

	@Test
	public void getLocation() {
		
		setUser(1);
		
		LocationDTO location = execute(new GetLocations(1)).getLocation();
		
		assertThat(location, notNullValue());
		assertThat(location.getName(), equalTo("Penekusu Kivu"));
		assertThat(location.getAxe(), nullValue());
		assertThat(location.getAdminEntity(1).getName(), equalTo("Sud Kivu"));
		assertThat(location.getAdminEntity(2).getName(), equalTo("Shabunda"));
				
	}
	
}
