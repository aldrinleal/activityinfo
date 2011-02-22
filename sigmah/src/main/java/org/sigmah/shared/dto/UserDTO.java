package org.sigmah.shared.dto;

import java.util.Date;
import java.util.List;

import org.sigmah.shared.dto.profile.ProfileDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
/**
 * One-to-one DTO of the {@link org.sigmah.shared.domain.User} domain class.
 * 
 * @author nrebiai
 * 
 */
public class UserDTO extends BaseModelData implements EntityDTO {

	private static final long serialVersionUID = 5865780039352557006L;
	
    @Override
    public String getEntityName() {
        return "User";
    }

    @Override
    public int getId() {
        return (Integer) get("id");
    }

    public void setId(int id) {
        set("id", id);
    }

	
	public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }
    
    public String getEmail() {
        return get("email");
    }

    public void setEmail(String email) {
    	set("email", email);
    }
    
    public String getFirstName() {
        return get("firstName");
    }

    public void setFirstName(String firstName) {
    	set("firstName", firstName);
    }
    
    public String getLocale() {
        return get("locale");
    }

    public void setLocale(String locale) {
    	set("locale", locale);
    }
    
    public OrgUnitDTO getOrgUnitWithProfiles() {
        return get("orgUnit");
    }

    public void setOrgUnitWithProfiles(OrgUnitDTO orgUnit) {
    	set("orgUnit", orgUnit);
    }
    
    public String getChangePasswordKey() {
        return get("pwdChangeKey");
    }

    public void setChangePasswordKey(String pwdChangeKey) {
    	set("pwdChangeKey", pwdChangeKey);
    }
    
    public Date getDateChangePasswordKeyIssued() {
        return get("pwdChangeDate");
    }

    public void setDateChangePasswordKeyIssued(Date pwdChangeDate) {
    	set("pwdChangeDate", pwdChangeDate);
    }
    
    public List<ProfileDTO> getProfilesDTO() {
    	return get("profilesDTO");
    }
    
    public void setProfilesDTO(List<ProfileDTO> profiles) {
        set("profilesDTO", profiles);
    }

    public String getCompleteName() {
        return get("cname");
    }

    public void setCompleteName(String cname) {
        set("cname", cname);
    }
    
    public void setIdd(int id){
    	set("idd", id);
    }
    
    public int getIdd(){
    	return (Integer) get("idd");
    }
    
    public void setActive(boolean isActive){
    	set("active", isActive);
    }
    
    public boolean getActive(){
    	return (Boolean) get("active");
    }

}
