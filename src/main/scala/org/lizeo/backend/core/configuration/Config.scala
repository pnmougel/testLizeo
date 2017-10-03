package org.lizeo.backend.core.configuration

import com.typesafe.config.ConfigFactory
import org.lizeo.backend.core.logs.WithLogging

/**
  * Created by nico on 02/10/17.
  */
object Config extends WithLogging {
  private val conf = ConfigFactory.load()

  // Server configuration
  private val serverConfig = conf.getConfig("server")
  val serverPort: Int = serverConfig.getInt("port")

  // ElasticSearch configuration
  private val elasticSearchConfig = conf.getConfig("elasticsearch")
  val esUrlDomain: String = elasticSearchConfig.getString("serverDomain")
  val esPort: Int = elasticSearchConfig.getInt("port")
  val esClusterName: String = elasticSearchConfig.getString("clusterName")
}
