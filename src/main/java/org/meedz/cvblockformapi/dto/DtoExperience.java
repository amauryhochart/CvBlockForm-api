package org.meedz.cvblockformapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.Document;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseBody
public class DtoExperience extends Document {
    private int experience_id;
    private Date creation_date;
    private Date modification_date;
    private Boolean deleted;
    private int skill_folder_id;
    private String client;
    private String work_function;
    private Date begin_date;
    private Date ending_date;
    private String details;

}
