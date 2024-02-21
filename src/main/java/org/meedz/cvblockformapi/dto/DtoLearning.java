package org.meedz.cvblockformapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.Document;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseBody
public class DtoLearning extends Document {
    private int learning_id;
    private Date creation_date;
    private Date modification_date;
    private Boolean deleted;
    private int skill_folder_id;
    public String name;
    public String institution;
    public Date date;

}
