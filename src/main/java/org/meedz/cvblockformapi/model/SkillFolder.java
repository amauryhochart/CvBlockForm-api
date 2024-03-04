package org.meedz.cvblockformapi.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Document(collection = "skillfolder")
public class SkillFolder {

    public int skill_folder_id;
    public Date creation_date;
    public Date modification_date;
    public Boolean deleted;
    public int experience_years;
    public String email;
    public int tjm;
    public String mobility;
    public List<String> languages;
    public String last_name;
    public String first_name;
    public String actual_function;
    public Date availability;
    public List<Skill> skills;
    public List<Experience> experiences;
    public List<Learning> learnings;
    public String resume;

}
