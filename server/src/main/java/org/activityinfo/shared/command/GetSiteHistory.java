package org.activityinfo.shared.command;

import java.util.List;

import org.activityinfo.shared.command.GetSiteHistory.GetSiteHistoryResult;
import org.activityinfo.shared.command.result.CommandResult;
import org.activityinfo.shared.dto.SiteHistoryDTO;

import com.google.common.collect.Lists;

public class GetSiteHistory implements Command<GetSiteHistoryResult> {
	private static final long serialVersionUID = 1475811548735657666L;
	
	private int siteId;

	public GetSiteHistory() {
	}
	
	public GetSiteHistory(int siteId) {
		this.siteId = siteId;
	}
	
	public int getSiteId() {
		return siteId;
	}
	
	public static class GetSiteHistoryResult implements CommandResult {
		private static final long serialVersionUID = -3044469805146309286L;
		
		private List<SiteHistoryDTO> siteHistories = Lists.newArrayList();

		public GetSiteHistoryResult() {
		}

		public GetSiteHistoryResult(List<SiteHistoryDTO> siteHistories) {
			this.siteHistories = siteHistories;
		}

		public List<SiteHistoryDTO> getSiteHistories() {
			return siteHistories;
		}
	}
}
