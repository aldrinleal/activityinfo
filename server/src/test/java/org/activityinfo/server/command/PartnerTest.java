/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.command;

import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.shared.command.AddPartner;
import org.activityinfo.shared.command.GetSchema;
import org.activityinfo.shared.command.result.CreateResult;
import org.activityinfo.shared.dto.PartnerDTO;
import org.activityinfo.shared.dto.SchemaDTO;
import org.activityinfo.shared.exception.DuplicatePartnerException;
import org.activityinfo.test.InjectionSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class PartnerTest extends CommandTestCase {
    public static final int PEAR_PLUS_DB_ID = 2;
    public static final int SOL_ID = 2;


    @Test
    public void testAddPartner() throws Exception {
        PartnerDTO newPartner = new PartnerDTO();
        newPartner.setName("Solidarites");

        CreateResult cr = execute(new AddPartner(PEAR_PLUS_DB_ID, newPartner));

        Assert.assertEquals(SOL_ID, cr.getNewId());

        SchemaDTO schema = execute(new GetSchema());
        PartnerDTO partner = schema.getDatabaseById(PEAR_PLUS_DB_ID).getPartnerById(SOL_ID);

        Assert.assertNotNull(partner);
        Assert.assertEquals(newPartner.getName(), partner.getName());
    }

    @Test
    public void testAddNewPartner() throws Exception {
        PartnerDTO newPartner = new PartnerDTO();
        newPartner.setName("VDE");
        newPartner.setFullName("Vision d'Espoir");

        CreateResult cr = execute(new AddPartner(1, newPartner));


        SchemaDTO schema = execute(new GetSchema());
        PartnerDTO partner = schema.getDatabaseById(1).getPartnerById(cr.getNewId());

        Assert.assertNotNull(partner);
        Assert.assertEquals("VDE", partner.getName());
        Assert.assertEquals("Vision d'Espoir", partner.getFullName());

    }


    @Test(expected = DuplicatePartnerException.class)
    public void testAddDuplicatePartner() throws Exception {
        PartnerDTO newPartner = new PartnerDTO();
        newPartner.setName("NRC");
        newPartner.setFullName("Norweigen Refugee Committe");

        CreateResult cr = execute(new AddPartner(1, newPartner));
    }
}