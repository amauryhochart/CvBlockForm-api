package org.meedz.cvblockformapi.model;

import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
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

    /**
     * Useful for thymeleaf.
     *
     * @param date Date
     * @return a string date formatted to MM/yyyy
     */
    public String convertDateToMonthYearNumberString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
        return formatter.format(date);
    }

    /**
     * Useful for thymeleaf.
     *
     * @param date Date
     * @return a string date formatted to MMMM yyyy (september 2024)
     */
    public String convertDateToMonthTextYearString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");
        return formatter.format(date);
    }
}
