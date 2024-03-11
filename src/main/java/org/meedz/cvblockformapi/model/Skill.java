package org.meedz.cvblockformapi.model;

import lombok.Data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Data
public class Skill {
    public BigInteger skill_id;
    public Date creation_date;
    public Date modification_date;
    public Boolean deleted;
    public BigInteger skill_folder_id;
    public String name;
    public String type;

    /**
     * Useful for thymeleaf.
     *
     * @param skillType Type of the skill
     * @return a list of skill names corresponding to the type
     */
    public List<String> getAMapOfSkills(String skillType) {
        List<String> list = new ArrayList<>();

        return list;
    }

}
