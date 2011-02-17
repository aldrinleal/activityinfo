/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.exception;

/**
 * @author Alex Bertram
 */
public class IllegalAccessCommandException extends UnexpectedCommandException {

    private static final long serialVersionUID = 4811925450225005973L;

    public IllegalAccessCommandException() {
    }

    public IllegalAccessCommandException(String message) {
        super(message);
    }
}
