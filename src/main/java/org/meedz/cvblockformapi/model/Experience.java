package org.meedz.cvblockformapi.model;

import lombok.Data;
import java.math.BigInteger;
import java.util.Date;

@Data
public class Experience {
    public BigInteger experience_id;
    public Date creation_date;
    public Date modification_date;
    public Boolean deleted;
    public BigInteger skill_folder_id;
    public String client;
    public String work_function;
    public Date begin_date;
    public Date ending_date;
    public String details;

}
