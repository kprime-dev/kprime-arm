package unibz.cs.semint.kprime

import unibz.cs.semint.kprime.scenario.SakilaScenario

class Starter {

 companion object {

     /**
      * Reads arguments from command lines and eventually gives then as scenario parameters.
      */
     fun main(args:Array<String>) {
         val version = "0.1.0-SNAPSHOT"
         println("KPrime $version")
         SakilaScenario().run()
     }

 }

}