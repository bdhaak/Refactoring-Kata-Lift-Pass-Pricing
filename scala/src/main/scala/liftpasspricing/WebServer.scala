package liftpasspricing

object WebServer extends App {

  println(
    """LiftPassPricing Api started on 5010,
      |you can open http://localhost:5010/prices?type=night&customerAge=23&date=2019-02-18 in a navigator
      |and you'll get the price of the list pass for the day.""".stripMargin)

  new LiftPassPricing().startServer("localhost", 5010)

}
