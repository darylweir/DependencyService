package com.darylweir.depservice

import org.scalatest.FlatSpec
import org.saddle.Frame
import org.saddle.Vec
import org.saddle.Series

class DataModelSpec extends FlatSpec {
  
  "The ProbabilityEstimator" should "return 1/6 for the probability a RV" +
   "= {1,2,3,4,5,6} is a specific value" in {
      val s = Series("1"->1,"2"->2,"3"->3,"4"->4,"5"->5,"6"->6)
      val f = Frame("p"->s)
      for (i <- 1 to 6) {
        assert(ProbabilityEstimator.probability(i,f) == 1.0 / 6)
      }
  }
  
  it should "return 0 for probability of an item not in the set" in {
    val s = Series("1"->1,"2"->2,"3"->3,"4"->4,"5"->5,"6"->6)
    val f = Frame("p"->s)
    val notInSet = 7;
    assert(ProbabilityEstimator.probability(notInSet,f) == 0.0)
  }
  
  it should "return 1/4 for joint probability of 2 simple independent RVs" in {
    val s1 = Series("1"->1,"2"->1, "3"->2, "4"->2)
    val s2 = Series("1"->1,"2"->2, "3"->1, "4"->2)
    val f = Frame("p1"->s1,"p2"->s2)
    assert(ProbabilityEstimator.jointProbability(1, 2, f) == 0.25)
  }
  
  it should "compute mutual information correctly for the independent case" in {
    val s1 = Series("1"->1,"2"->1, "3"->2, "4"->2)
    val s2 = Series("1"->1,"2"->2, "3"->1, "4"->2)
    val f = Frame("p1"->s1,"p2"->s2)
    assert(ProbabilityEstimator.mutualInformation(f, List(1,2), List(1,2), "p1", "p2")==0.0)
  }
  
  it should "compute mutual information correctly for the dependent case" in {
    val s1 = Series("1"->1,"2"->1, "3"->1, "4"->2)
    val s2 = Series("1"->1,"2"->2, "3"->1, "4"->2)
    val f = Frame("p1"->s1,"p2"->s2)
    assert(ProbabilityEstimator.mutualInformation(f, List(1,2), List(1,2), "p1", "p2")-0.311 < 0.001)
  }
  
  it should "handle bad input for probability" in {
    val s1 = Series("1"->1,"2"->1, "3"->1, "4"->2)
    val s2 = Series("1"->1,"2"->2, "3"->1, "4"->2)
    val f = Frame("p1"->s1,"p2"->s2)
    val f2 = Frame("p1"->s1)
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.probability(1, f)
    }
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.probability(1, f.rowSlice(0,0))
    }
  }
  
  it should "handle bad input for joint probability" in {
    val s1 = Series("1"->1,"2"->1, "3"->1, "4"->2)
    val s2 = Series("1"->1,"2"->2, "3"->1, "4"->2)
    val f = Frame("p1"->s1,"p2"->s2)
    val f2 = Frame("p1"->s1)
    val f3 = Frame("p1"->s1,"p2"->s2,"p3"->s1)
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.jointProbability(1, 2, f2)
    }
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.jointProbability(1, 2, f3)
    }
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.jointProbability(1, 2, f.rowSlice(0,0))
    }
  }
  
  it should "handle bad input for mutual information" in {
    val s1 = Series("1"->1,"2"->1, "3"->1, "4"->2)
    val s2 = Series("1"->1,"2"->2, "3"->1, "4"->2)
    val f = Frame("p1"->s1,"p2"->s2)
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.mutualInformation(f, List(), List(1,2,3), "p1", "p2")
    }
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.mutualInformation(f, List(1,2), List(), "p1", "p2")
    }
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.mutualInformation(f, List(1,2,3), List(1,2,3), "p3", "p2")
    }
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.mutualInformation(f, List(1,2,3), List(1,2,3), "p1", "p4")
    }
    assertThrows[IllegalArgumentException] {
      ProbabilityEstimator.mutualInformation(f.rowSlice(0,0), List(1,2,3), List(1,2,3), "p1", "p2")
    }
  }
  
  val dm = new DataModel
  
  "A DataModel" should "correctly test whether keys are valid or not" in {
    assert(dm.contains("ux_kickstart"))
    assert(!dm.contains("missingkey"))
  }
  
  it should "return a List of Dependency when a key is present, and none otherwise." in {
    var list = false;
    var none = false;
    
    list = dm.get("ux_kickstart") match {
      case Some(x) => true
      case None => false
    }
    
    none = dm.get("missingkey") match {
      case Some(x) => false
      case None => true
    }
    
    assert(list)
    assert(none)
  }
  
}