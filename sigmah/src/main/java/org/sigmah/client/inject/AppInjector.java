/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

/**
 * Support classes for the Dependency Injection Framework, grace a Gin
 */
package org.sigmah.client.inject;

import org.sigmah.client.EventBus;
import org.sigmah.client.HistoryManager;
import org.sigmah.client.UsageTracker;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.dispatch.remote.cache.AdminEntityCache;
import org.sigmah.client.dispatch.remote.cache.SchemaCache;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.offline.OfflineModule;
import org.sigmah.client.offline.ui.OfflinePresenter;
import org.sigmah.client.page.DownloadManager;
import org.sigmah.client.page.charts.ChartLoader;
import org.sigmah.client.page.config.AccountEditor;
import org.sigmah.client.page.config.ConfigLoader;
import org.sigmah.client.page.config.ConfigModule;
import org.sigmah.client.page.config.ConfigNavigator;
import org.sigmah.client.page.config.DbConfigPresenter;
import org.sigmah.client.page.config.DbListPage;
import org.sigmah.client.page.config.DbPartnerEditor;
import org.sigmah.client.page.config.DbUserEditor;
import org.sigmah.client.page.config.design.DesignPanel;
import org.sigmah.client.page.config.design.DesignPanelActivityInfo;
import org.sigmah.client.page.entry.DataEntryLoader;
import org.sigmah.client.page.entry.ActivityFilterPanel;
import org.sigmah.client.page.entry.DataEntryModule;
import org.sigmah.client.page.map.MapLoader;
import org.sigmah.client.page.map.MapModule;
import org.sigmah.client.page.report.ReportListPagePresenter;
import org.sigmah.client.page.report.ReportLoader;
import org.sigmah.client.page.report.ReportModule;
import org.sigmah.client.page.report.ReportPreviewPresenter;
import org.sigmah.client.page.table.PivotModule;
import org.sigmah.client.page.table.PivotPageLoader;
import org.sigmah.client.page.table.PivotPresenter;
import org.sigmah.client.page.welcome.Welcome;
import org.sigmah.client.page.welcome.WelcomeLoader;
import org.sigmah.client.util.state.IStateManager;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * GIN injector.
 *
 * TODO: having this number of explicit entries is probably not ideal, try to make better use
 * of injection and injecting Provider<>s
 */
@GinModules({
        AppModule.class,
        ReportModule.class,
        DataEntryModule.class,
        MapModule.class,
        PivotModule.class,
        ConfigModule.class,
        OfflineModule.class
})
public interface AppInjector extends Ginjector {

    EventBus getEventBus();

    Dispatcher getService();

    Welcome getWelcomePage();

    IStateManager getStateManager();

    HistoryManager getHistoryManager();

    ActivityFilterPanel getDataEntryNavigator();

    ReportListPagePresenter getReportHomePresenter();

    ConfigNavigator getConfigNavigator();

    AccountEditor getAccountEditor();

    DbUserEditor getDbUserEditor();

    DbPartnerEditor getDbPartnerEditor();

    DesignPanel getDesigner();
    
    DesignPanelActivityInfo getDesignerActivityInfo();

    WelcomeLoader createWelcomeLoader();

    DataEntryLoader createDataEntryLoader();

    MapLoader createMapLoader();

    ChartLoader createChartLoader();

    ReportLoader createReportLoader();

    ConfigLoader createConfigLoader();

    PivotPageLoader createPivotLoader();

    PivotPresenter getPivotPresenter();

    UIConstants getMessages();

    ReportPreviewPresenter getReportPreviewPresenter();

    OfflinePresenter createOfflineManager();

    SchemaCache createSchemaCache();

    AdminEntityCache createAdminCache();

    DownloadManager getDownloadManager();

    DbConfigPresenter getDbConfigPresenter();

    UsageTracker getUsageTracker();

    DbListPage getDbListPage();

    Authentication getAuthentication();
}
