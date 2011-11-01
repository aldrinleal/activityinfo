/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import static java.util.Collections.singleton;
import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.resetToDefault;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.mock.DispatcherStub;
import org.sigmah.client.mock.DummyData;
import org.sigmah.client.mock.MockEventBus;
import org.sigmah.client.mock.StateManagerStub;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.entry.place.ActivityDataEntryPlace;
import org.sigmah.client.page.entry.place.DataEntryPlace;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.UpdateSite;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.report.model.DimensionType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.store.Record;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class SiteGridTest {


    @Before
    public void setUp() {

    }


    @Test
    public void testLoader() {

        SchemaDTO schema = DummyData.PEAR();

        // collaborator: service
        DispatcherStub service = new DispatcherStub();
        service.setResult(new GetSchema(), schema);
        service.setResult(GetSites.class, DummyData.PEAR_Sites_Result());

        // collaborator: eventbus
        MockEventBus eventBus = new MockEventBus();

        // collaborator: view
        SiteEditor.View view = createNiceMock(SiteEditor.View.class);
        replay(view);

        // Class Under Test !!
        SiteEditor presenter = new SiteEditor(eventBus, service,
                new StateManagerStub(), view);

        // VERIFY that command is correct

        presenter.go(new ActivityDataEntryPlace(91), schema.getActivityById(91));

        GetSites cmd = service.getLastExecuted(GetSites.class);
        assertThat(cmd.getFilter().getRestrictions(DimensionType.Activity), hasSingleValueEqualTo(91));

        // VERIFY that rows are loaded

//        Assert.assertEquals("number of rows", 2, presenter.getStore().getCount());

    }

    private <T> Matcher<Set<T>> hasSingleValueEqualTo(final T value) {
        return new TypeSafeMatcher<Set<T>>() {
            @Override
            public boolean matchesSafely(Set<T> item) {
                return item.size() == 1 && item.iterator().next().equals(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("single value equal to ");
                description.appendValue(value);
            }
        };
    }


    @Test
    public void testPartnerLevelPermissions() {

        SchemaDTO schema = DummyData.PEAR();

        // collaborator: service
        DispatcherStub service = new DispatcherStub();
        service.setResult(new GetSchema(), schema);
        service.setResult(GetSites.class, DummyData.PEAR_Sites_Result());

        // collaborator: eventbus
        MockEventBus eventBus = new MockEventBus();


        // collaborator: view
        SiteEditor.View view = createNiceMock("view", SiteEditor.View.class);
        // on load, we have permission to add, but since nothing
        // is yet selected, edit+delete should be set to false
        view.setActionEnabled(UIActions.add, true);
        view.setActionEnabled(UIActions.delete, false);
        view.setActionEnabled(UIActions.edit, false);

        replay(view);

        // Class Under Test !
        SiteEditor presenter = new SiteEditor(eventBus, service, new StateManagerStub(), view);

        // navigate to NFI data entry
        // verify that the appropriate actions were enabled correctly
        presenter.go(new ActivityDataEntryPlace(91), schema.getActivityById(91));

        verify(view);

        // mock select the first site from NRC, which we have the right to modify

        reset(view);
        view.setActionEnabled(UIActions.edit, true);
        view.setActionEnabled(UIActions.delete, true);
        replay(view);

        presenter.onSelectionChanged(DummyData.PEAR_Sites().get(4));

//        Record record = presenter.getStore().getRecord(presenter.getStore().getAt(0));
//        Assert.assertTrue(presenter.beforeEdit(record, "foobar"));

        verify(view);

        // mock select the second site from AVSI, which we do not
        reset(view);
        view.setActionEnabled(UIActions.edit, false);
        view.setActionEnabled(UIActions.delete, false);
        replay(view);

//        record = presenter.getStore().getRecord(presenter.getStore().getAt(0));
//        Assert.assertTrue(presenter.beforeEdit(record, "foobar"));

        presenter.onSelectionChanged(DummyData.PEAR_Sites().get(5));

        verify(view);

    }


    @Test
    public void testExportButtonEnabled() {
        SchemaDTO schema = DummyData.PEAR();

        // collaborator: service
        DispatcherStub service = new DispatcherStub();
        service.setResult(new GetSchema(), schema);
        service.setResult(GetSites.class, DummyData.PEAR_Sites_Result());

        // collaborator: eventbus
        MockEventBus eventBus = new MockEventBus();

        // collaborator: view
        // we expect the export button to be initially disabled
        SiteEditor.View view = createNiceMock("view", SiteEditor.View.class);
        view.setActionEnabled(UIActions.export, false);
        replay(view);

        // Class Under Test !!

        SiteEditor presenter = new SiteEditor(eventBus, service,
                new StateManagerStub(), view);

        // VERIFY that the button is enabled on load
        reset(view);
        view.setActionEnabled(UIActions.export, true);
        replay(view);

        presenter.go(new ActivityDataEntryPlace(91), schema.getActivityById(91));

        verify(view);
    }

    @Test
    public void testExportButtonDisabled() {
        SchemaDTO schema = DummyData.PEAR();

        // collaborator: service
        DispatcherStub service = new DispatcherStub();
        service.setResult(new GetSchema(), schema);
        service.setResult(GetSites.class, new SiteResult());

        // collaborator: eventbus
        MockEventBus eventBus = new MockEventBus();

        // collaborator: view
        // we expect the export button to be initially disabled
        SiteEditor.View view = createNiceMock("view", SiteEditor.View.class);
        view.setActionEnabled(UIActions.export, false);
        replay(view);

        // Class Under Test !!

        SiteEditor presenter = new SiteEditor(eventBus, service,
                new StateManagerStub(), view);

        // VERIFY that the export is not enabled

        presenter.go(new ActivityDataEntryPlace(91), schema.getActivityById(91));

        verify(view);
    }


    @Test
    public void testNavigation() {

        final SchemaDTO schema = DummyData.PEAR();

        // collaborator: eventBus
        EventBus eventBus = new MockEventBus();

        // collaborator: service
        DispatcherStub service = new DispatcherStub();
        service.setResult(new GetSchema(), schema);
        service.setResult(GetSites.class, new SiteResult(Collections.<SiteDTO>emptyList()));

        // collaborator: view
        SiteEditor.View view = createNiceMock(SiteEditor.View.class);
        replay(view);

        // Class under test !
        SiteEditor presenter = new SiteEditor(eventBus, service, new StateManagerStub(), view);


        // VERIFY that a navigation request result in command load

        DataEntryPlace firstPlace = new ActivityDataEntryPlace(schema.getActivityById(91));
        firstPlace.setPageNum(2);
        firstPlace.setSortInfo(new SortInfo("location", Style.SortDir.DESC));

        presenter.go(firstPlace, schema.getActivityById(91));

        service.assertExecuteCount(GetSites.class, 1);

        GetSites cmd = service.getLastExecuted(GetSites.class);
        Assert.assertEquals(singleton(91), cmd.getFilter().getRestrictions(DimensionType.Activity));
        Assert.assertEquals("location", cmd.getSortInfo().getSortField());
        Assert.assertEquals(Style.SortDir.DESC, cmd.getSortInfo().getSortDir());
        Assert.assertEquals(SiteEditor.PAGE_SIZE, cmd.getOffset());

        service.resetLog();


        // VERIFY that a second, identical navigation doesn't generate a reload

        presenter.navigate(firstPlace);

        service.assertExecuteCount(GetSites.class, 0);


        // verify that a navigation request for a different activity returns false

        Assert.assertFalse(presenter.navigate(new ActivityDataEntryPlace(92)));

    }

    @Test
    public void testNewSiteTriggersSeekToPage() {

        // Collaborator: service
        DispatcherStub service = new DispatcherStub();
        service.setResult(new GetSchema(), DummyData.PEAR());
        service.setResult(GetSites.class, DummyData.PEAR_Sites_Many_Results(100));

        // Collaborator: eventbus
        MockEventBus eventBus = new MockEventBus();

        // Collaborator: View
        SiteEditor.View view = createNiceMock(SiteEditor.View.class);
        replay(view);

        // CLASS UNDER Test
        SiteEditor presenter = new SiteEditor(eventBus, service, new StateManagerStub(),
                view);

        // VERIFY that a new site triggers the correct command AND
        //        that the site is selected upon load

        presenter.go(new ActivityDataEntryPlace(91), DummyData.PEAR().getActivityById(91));

        resetToDefault(view);
        view.setSelection(3);
        view.setActionEnabled((String) anyObject(), anyBoolean());
        expectLastCall().anyTimes();
        replay(view);

        service.setResult(GetSites.class, new SiteResult(new SiteDTO(3)));

        eventBus.fireEvent(new SiteEvent(AppEvents.SiteCreated, this, new SiteDTO(3)));

        GetSites cmd = service.getLastExecuted(GetSites.class);
        Assert.assertEquals(3, cmd.getSeekToSiteId().intValue());

        verify(view);

    }

    @Test
    public void testSiteUpdate() {

        // Collaborator: service
        DispatcherStub service = new DispatcherStub();
        service.setResult(new GetSchema(), DummyData.PEAR());
        service.setResult(GetSites.class, DummyData.PEAR_Sites_Result());

        // Collaborator: eventbus  
        MockEventBus eventBus = new MockEventBus();

        // Collaborator: View
        SiteEditor.View view = createNiceMock(SiteEditor.View.class);
        replay(view);

        // CLASS UNDER TEST
        SchemaDTO schema = DummyData.PEAR();
        SiteEditor presenter = new SiteEditor(eventBus, service, new StateManagerStub(),
                view);

        presenter.go(new ActivityDataEntryPlace(91), schema.getActivityById(91));

        //VERIFY that an external chnage is propageted to the store
        SiteDTO site = DummyData.PEAR_Sites().get(4);
        site.setLocationName("Freeport");

        eventBus.fireEvent(new SiteEvent(AppEvents.SiteChanged, this, site));

        SiteDTO stored = presenter.getStore().findModel("id", site.getId());
        Assert.assertEquals("Freeport", stored.get("locationName"));

    }

    @Test
    public void testSave() {

        // Dummy data
        SchemaDTO schema = DummyData.PEAR();
        SiteResult sites = DummyData.PEAR_Sites_Result();

        // Collaborator: service
        DispatcherStub service = new DispatcherStub();
        service.setResult(GetSites.class, sites);
        service.setResult(UpdateSite.class, new VoidResult());

        // Collaborator: View
        SiteEditor.View view = createNiceMock(SiteEditor.View.class);
        replay(view);

        // CLASS UNDER TEST
        SiteEditor editor = new SiteEditor(new MockEventBus(), service, new StateManagerStub(),
                view);

        editor.go(new ActivityDataEntryPlace(91), schema.getActivityById(91));


        //VERIFY that an inline change is results in an update entity call

        Record record = editor.getStore().getRecord(sites.getData().get(0));
        record.set("activityId", 3);

        editor.onUIAction(UIActions.save);

        UpdateSite cmd = service.getLastExecuted(UpdateSite.class);
        Assert.assertEquals(sites.getData().get(0).getId(), cmd.getSiteId());
        Assert.assertEquals(3, cmd.getChanges().get("activityId"));
    }

}
