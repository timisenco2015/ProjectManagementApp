package com.zihron.projectmanagementapp;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by ayoba on 2018-04-07.
 */

 public  class  ValidationClass {

    private Pattern pattern;
    private Matcher matcher;
   public ValidationClass()
   {

   }

    public String checkInputSearchField(String fieldValue)
    {
        boolean isValid = true;
        String messageStatus="field is okay";
        if(fieldValue.length()==0 )
        {
            messageStatus ="Please enter search value";
            isValid = true;
        }
        else
        {
            for(int i=0; i<fieldValue.length() && isValid;i++)
            {
                if(!Character.isAlphabetic(fieldValue.charAt(i)))
                {
                    isValid = false;
                    messageStatus ="Value entered is not an alphabet or a word";
                }
            }
        }

        return messageStatus;
    }

   public boolean checkFieldEmpty(String fieldValue)
   {
       boolean valueNull = false;
       if(fieldValue.length()==0 )
       {

           valueNull = true;
       }

       return valueNull;
   }

    public boolean  checkNumericOnlyValue(String fieldValue)
    {
        boolean valueNull = false;
        if(fieldValue.length()>0 )
        {
            for(int i=0; i<fieldValue.length() && !valueNull; i++) {
                if (!Character.isDigit(fieldValue.charAt(i))) {
                    valueNull = true;
                }
            }

        }


        return valueNull;
    }

    public String checkFieldValue(String fieldValue)
    {
        String result="field okay";
        boolean errorCheck = false;
        if(fieldValue.length()>0)
        {
            for(int i=0; i<fieldValue.length() && !errorCheck; i++) {
                if (!Character.isDigit(fieldValue.charAt(i)) && !Character.isAlphabetic(fieldValue.charAt(i))) {
                    errorCheck = true;
                    result = "Your firstname contains invalid characters";
                }
            }
        }
        else
        {
            result = "This field value cannot be empty";
        }
        return result;
    }

    public boolean  checkInvalidChar(String fieldValue)
    {
        boolean valueNull = false;
        if(fieldValue.length()>=0 )
        {
           for(int i=0; i<fieldValue.length() && !valueNull; i++)
           {
               if(fieldValue.charAt(i)=='@' || fieldValue.charAt(i)=='^' ||fieldValue.charAt(i)=='&' )
               {
                   valueNull = true;
               }
           }

        }

        return valueNull;
    }

    public boolean  checkStartWithInvalidChars(String fieldValue)
    {
        boolean valueNull = false;
        if(fieldValue.length()>=0 )
        {
            for(int i=0; i<fieldValue.length(); i++)
            {

                if(!Character.isAlphabetic(fieldValue.charAt(0)) )
                {
                    valueNull = true;
                }
            }

        }

        return valueNull;
    }

    public boolean validateDate(String stringDate)
    {
        boolean check =false;

        final String DATE_PATTERN = "((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])";
        pattern = Pattern.compile(DATE_PATTERN);
        matcher = pattern.matcher(stringDate);

        if(matcher.matches())
        {
            check = true;
        }
        return check;
    }

    public boolean validateEmail(String emailStr)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (emailStr == null)
            return false;
        return pat.matcher(emailStr).matches();
    }


    public String validationGroupName(String fieldValue)
    {
        String result="field okay";
        boolean invalid = false;
        if(fieldValue.length()>0)
        {
            for(int i=0; i<fieldValue.length() && !invalid; i++) {
                if (!Character.isAlphabetic(fieldValue.charAt(i))&&!Character.isDigit(fieldValue.charAt(i))&& fieldValue.charAt(i)!='-'&& fieldValue.charAt(i)!='_')
                {
                    invalid = true;
                    result = "Your group name contains invalid characters";
                }
            }
        }

        else
        {
            result = "group name field value cannot be empty";
        }

        return result;
    }
}
