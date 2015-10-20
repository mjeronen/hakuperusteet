package fi.vm.sade.hakuperusteet.domain

import java.time.{ZoneId, LocalDate}
import java.util.Date

object Users {

  def generateUsers = {
    val userData = (firstNameAndGenders zip lastNames) zipWithIndex

    userData.map{ case (((name, gender), lastName), i) =>
      User(None, Some(personOidFromIndex(i)), s"${name}.${lastName}@example.com".toLowerCase, name, lastName, birthDate, None, "google", gender, "AB", "004") }
  }

  private def personOidFromIndex(index: Int) = f"1.2.246.562.24.${index + 1000}%011d"

  private val MIES = "1"
  private val NAINEN = "2"

  private val firstNameAndGenders = List(
    ("Anni", NAINEN), ("Ossi", MIES),
    ("Pentti", MIES), ("Ritva",NAINEN),
    ("Ilja", MIES), ("Pirjo", NAINEN),
    ("Kalevi", MIES), ("Marja", NAINEN))

  private val lastNames = List("Annilainen", "Ossilainen", "Penttiläinen", "Iljanen", "Simonen", "Kalevinen", "Marjanen")

  private def birthDate = Date.from(LocalDate.now().minusYears(20).atStartOfDay(ZoneId.systemDefault()).toInstant())
}
