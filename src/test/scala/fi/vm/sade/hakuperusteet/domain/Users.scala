package fi.vm.sade.hakuperusteet.domain

import java.time.{ZoneId, LocalDate}
import java.util.Date
import fi.vm.sade.hakuperusteet.personIdDateFormatter

object Users {

  def generateUsers = {
    val userData = (firstNameGenderAndPersonIDs zip lastNames) zipWithIndex

    userData.map{ case (((name, gender, personId), lastName), i) =>
      User(None, Some(personOidFromIndex(i)), s"${name}.${lastName}@example.com".toLowerCase, name, lastName, birthDate, Option(personId), "google", gender, "AB", "004") }
  }

  private def personOidFromIndex(index: Int) = f"1.2.246.562.24.${index + 1000}%011d"

  private val MIES = "1"
  private val NAINEN = "2"

  private val firstNameGenderAndPersonIDs = List(
    ("Anni", NAINEN, "261095-910P"), ("Ossi", MIES, "261095-939M"),
    ("Pentti", MIES, "261095-977V"), ("Ritva",NAINEN, "261095-904H"),
    ("Ilja", MIES, "261095-933E"), ("Pirjo", NAINEN, "261095-962C"),
    ("Kalevi", MIES, "261095-9854"), ("Marja", NAINEN, "261095-9843"))

  private val lastNames = List("Annilainen", "Ossilainen", "Penttiläinen", "Iljanen", "Simonen", "Kalevinen", "Marjanen")

  private def birthDate = Date.from(LocalDate.from(personIdDateFormatter.parse("261095")).atStartOfDay(ZoneId.systemDefault()).toInstant())
}
