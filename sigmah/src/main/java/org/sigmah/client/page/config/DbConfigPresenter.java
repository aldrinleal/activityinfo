/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.common.GalleryView;
import org.sigmah.client.page.config.design.DesignPanelActivityInfo;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.google.inject.Inject;

public class DbConfigPresenter implements Page {

    private final GalleryView view;
    public static final PageId DatabaseConfig =  new PageId("db");

    @Inject
    public DbConfigPresenter(GalleryView view) {
        this.view = view;
    }

    public void go(UserDatabaseDTO db) {
        view.setHeading(db.getFullName() == null ? db.getName() : db.getFullName());

        if (db.isDesignAllowed()) {
            view.add(I18N.CONSTANTS.design(), I18N.CONSTANTS.designDescription(),
                    "db-design.png", new DbPageState(DesignPanelActivityInfo.PAGE_ID, db.getId()));
        }
        if (db.isManageAllUsersAllowed()) {
            view.add(I18N.CONSTANTS.partner(), I18N.CONSTANTS.partnerEditorDescription(),
                    "db-partners.png", new DbPageState(DbPartnerEditor.DatabasePartners, db.getId()));
        }
        if (db.isManageUsersAllowed()) {
            view.add(I18N.CONSTANTS.users(), I18N.CONSTANTS.userManagerDescription(),
                    "db-users.png", new DbPageState(DbUserEditor.DatabaseUsers, db.getId()));
        }

//        view.add("Cibles", "Définer les cibles pour les indicateurs.", "db-targets",
//                new DbPageState(Pages.DatabaseTargets, db.getId()));
    }

    @Override
    public PageId getPageId() {
        return DatabaseConfig;
    }

    @Override
    public Object getWidget() {
        return view;
    }

    @Override
    public void requestToNavigateAway(PageState place, NavigationCallback callback) {
        callback.onDecided(true);
    }

    @Override
    public String beforeWindowCloses() {
        return null;
    }

    @Override
    public boolean navigate(PageState place) {
        return false;
    }

    @Override
    public void shutdown() {

    }
}
