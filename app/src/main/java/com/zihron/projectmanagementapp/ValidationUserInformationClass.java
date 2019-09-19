package com.zihron.projectmanagementapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ayoba on 2018-04-07.
 */

 public  class ValidationUserInformationClass {

    private Pattern pattern;
    private Matcher matcher;
   public ValidationUserInformationClass()
   {

   }

  public String validationFirstName(String fieldValue)
  {
      String result="field okay";
      boolean invalid = false;
              if(fieldValue.length()>0)
              {
                  for(int i=0; i<fieldValue.length() && !invalid; i++) {
                      if (!Character.isAlphabetic(fieldValue.charAt(i)))
                      {
                          invalid = true;
                          result = "Your firstname contains invalid characters";
                      }
                  }
              }

              else
              {
                result = "Firstname field value cannot be empty";
              }

              return result;
  }


    public String validationLastName(String fieldValue)
    {
        String result="field okay";
        boolean invalid = false;
        if(fieldValue.length()>0)
        {
            for(int i=0; i<fieldValue.length() && !invalid; i++) {
                if (!Character.isAlphabetic(fieldValue.charAt(i)))
                {
                    invalid = true;
                    result = "Your Lastname contains invalid characters";
                }
            }
        }

        else
        {
            result = "Lastname field value cannot be empty";
        }

        return result;
    }

public String validateHomeAddress(String fieldValue)
{
    String result="field okay";
    boolean invalid = false;

    if(fieldValue.length()>0)
    {
        for(int i=0; i<fieldValue.length() && !invalid; i++) {

            if (!Character.isDigit(fieldValue.charAt(i)) && !Character.isAlphabetic(fieldValue.charAt(i)) && !Character.isWhitespace(fieldValue.charAt(i)) && fieldValue.charAt(i)!='.' && fieldValue.charAt(i)!=','&& fieldValue.charAt(i)!='-')
            {
                invalid = true;
                result = "Your Home Adress contains invalid characters";
            }
        }
    }
    else
    {
        result = "Home Address field value cannot be empty";
    }
    return result;
}


    public String validationMiddleName(String fieldValue)
    {
        String result="field okay";
        boolean invalid = false;
        if(fieldValue.length()>0)
        {
            for(int i=0; i<fieldValue.length() && !invalid; i++) {
                if (!Character.isAlphabetic(fieldValue.charAt(i)))
                {
                    invalid = true;
                    result = "Your Middlename contains invalid characters";
                }
            }
        }
        else
        {
            result = "Middlename field value cannot be empty";
        }

        return result;
    }


    private boolean  checkStartWithInvalidChars(String fieldValue)
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

    public boolean checkLowerCase(char charValue)
    {

        boolean found = false;


                if(Character.isLowerCase(charValue) )
                {

                    found=true;
                }

        return found;
    }


    public boolean checkUpperCase(char charValue)
    {
        boolean found = false;


            if(Character.isUpperCase(charValue) )
            {
                found=true;
        }

        return found;
    }

    public boolean checkSpecialCharacters(String fieldValue)
    {
        boolean found=false;

        Pattern regex = Pattern.compile("[&+,:;=?@#'<>^*()%!-]");
        //matcher to find if there is any special character in string
        Matcher matcher = regex.matcher(fieldValue);

        if(matcher.find())
        {
          found= true;
        }

 return found;
    }

    public String  validatePassword(String fieldValue)
    {
        int lowercaseCount = 0;
        int uppercaseCount =0;
        int numbersCount =0;

                boolean errorFound= false;

        String result="field okay";
        boolean invalid = false;
        if(fieldValue.length()>=0) {

            if (checkStartWithInvalidChars(fieldValue)) {
                result = "Password cannot start with number or special character";
            } else if (!checkSpecialCharacters(fieldValue)) {
                result = "Password must have at least one special character";
            } else {
                for (int i = 0; i < fieldValue.length(); i++) {
                    if (Character.isAlphabetic(fieldValue.charAt(i)) && checkLowerCase(fieldValue.charAt(i))) {
                        lowercaseCount++;

                    } else if (Character.isAlphabetic(fieldValue.charAt(i)) && checkUpperCase(fieldValue.charAt(i))) {
                        uppercaseCount++;

                    } else if (Character.isDigit(fieldValue.charAt(i))) {
                        numbersCount++;
                    }

                }

                if (lowercaseCount == 0) {
                    result = "your password must have at least one lower case letter";
                } else if (uppercaseCount == 0) {
                    result = "your password must have at least one upper case letter";
                } else if (numbersCount == 0) {
                    result = "your password must have at least one number";
                }


            }
        }

        else
        {
            result = "Middlename field value cannot be empty";
        }

        return result;
    }


}
