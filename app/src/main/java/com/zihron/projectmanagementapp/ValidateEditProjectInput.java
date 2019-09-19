package com.zihron.projectmanagementapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateEditProjectInput {

    public ValidateEditProjectInput()
    {

    }



    public String validationProjectNameField(String fieldValue)
    {
        String result="field okay";
        boolean invalid = false;
        if(fieldValue.length()>0)
        {
            for(int i=0; i<fieldValue.length() && !invalid; i++) {
                if (!Character.isAlphabetic(fieldValue.charAt(i)) && !Character.isDigit(fieldValue.charAt(i)) && fieldValue.charAt(i)!='_')
                {
                    invalid = true;
                    result = "Your projectname contains invalid characters";
                }
            }
        }

        else
        {
            result = "Projectname field value cannot be empty";
        }

        return result;
    }



    public String validationProjectDeescriptionField(String fieldValue)
    {
        String result="field okay";
        boolean invalid = false;

        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]-]");
        //Pattern eight = Pattern.compile (".{8}");


        Matcher hasLetter = letter.matcher(fieldValue);
        Matcher hasDigit = digit.matcher(fieldValue);
        Matcher hasSpecial = special.matcher(fieldValue);


        if(fieldValue.length()>0)
        {
          if(!hasLetter.find() && !hasDigit.find() && !hasSpecial.find())
          {
              result = "Your project description contains invalid characters";
          }
        }

        else
        {
            result = "Project description field value cannot be empty";
        }

        return result;
    }
}
