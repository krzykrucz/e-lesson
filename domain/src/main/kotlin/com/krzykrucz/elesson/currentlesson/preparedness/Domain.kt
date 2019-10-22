package com.krzykrucz.elesson.currentlesson.preparedness

import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber

data class StudentSubjectUnpreparednessInASemester(val count: NaturalNumber) // constraint max 3

//typealias RaiseUnpreparedness = (StudentSubjectUnpreparednessInASemester, PresentStudent) -> UnpreparedStudent
//
//typealias Write