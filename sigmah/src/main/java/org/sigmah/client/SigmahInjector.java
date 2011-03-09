/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.dispatch.remote.cache.AdminEntityCache;
import org.sigmah.client.dispatch.remote.cache.SchemaCache;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.admin.AdminModule;
import org.sigmah.client.page.admin.AdminPageLoader;
import org.sigmah.client.page.admin.AdminPresenter;
import org.sigmah.client.page.charts.ChartPagePresenter;
import org.sigmah.client.page.charts.SigmahChartLoader;
import org.sigmah.client.page.config.AccountEditor;
import org.sigmah.client.page.config.ConfigModule;
import org.sigmah.client.page.config.ConfigNavigator;
import org.sigmah.client.page.config.DbConfigPresenter;
import org.sigmah.client.page.config.DbListPage;
import org.sigmah.client.page.config.DbListPresenter;
import org.sigmah.client.page.config.DbPartnerEditor;
import org.sigmah.client.page.config.DbUserEditor;
import org.sigmah.client.page.config.SigmahConfigLoader;
import org.sigmah.client.page.config.design.DesignPresenter;
import org.sigmah.client.page.dashboard.DashboardModule;
import org.sigmah.client.page.dashboard.DashboardPageLoader;
import org.sigmah.client.page.dashboard.DashboardPresenter;
import org.sigmah.client.page.entry.ActivityFilterPanel;
import org.sigmah.client.page.entry.DataEntryLoader;
import org.sigmah.client.page.entry.EntryModule;
import org.sigmah.client.page.map.MapModule;
import org.sigmah.client.page.map.SigmahMapLoader;
import org.sigmah.client.page.orgunit.OrgUnitModule;
import org.sigmah.client.page.orgunit.OrgUnitPageLoader;
import org.sigmah.client.page.orgunit.OrgUnitPresenter;
import org.sigmah.client.page.project.ProjectModule;
import org.sigmah.client.page.project.ProjectPageLoader;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.report.ReportListPagePresenter;
import org.sigmah.client.page.report.ReportModule;
import org.sigmah.client.page.report.ReportPreviewPresenter;
import org.sigmah.client.page.report.SigmahReportLoader;
import org.sigmah.client.page.table.PivotModule;
import org.sigmah.client.page.table.PivotPresenter;
import org.sigmah.client.page.table.SigmahPivotPageLoader;
import org.sigmah.client.util.state.IStateManager;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({SigmahModule.class,
             DashboardModule.class,
             ProjectModule.class,
             OrgUnitModule.class,
             AdminModule.class,
             MapModule.class,
             PivotModule.class,
             ReportModule.class,
             EntryModule.class,
             ConfigModule.class})
public interface SigmahInjector extends Ginjector {
    Authentication getAuthentication();

    EventBus getEventBus();
    Dispatcher getService();
    NavigationHandler getNavigationHandler();
    HistoryManager getHistoryManager();
    
    // Pages from Sigmah
    DashboardPageLoader registerDashboardPageLoader();
//    ProjectListPageLoader registerProjectListPageLoader();
    ProjectPageLoader registerProjectPageLoader();
    
    // Required by the 'Dashboard' page
    DashboardPresenter getDashboardPresenter();
    
    // Required by the 'Project' page
    ProjectPresenter getProjectListPresenter();

    // Required by the 'Project' page
    ProjectPresenter getProjectPresenter();
    
    // Required by the 'Org unit' page
    OrgUnitPresenter getOrgUnitPresenter();
    OrgUnitPageLoader registerOrgUnitPageLoader();
    
    // Required by the 'Admin' page
    AdminPresenter getAdminPresenter();
    AdminPageLoader registerAdminPageLoader();
    
    // Pages from ActivityInfo
    DataEntryLoader registerDataEntryLoader();
    SigmahMapLoader registerMapLoader();
    SigmahChartLoader registerChartLoader();
    SigmahConfigLoader registerConfigLoader();
    SigmahPivotPageLoader registerPivotLoader();
    SigmahReportLoader registerReportLoader();
    
    // Required by the 'Data Entry' page
    ActivityFilterPanel getDataEntryNavigator();
    IStateManager getStateManager();
    UIConstants getMessages();
    
    // Required by the 'Charts' page
    ChartPagePresenter getCharter();
    
    // Required by the 'Report' page
    ReportPreviewPresenter getReportPreviewPresenter();
    ReportListPagePresenter getReportHomePresenter();
    
    // Required by the 'Config' page
    ConfigNavigator getConfigNavigator();
    AccountEditor getAccountEditor();
    DbListPresenter getDbListPresenter();
    DbUserEditor getDbUserEditor();
    DbPartnerEditor getDbPartnerEditor();
    DesignPresenter getDesigner();
    DbConfigPresenter getDbConfigPresenter();
    DbListPage getDbListPage();
    
    // Required by the 'Pivot' page
    PivotPresenter getPivotPresenter();
    
    // Cache
    SchemaCache createSchemaCache();
    AdminEntityCache createAdminCache();
}
