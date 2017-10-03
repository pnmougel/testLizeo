package org.lizeo.backend.core.elasticsearch

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import org.elasticsearch.common.settings.Settings
import org.lizeo.backend.core.configuration.Config

/**
  * Created by nico on 02/10/17.
  */
object EsClient {
  private val settings = Settings.builder().put("cluster.name", Config.esClusterName).build()
  private val elasticSearchClientUri = ElasticsearchClientUri(Config.esUrlDomain, Config.esPort)
  val client: ElasticClient = ElasticClient.transport(settings, elasticSearchClientUri)
}
