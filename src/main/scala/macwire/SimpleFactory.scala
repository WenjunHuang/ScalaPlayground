package macwire

import com.github.dwickern.macros.NameOf._

object SimpleFactory extends App {

  class DatabaseAccess() {
    def foo(): Unit = {
      println(nameOfType[DatabaseAccess])
    }
  }

  class TaxDeductionLibrary(databaseAccess: DatabaseAccess) {
    def foo(): Unit ={
      println(nameOfType[TaxDeductionLibrary])
    }
  }

  class TaxCalculator(taxBase: Double, taxDeductionLibrary: TaxDeductionLibrary) {
    def foo(): Unit = {
      println(nameOfType[TaxCalculator])
      taxDeductionLibrary.foo()
    }
  }

  trait TaxModule {

    import com.softwaremill.macwire._

    lazy val theDatabaseAccess = wire[DatabaseAccess]
    lazy val theTaxDeductionLibrary = wire[TaxDeductionLibrary]

    def taxCalculator(taxBase: Double) = wire[TaxCalculator]
  }

  println("hello macwire")
  val taxModule = new TaxModule {}
  taxModule.taxCalculator(100).foo()

}
