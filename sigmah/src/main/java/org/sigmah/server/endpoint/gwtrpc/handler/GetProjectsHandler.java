/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.hibernate.ejb.HibernateEntityManager;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectFunding;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.element.QuestionChoiceElement;
import org.sigmah.shared.domain.element.QuestionElement;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetProjectsHandler implements CommandHandler<GetProjects> {

    private final EntityManager em;
    private final Mapper mapper;

    private final static Log LOG = LogFactory.getLog(GetProjectsHandler.class);

    @Inject
    public GetProjectsHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    /**
     * Gets the projects list from the database.
     * 
     * @param cmd
     * @param user
     * 
     * @return a {@link CommandResult} object containing the
     *         {@link ProjectListResult} object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public CommandResult execute(GetProjects cmd, User user) throws CommandException {

        // Disable the ActivityInfo filter on Userdatabase.
        org.hibernate.Session session = ((HibernateEntityManager) em).getSession();
        session.disableFilter("userVisible");

        if (LOG.isDebugEnabled()) {
            LOG.debug("[execute] Gets projects: " + cmd + ".");
        }

        // Retrieves command parameters.
        final HashSet<Project> projects = new HashSet<Project>();
        final ProjectModelType modelType = cmd.getModelType();

        // ---------------
        // Projects which I own or I manage.
        // ---------------

        if (cmd.getViewOwnOrManage()) {
            final Query ownerManagerQuery = em
                    .createQuery("SELECT p FROM Project p WHERE p.owner = :ouser OR p.manager = :muser");
            ownerManagerQuery.setParameter("ouser", user);
            ownerManagerQuery.setParameter("muser", user);
            for (final Project p : (List<Project>) ownerManagerQuery.getResultList()) {
                projects.add(p);
            }
        }
        // ---------------
        // Projects in my visible organization units.
        // ---------------

        final List<Integer> ids = cmd.getOrgUnitsIds();

        // Use a set to be avoid duplicated entries.
        final HashSet<OrgUnit> units = new HashSet<OrgUnit>();

        // Checks if there is at least one org unit id specified.
        if (ids == null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("[execute] No org unit specified, gets all projects for the user org unit.");
            }

            // Crawl the org units hierarchy from the user root org unit.
            GetProjectHandler.crawlUnits(user.getOrgUnitWithProfiles().getOrgUnit(), units, false);
        } else {

            // Crawl the org units hierarchy from each specified org unit.
            OrgUnit unit;
            for (final Integer id : ids) {
                if ((unit = em.find(OrgUnit.class, id)) != null) {
                    GetProjectHandler.crawlUnits(unit, units, true);
                }
            }
        }

        // Retrieves all the corresponding org units.
        for (final OrgUnit unit : units) {

            // Builds and executes the query.
            final Query query = em.createQuery("SELECT p FROM Project p WHERE :unit MEMBER OF p.partners");
            query.setParameter("unit", unit);

            int count = 0;
            final List<Project> listResults = (List<Project>) query.getResultList();
            for (final Project p : listResults) {

                if (modelType == null) {
                    projects.add(p);
                    count++;
                }
                // Filters by model type.
                else {
                    if (p.getProjectModel().getVisibility(user.getOrganization()) == modelType) {
                        projects.add(p);
                        count++;
                    }
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("[execute] Found " + count + "/" + listResults.size() + " projects for org unit #"
                        + unit.getName() + ".");
            }
        }

        // ---------------
        // Mapping and return.
        // ---------------

        final ProjectListResult result = new ProjectListResult();

        switch (cmd.getReturnType()) {
        case PROJECT:
            // Not implemented.
            break;
        case ID:

            final ArrayList<Integer> projectsIds = new ArrayList<Integer>();
            for (final Project project : projects) {
                projectsIds.add(project.getId());
            }

            result.setListProjectsIds(projectsIds);
            break;

        case PROJECT_LIGHT:
        default:

            // Mapping into DTO objects
            final ArrayList<ProjectDTOLight> projectDTOList = new ArrayList<ProjectDTOLight>();
            for (final Project project : projects) {
                final ProjectDTOLight pLight = mapProject(em, mapper, user, project, true);
                projectDTOList.add(pLight);
            }

            result.setListProjectsLightDTO(projectDTOList);
            break;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("[execute] Found " + projects.size() + " project(s).");
        }

        return result;
    }

    /**
     * Map a project into a project light DTO.
     * 
     * @param em
     *            The entity manager.
     * @param mapper
     *            The entities mapper.
     * @param user
     *            The current user.
     * @param project
     *            The project.
     * @param mapChildren
     *            If the children projects must be retrieved.
     * @return The light DTO.
     */
    public static ProjectDTOLight mapProject(final EntityManager em, final Mapper mapper, final User user,
            final Project project, final boolean mapChildren) {

        final ProjectDTOLight pLight = mapper.map(project, ProjectDTOLight.class);

        // Fill the org unit.
        if (project.getPartners() != null) {
            for (final OrgUnit orgUnit : project.getPartners()) {
                pLight.setOrgUnitName(orgUnit.getName() + " - " + orgUnit.getFullName());
                break;
            }
        }

        // Fill categories.

        final GetValueHandler valuesHandler = new GetValueHandler(em, mapper);
        final GetValue cmd = new GetValue();
        cmd.setProjectId(pLight.getId());
        cmd.setElementEntityName("element.QuestionElement");

        final HashMap<CategoryTypeDTO, Set<CategoryElementDTO>> categories = new HashMap<CategoryTypeDTO, Set<CategoryElementDTO>>();

        // Retrieves each flexible element.
        for (final LayoutGroup group : project.getProjectModel().getProjectDetails().getLayout().getGroups()) {
            for (final LayoutConstraint constraint : group.getConstraints()) {

                final FlexibleElement element = constraint.getElement();

                // Tests if the element is a question.
                if (element instanceof QuestionElement) {

                    final QuestionElement question = (QuestionElement) element;

                    // Tests if the question has a category.
                    if (question.getCategoryType() != null) {

                        // Retrieves category values.
                        cmd.setElementId(question.getId());
                        ValueResult valueResult = null;
                        try {
                            valueResult = (ValueResult) valuesHandler.execute(cmd, user);
                        } catch (CommandException e) {
                            LOG.error("[mapProject] Error while retrieving que question values.", e);
                        }

                        // Tests if the categories has selected values.
                        if (valueResult != null) {

                            final CategoryTypeDTO type = mapper.map(question.getCategoryType(), CategoryTypeDTO.class);
                            Set<CategoryElementDTO> elements = categories.get(type);

                            if (elements == null) {
                                elements = new HashSet<CategoryElementDTO>();
                                categories.put(type, elements);
                            }

                            final List<Long> selectedChoicesId = ValueResultUtils.splitValuesAsLong(valueResult
                                    .getValueObject());
                            for (final Long id : selectedChoicesId) {
                                final QuestionChoiceElement choice = em.find(QuestionChoiceElement.class, id);
                                elements.add(mapper.map(choice.getCategoryElement(), CategoryElementDTO.class));
                            }

                        }
                    }
                }
            }
        }
        for (final PhaseModel phase : project.getProjectModel().getPhases()) {
            for (final LayoutGroup group : phase.getLayout().getGroups()) {
                for (final LayoutConstraint constraint : group.getConstraints()) {

                    final FlexibleElement element = constraint.getElement();

                    // Tests if the element is a question.
                    if (element instanceof QuestionElement) {

                        final QuestionElement question = (QuestionElement) element;

                        // Tests if the question has a category.
                        if (question.getCategoryType() != null) {

                            // Retrieves category values.
                            cmd.setElementId(question.getId());
                            ValueResult valueResult = null;
                            try {
                                valueResult = (ValueResult) valuesHandler.execute(cmd, user);
                            } catch (CommandException e) {
                                LOG.error("[mapProject] Error while retrieving que question values.", e);
                            }

                            // Tests if the categories has selected values.
                            if (valueResult != null) {

                                final CategoryTypeDTO type = mapper.map(question.getCategoryType(),
                                        CategoryTypeDTO.class);
                                Set<CategoryElementDTO> elements = categories.get(type);

                                if (elements == null) {
                                    elements = new HashSet<CategoryElementDTO>();
                                    categories.put(type, elements);
                                }

                                final List<Long> selectedChoicesId = ValueResultUtils.splitValuesAsLong(valueResult
                                        .getValueObject());
                                for (final Long id : selectedChoicesId) {
                                    final QuestionChoiceElement choice = em.find(QuestionChoiceElement.class, id);
                                    elements.add(mapper.map(choice.getCategoryElement(), CategoryElementDTO.class));
                                }

                            }
                        }
                    }
                }
            }
        }
        pLight.setCategoriesMap(categories);

        // Fill children.
        final ArrayList<ProjectDTOLight> children = new ArrayList<ProjectDTOLight>();
        // Maps the funding projects.
        if (mapChildren && project.getFunding() != null) {
            for (final ProjectFunding funding : project.getFunding()) {

                final Project pFunding = funding.getFunding();

                if (pFunding != null) {
                    // Recursive call to retrieve the child (without its
                    // children).
                    children.add(mapProject(em, mapper, user, pFunding, false));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("[execute] Attached funding project " + pFunding.getName() + " - '"
                                + pFunding.getFullName() + "'.");
                    }
                }
            }
        }
        // Maps the funded projects.
        if (mapChildren && project.getFunded() != null) {
            for (final ProjectFunding funded : project.getFunded()) {

                final Project pFunded = funded.getFunded();

                if (pFunded != null) {
                    // Recursive call to retrieve the child (without its
                    // children).
                    children.add(mapProject(em, mapper, user, pFunded, false));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("[execute] Attached funded project " + pFunded.getName() + " - '"
                                + pFunded.getFullName() + "'.");
                    }
                }
            }
        }
        pLight.setChildrenProjects(children);

        return pLight;
    }
}
