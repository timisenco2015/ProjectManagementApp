package com.zihron.projectmanagementapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatePasswordReqQuestFields {

    public ValidatePasswordReqQuestFields()
    {

    }
    public static boolean Password_Validation(String password)
    {

        if(password.length()>=8)
        {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
            //Pattern eight = Pattern.compile (".{8}");


            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigit = digit.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            return hasLetter.find() && hasDigit.find() && hasSpecial.find();

        }
        else
            return false;

    }

    public String validateQuestionsField(String fieldValue)
    {
        String result="field okay";
        boolean invalid = false;
        if(fieldValue.length()>0)
        {
            for(int i=0; i<fieldValue.length() && !invalid; i++) {
                if (!Character.isAlphabetic(fieldValue.charAt(i))&& fieldValue.charAt(i)!=' ')
                {
                    invalid = true;
                    result = "Your this contains invalid characters. Letters only (A-Z)";
                }
            }
        }

        else
        {
            result = "this field value cannot be empty";
        }

        return result;
    }

    public String validateAnswersField(String fieldValue)
    {
        String result="field okay";
        boolean invalid = false;
        if(fieldValue.length()>0)
        {
            for(int i=0; i<fieldValue.length() && !invalid; i++) {
                if (!Character.isAlphabetic(fieldValue.charAt(i)) && !Character.isDigit(fieldValue.charAt(i)) && fieldValue.charAt(i)!='.' && fieldValue.charAt(i)!=',' && fieldValue.charAt(i)!='-'
                        && fieldValue.charAt(i)!='_' && fieldValue.charAt(i)!='@' && fieldValue.charAt(i)!='$' && fieldValue.charAt(i)!='#' && fieldValue.charAt(i)!=' ')
                {
                    invalid = true;
                    result = "Your this contains invalid characters. Letters only (A-Z, 0-9, $, #, @, ., ,, _, -)";
                }
            }
        }

        else
        {
            result = "this field value cannot be empty";
        }

        return result;
    }
}
