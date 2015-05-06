package io.straight.radg

import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import io.straight.radg.randomtypes.{TemplateGenerator, RegexGenerator}

/**
 * @author rbuckland
 */
class GenerationTests extends FlatSpec with Matchers {
  "A String Array Random Generate" should "create the correct number of rows" in {

    val randimio = SeqOfSeqOfStringsGenerator(20,List(new RegexGenerator("myregexp", "[a-c][0-9]\\d\\d\\w Word")))
    val randomData = randimio.generate()
    randomData._2.length should be (20)

  }

}

class TopoSortTest extends FlatSpec with Matchers {
  "A dependent set " should "be sorted into execution order" in {
    val ramondom = SeqOfSeqOfStringsGenerator(10,List(
      TemplateGenerator("A","${E} ${B} ${C}"),
      TemplateGenerator("B","${F}"),
      TemplateGenerator("C","${F}"),
      TemplateGenerator("D",""),
      TemplateGenerator("E",""),
      TemplateGenerator("F","${D}")
    ))

    val sorted = ramondom.dependencyOrdered
    sorted(2).name should be ("F")
    sorted(5).name should be ("A")
  }
}


class TemplateTest extends FlatSpec with Matchers {
  "A dependent set " should "be sorted into execution order and produce a correct result" in {
    val ramondom = SeqOfSeqOfStringsGenerator(1,List(
      TemplateGenerator("A","This is A [${E}] [${B}] [${C}]"),
      TemplateGenerator("B","This is B [${F}]"),
      TemplateGenerator("C","This is C [${F}]"),
      TemplateGenerator("D","DEE_Template"),
      TemplateGenerator("E","E_TEMPLATE"),
      TemplateGenerator("F","This is F [${D}]")
    ))
    /* -- above definitions will create this (each line is an element in the List)
      This is A [E_TEMPLATE] [This is B [This is F [DEE_Template]]] [This is C [This is F [DEE_Template]]]
      This is B [This is F [DEE_Template]]
      This is C [This is F [DEE_Template]]
      DEE_Template
      E_TEMPLATE
      This is F [DEE_Template]
     */

    val sorted = ramondom.dependencyOrdered

    val result = ramondom.generate()

    // check the column names
    sorted(2).name should be ("F")
    sorted(5).name should be ("A")
    // building the template from the definition above will produce the below
    result._2(0)(5) should be ("This is F [DEE_Template]")
    result._2(0)(0) should be ("This is A [E_TEMPLATE] [This is B [This is F [DEE_Template]]] [This is C [This is F [DEE_Template]]]")
  }
}
