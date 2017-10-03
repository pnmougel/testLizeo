package org.lizeo.backend.core.json

import java.text.SimpleDateFormat

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonFilter, JsonInclude}
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ser.impl.{SimpleBeanPropertyFilter, SimpleFilterProvider}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.sksamuel.elastic4s.searches.RichSearchHit
import com.sksamuel.elastic4s.{Hit, HitAs, HitReader, Indexable}

import scala.util.control.NonFatal


/**
  * Created by nico on 18/11/16.
  * This class adds the Include.NON_EMPTY flag to the serializer / unserializer
  * This flag is required to avoid serialization of the _id field of a case class
  * for the index operation
  */

object  ElasticJackson {
  object Implicits {

    def buildDefaultMapper(): ObjectMapper with ScalaObjectMapper = {
      val mapper: ObjectMapper with ScalaObjectMapper = new ObjectMapper with ScalaObjectMapper
      mapper.registerModule(DefaultScalaModule)

      mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"))
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
      mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
      mapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
      mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

      // Disable inclusion of empty fields (i.e. None) to avoid errors for _id field
      mapper.setSerializationInclusion(Include.NON_EMPTY)
      mapper
    }

    val mapper: ObjectMapper with ScalaObjectMapper = buildDefaultMapper()
    val mapperForUpdate: ObjectMapper with ScalaObjectMapper = buildDefaultMapper()

    // Create a specific mapper for the update operation in order to avoid removing _id and _score field
    @JsonFilter("filter properties by name")
    class PropertyFilterMixIn {}

    // Serialize empty field to be able tu unset a value
    // If changed, ensure for example that it is possible to remove the default shipping for a seller
    mapperForUpdate.setSerializationInclusion(Include.NON_NULL)
    mapperForUpdate.addMixIn(classOf[Object], classOf[PropertyFilterMixIn])
    val ignorableFields = Array("_id", "_score")
    val filter = new SimpleFilterProvider().addFilter("filter properties by name", SimpleBeanPropertyFilter.serializeAllExcept("_id", "_score"))

    def JacksonJsonUpdateIndexable[T]: Indexable[T] = new Indexable[T] {
      override def json(t: T): String = {
        mapperForUpdate.writer(filter).writeValueAsString(t)
      }
    }

    implicit def JacksonJsonIndexable[T]: Indexable[T] = new Indexable[T] {
      override def json(t: T): String = {
        mapper.writeValueAsString(t)
      }
    }

    implicit def JacksonJsonHitReader[T: Manifest]: HitReader[T] = new HitReader[T] {
      override def read(hit: Hit): Either[Throwable, T] = {
        try {
          val node = mapper.readTree(hit.sourceAsString).asInstanceOf[ObjectNode]
          if (!node.has("_id")) node.put("_id", hit.id)
          if (!node.has("_type")) node.put("_type", hit.`type`)
          if (!node.has("_index")) node.put("_index", hit.index)
          if (!node.has("_version")) node.put("_version", hit.version)

          if (!node.has("_timestamp")) hit.sourceFieldOpt("_timestamp").collect {
            case f => f.toString
          }.foreach(node.put("_timestamp", _))
          Right(mapper.readValue[T](mapper.writeValueAsBytes(node)))
        } catch {
          case NonFatal(e) => Left(e)
        }
      }
    }

    @deprecated("use HitReader which can be used for both get and search APIs", "5.0.0")
    implicit def JacksonJsonHitAs[T: Manifest]: HitAs[T] = new HitAs[T] {
      override def as(hit: RichSearchHit): T = {
        val node = mapper.readTree(hit.sourceAsString).asInstanceOf[ObjectNode]
        if (!node.has("_id")) node.put("_id", hit.id)
        if (!node.has("_type")) node.put("_type", hit.`type`)
        if (!node.has("_index")) node.put("_index", hit.index)
        if (!node.has("_score")) node.put("_score", hit.score)
        if (!node.has("_version")) node.put("_version", hit.version)
        if (!node.has("_timestamp")) hit.fieldOpt("_timestamp").collect {
          case f => f.value.toString
        }.foreach(node.put("_timestamp", _))
        mapper.readValue[T](mapper.writeValueAsBytes(node))
      }
    }
  }
}

