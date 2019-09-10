package com.github.scalaspring.akka.config

import java.net.URL

import com.github.scalaspring.test.scalatest.TestContextManagement
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{FlatSpec, Matchers}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation._
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
  //loader = classOf[SpringBootContextLoader],
  classes = Array(classOf[AkkaConfigAutoConfigurationSpec.Configuration])
)
class AkkaConfigAutoConfigurationSpec extends FlatSpec with TestContextManagement with Matchers with StrictLogging {

  import AkkaConfigAutoConfigurationSpec._

  @Autowired val environment: ConfigurableEnvironment = null

  @Autowired val config: Config = null

  @Autowired val testUrl: URL = null

  val defaultConfig: Config = ConfigFactory.defaultReference

  @Value("${single.string}")
  val singleString: String = null

  @Value("${java.vm.version}")
  val javaVmVersion: String = null

  "System properties" should "be accessible" in {
    //logger.info(System.getProperties.keySet.toString)
    //logger.info(defaultConfig.entrySet().asScala.map(_.getKey).toSeq.sorted.toString)
    javaVmVersion should not be null
    javaVmVersion shouldBe System.getProperty("java.vm.version")
  }

  "Autowired dependency" should "be injected" in {
    testUrl shouldBe TEST_URL
  }

  "Spring configuration" should "override Akka default configuration" in {
    val propertyName = "akka.test.default-timeout"

    val defaultTimeout = defaultConfig.getString(propertyName)
    val springTimeout = environment.getProperty(propertyName)
    val configTimeout = config.getString(propertyName)

    defaultTimeout should not be null
    springTimeout should not be null

    defaultTimeout should not equal springTimeout
    configTimeout shouldBe springTimeout
  }

}


object AkkaConfigAutoConfigurationSpec {

  val TEST_URL = new URL("http://google.com")

  //@Import(Array(classOf[AkkaConfigAutoConfiguration]))
  @PropertySource(Array("classpath:AkkaConfigAutoConfigurationSpec.properties"))
  @Configuration
  class Configuration {

    @Bean
    def testUrl = TEST_URL

  }

}

