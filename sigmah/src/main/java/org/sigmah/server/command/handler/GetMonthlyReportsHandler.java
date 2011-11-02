/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.command.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.sigmah.server.database.hibernate.entity.Indicator;
import org.sigmah.server.database.hibernate.entity.IndicatorValue;
import org.sigmah.server.database.hibernate.entity.ReportingPeriod;
import org.sigmah.server.database.hibernate.entity.User;
import org.sigmah.shared.command.GetMonthlyReports;
import org.sigmah.shared.command.Month;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.MonthlyReportResult;
import org.sigmah.shared.dto.IndicatorRowDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * See GetMonthlyReports
 *
 * @author Alex Bertram
 */
public class GetMonthlyReportsHandler implements CommandHandler<GetMonthlyReports> {


    private final EntityManager em;

    @Inject
    public GetMonthlyReportsHandler(EntityManager em) {
        this.em = em;
    }

    public CommandResult execute(GetMonthlyReports cmd, User user) throws CommandException {


        List<ReportingPeriod> periods = em.createQuery("select p from ReportingPeriod p where p.site.id = ?1")
                .setParameter(1, cmd.getSiteId())
                .getResultList();

        List<Indicator> indicators = em.createQuery("select i from Indicator i where i.activity.id =" +
                "(select s.activity.id from Site s where s.id = ?1)")
                .setParameter(1, cmd.getSiteId())
                .getResultList();


        List<IndicatorRowDTO> list = new ArrayList<IndicatorRowDTO>();

        for (Indicator indicator : indicators) {

            IndicatorRowDTO dto = new IndicatorRowDTO();
            dto.setIndicatorId(indicator.getId());
            dto.setSiteId(cmd.getSiteId());
            dto.setIndicatorName(indicator.getName());

            for (ReportingPeriod period : periods) {

                Month month = HandlerUtil.monthFromRange(period.getDate1(), period.getDate2());
                if (month != null &&
                        month.compareTo(cmd.getStartMonth()) >= 0 &&
                        month.compareTo(cmd.getEndMonth()) <= 0) {

                    for (IndicatorValue value : period.getIndicatorValues()) {
                        if (value.getIndicator().getId() == indicator.getId()) {
                            dto.setValue(month, value.getValue());
                        }
                    }
                }
            }

            list.add(dto);
        }

        return new MonthlyReportResult(list);

    }

}