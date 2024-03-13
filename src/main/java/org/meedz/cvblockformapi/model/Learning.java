package org.meedz.cvblockformapi.model;

import lombok.Data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
    public Date begin_date;
    public Date ending_date;

    /**
     * Useful for thymeleaf.
     *
     * @param date Date
     * @return a string date formatted to yyyy
     */
    public String convertDateToYearString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(date);
    }

}
