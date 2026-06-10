package com.dbizz.util;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;

public class PhoneNoUtil {

    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private PhoneNoUtil() {

    }

    public static boolean isValidPhoneNo(String phoneNo) {
        try {
            PhoneNumber number = phoneNumberUtil.parse(phoneNo, null);
            return phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static void validatePhoneNo(String phoneNo) {
        try {
            PhoneNumber number = phoneNumberUtil.parse(phoneNo, null);
            phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid Phone Number : " + phoneNo);
        }
    }
}
