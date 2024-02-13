package org.meedz.cvblockformapi.model;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Consultant {
    public BigInteger consultant_id;
    public Date creation_date;
    public Date modification_date;
    public Boolean deleted;
    public BigInteger skill_folder_id;
    public String last_name;
    public String first_name;
    public String actual_function;
    public Date disponibility;

}
