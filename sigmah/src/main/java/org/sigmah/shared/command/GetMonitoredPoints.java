/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.shared.command.result.MonitoredPointsResultList;

/**
 * Request to retrieve the monitored points of every project available to the current user.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetMonitoredPoints implements Command<MonitoredPointsResultList> {
    private static final long serialVersionUID = 1L;
}
