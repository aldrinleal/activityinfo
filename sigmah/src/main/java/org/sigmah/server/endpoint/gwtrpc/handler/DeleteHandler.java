/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.Date;

import javax.persistence.EntityManager;

import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.Deleteable;
import org.sigmah.shared.domain.ReallyDeleteable;
import org.sigmah.shared.domain.Site;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;

import com.google.inject.Inject;

/**
 * @author Alex Bertram
 * @see org.sigmah.shared.command.Delete
 * @see org.sigmah.shared.domain.Deleteable
 */
public class DeleteHandler implements CommandHandler<Delete> {
    private EntityManager em;

    @Inject
    public DeleteHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(Delete cmd, User user) {

        // TODO check permissions for delete!
        // These handler should redirect to one of the Entity policy classes.
        Class entityClass = entityClassForEntityName(cmd.getEntityName());
		Object entity = em.find(entityClass, cmd.getId());
        
        if (entity instanceof Deleteable) {
            Deleteable deleteable = (Deleteable) entity;
            deleteable.delete();
            
            if(entity instanceof Site) {
            	((Site)entity).setDateEdited(new Date());
            }
            //db.setLastSchemaUpdate(new Date());  UGH this sucks
        }
        
        if (entity instanceof ReallyDeleteable) {
        	ReallyDeleteable reallyDeleteable = (ReallyDeleteable)entity;
        	reallyDeleteable.deleteReferences();
        	em.remove(reallyDeleteable);
        }
        
        return null;
    }

    private Class<Deleteable> entityClassForEntityName(String entityName) {
        try {
            return (Class<Deleteable>) Class.forName(UserDatabase.class.getPackage().getName() + "." + entityName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid entity name '" + entityName + "'");
        } catch (ClassCastException e) {
            throw new RuntimeException("Entity type '" + entityName + "' not Deletable");
        }
    }
}
