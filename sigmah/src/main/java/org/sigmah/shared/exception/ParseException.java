/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.exception;

/**
 * @author Alex Bertram
 */
public class ParseException extends CommandException {

    private static final long serialVersionUID = -1785738366482269376L;

    public ParseException(String message) {
        super(message);
    }
}
