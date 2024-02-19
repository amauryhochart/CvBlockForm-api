package org.meedz.cvblockformapi.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Experience {
    public int experience_id;
    public Date creation_date;
    public Date modification_date;
    public Boolean deleted;
    public int skill_folder_id;
    public String client;
    public String work_function;
    public Date begin_date;
    public Date ending_date;
    public String details;

}
