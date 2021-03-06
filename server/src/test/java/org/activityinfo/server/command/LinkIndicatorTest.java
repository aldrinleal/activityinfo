package org.activityinfo.server.command;

import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.shared.command.GetSchema;
import org.activityinfo.shared.dto.SchemaDTO;
import org.activityinfo.shared.dto.UserDatabaseDTO;
import org.activityinfo.shared.exception.CommandException;
import org.activityinfo.test.InjectionSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/schema1.db.xml")
public class LinkIndicatorTest  extends CommandTestCase {

	private static final int DATABASE_OWNER = 1;
	private static UserDatabaseDTO db;
	
	@Before
	public void setUser() {
		 setUser(DATABASE_OWNER);
		 	/*
			 * Initial data load
			 */

			SchemaDTO schema = execute(new GetSchema());
			db = schema.getDatabaseById(1);
	}

	@Test
	public void testLinkIndicators() throws CommandException {
		
//		TODO test this action
//		UpdateIndicatorLink
	}

}
