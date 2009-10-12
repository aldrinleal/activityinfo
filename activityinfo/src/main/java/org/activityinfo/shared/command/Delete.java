package org.activityinfo.shared.command;

import org.activityinfo.shared.command.result.VoidResult;
import org.activityinfo.shared.dto.EntityDTO;


/**
 * Deletes a database entity.
 *
 * Note: the specified entity must be <code>Deletable</code>
 *
 * Returns <code>VoidResult</code>
 *
 * @see org.activityinfo.server.domain.Deleteable
 *
 */
public class Delete implements Command<VoidResult> {
	

	private String entityName;
	private int id;

    protected Delete() {
		
	}
	
	public Delete(EntityDTO entity) {
		this.entityName = entity.getEntityName();
		this.id =  entity.getId();
	}

    public Delete(String entityName, int id) {
        this.entityName = entityName;
        this.id = id;
    }

    public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
