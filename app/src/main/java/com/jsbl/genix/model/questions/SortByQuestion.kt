package com.jsbl.genix.model.questions

class SortByQuestion : Comparator<QuestionResponseItem> {
    override fun compare(studentOne: QuestionResponseItem, studentTwo: QuestionResponseItem): Int {
        return studentTwo.identifier?.let { studentOne.identifier?.compareTo(it) }!!
    }
}