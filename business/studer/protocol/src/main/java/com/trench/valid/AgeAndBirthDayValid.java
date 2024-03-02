package com.trench.valid;

import com.trench.cofig.valid.KenValid;
import com.trench.cofig.valid.LinkageValid;
import com.trench.input.StuderInput;

import java.util.Calendar;
import java.util.Date;

public class AgeAndBirthDayValid implements KenValid<StuderInput> {
    @Override
    public boolean isValid(LinkageValid custemValid, StuderInput value) {
        Date birthDay = value.getBirthDay();
        Integer age = value.getAge();
        if (birthDay==null||age==null)return true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int newYear = calendar.get(Calendar.YEAR);
        calendar.setTime(birthDay);
        int birthYear = calendar.get(Calendar.YEAR);
        return newYear-birthYear==age;
    }
}
