package org.meedz.cvblockformapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.Document;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseBody
public class DtoSkillFolder extends Document {

    public int skill_folder_id;
    public Date creation_date;
    public Date modification_date;
    public Boolean deleted;
    public int experience_years;
    public String email;
    public int tjm;
    public String mobility;
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public List<String> languages;
    public String last_name;
    public String first_name;
    public String actual_function;
    public Date availability;
    public List<DtoSkill> skills;
    public List<DtoExperience> experiences;
    public List<DtoLearning> learnings;
    public String resume;

}
