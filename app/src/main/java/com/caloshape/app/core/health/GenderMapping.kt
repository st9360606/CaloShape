package com.caloshape.app.core.health


fun toCalcGender(genderStr: String?): Gender =
    if (genderStr?.equals("MALE", ignoreCase = true) == true) Gender.Male else Gender.Female
