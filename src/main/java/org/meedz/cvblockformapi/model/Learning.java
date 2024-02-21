package org.meedz.cvblockformapi.model;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Learning {
    public BigInteger learning_id;
    public Date creation_date;
    public Date modification_date;
    public Boolean deleted;
    public BigInteger skill_folder_id;
    public String name;
    public String institution;
    public Date date;

}
