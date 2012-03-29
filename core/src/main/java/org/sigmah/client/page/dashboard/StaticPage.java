/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.dashboard;

import org.sigmah.client.page.Frames;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;

import com.extjs.gxt.ui.client.widget.ContentPanel;

public class StaticPage extends ContentPanel implements Page {

    public StaticPage() {
        this.setHeaderVisible(false);
    }

    public void navigate(StaticPageState place) {
        this.setUrl("static/" + place.getKeyword() + ".html");
    }

    public PageId getPageId() {
        return Frames.STATIC;
    }

    public Object getWidget() {
        return this;
    }

    public void requestToNavigateAway(PageState place, NavigationCallback callback) {
        callback.onDecided(true);
    }

    public String beforeWindowCloses() {
        return null;
    }

    public void shutdown() {

    }

    public boolean navigate(PageState place) {
        return false;
    }
}