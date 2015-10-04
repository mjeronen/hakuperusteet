package fi.vm.sade.hakuperusteet

object CodeGeneratorTest {
  val dbUrl = "jdbc:hsqldb:mem:hakuperusteet"
  val user = "sa"
  val password = ""

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.CodeGeneratorTest"
  */
  def main(args: Array[String]): Unit = {
    val db = new HsqlDatabase(dbUrl, user, password)
    db.startHsqlServer()
    createDbClasses
  }

  def createDbClasses {
    slick.codegen.SourceCodeGenerator.main(Array(
      "slick.driver.HsqldbDriver",
      "org.hsqldb.jdbc.JDBCDriver", dbUrl, "src/test/scala", "fi.vm.sade.hakuperusteet.db.generated", user, password)
    )
  }
}
