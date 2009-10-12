package org.activityinfo.server.report.generator;

import org.activityinfo.server.dao.hibernate.PivotDAO;
import org.activityinfo.server.domain.User;
import org.activityinfo.shared.report.content.PivotTableData;
import org.activityinfo.shared.report.model.*;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class PivotTableGeneratorTest {


    @Test
    public void test2x2() {

        // test input data: user

        User user = new User();
        user.setLocale("fr");


        // test input data: PivotTableElement

        PivotTableElement element = new PivotTableElement();
        AdminDimension provinceDim = new AdminDimension(1);
        element.addRowDimension(provinceDim);
        Dimension partnerDim = new Dimension(DimensionType.Partner);
        element.addColDimension(partnerDim);

        // test input data: aggregated results
        List<PivotDAO.Bucket> buckets = new ArrayList<PivotDAO.Bucket>();

        buckets.add(new PivotDAO.Bucket(433, provinceDim.category(2, "Sud Kivu"), partnerDim.category(1, "IRC")));
        buckets.add(new PivotDAO.Bucket(1032, provinceDim.category(1, "Nord Kivu"), partnerDim.category(2, "Solidarites")));
        buckets.add(new PivotDAO.Bucket(310, provinceDim.category(1, "Nord Kivu"), partnerDim.category(1, "IRC")));
        buckets.add(new PivotDAO.Bucket(926, provinceDim.category(1, "Nord Kivu"), partnerDim.category(3, "AVSI")));

        // collaborator : PivotDAO

        PivotDAO dao = createMock(PivotDAO.class);
        expect(dao.aggregate(isA(Filter.class), EasyMock.<Set<Dimension>>anyObject())).andReturn(buckets);
        replay(dao);

        // CLASS UNDER TEST!!

        PivotTableGenerator generator = new PivotTableGenerator(dao);

        generator.generate(user, element, null, null);

        Assert.assertNotNull("element content", element.getContent());

        PivotTableData data = element.getContent().getData();
        Assert.assertEquals("rows", 2,  data.getRootRow().getChildCount());
        Assert.assertEquals("rows sorted", "Nord Kivu", data.getRootRow().getChildren().get(0).getLabel());
        Assert.assertEquals("cols", 3, data.getRootColumn().getChildCount());
        

    }

}
