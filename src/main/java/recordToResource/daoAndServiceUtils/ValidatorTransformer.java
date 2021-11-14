package recordToResource.daoAndServiceUtils;

import java.sql.Date;
import java.sql.Time;
import java.util.Locale;

public class ValidatorTransformer {
    final static int MIN_LENGTH_PHONE = 1;
    public String getWordOrNull(String name){
        String nameUpper = name.toUpperCase(Locale.ROOT).trim();
        return nameUpper.chars().allMatch(Character::isLetter)
            && nameUpper.length() > 1
                ? nameUpper : null;
    }
    public Date getDateOrNull(String dateString){
        try{
            Date date = Date.valueOf(dateString);
            return date;
        }catch(Exception exc){
            return null;
        }
    }
    public Time getTimeOrNull(String timeString){
        try{
            Time time = Time.valueOf(timeString);
            return time;
        }catch(Exception exc){
            return null;
        }
    }
    public String getPhoneOrNull(String phone) {
       phone = phone.trim();
       StringBuilder correctPhone = new StringBuilder();
       if (phone.length() == 0)
           return null;
       if (phone.charAt(0) == '+') correctPhone.append("810");
        if (phone.length() < MIN_LENGTH_PHONE)
            return null;
       for (int i = 0; i < phone.length(); i++){
           if(Character.isDigit(phone.charAt(i)))
               correctPhone.append(phone.charAt(i));
           else return null;
       }
       return  correctPhone.toString();

    }
}
