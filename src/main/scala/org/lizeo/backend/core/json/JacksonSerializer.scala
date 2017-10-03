package org.lizeo.backend.core.json

import java.text.SimpleDateFormat

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationFeature, MapperFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import java.lang.reflect.{ParameterizedType, Type => JType}

import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.scaladsl.model.ContentTypeRange
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

import scala.collection.immutable.Seq
import scala.reflect.runtime.universe._

object JacksonSupport extends JacksonSupport {
  val akkaHttpMapper: ObjectMapper = new ObjectMapper().registerModule(DefaultScalaModule)
  akkaHttpMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"))
  akkaHttpMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  akkaHttpMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
  akkaHttpMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
  akkaHttpMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)
  akkaHttpMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
  akkaHttpMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
  akkaHttpMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
  akkaHttpMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
  akkaHttpMapper.setSerializationInclusion(Include.NON_NULL)
}

/**
  * JSON marshalling/unmarshalling using an in-scope Jackson's ObjectMapper
  */
trait JacksonSupport {
  import JacksonSupport._

  def unmarshallerContentTypes: Seq[ContentTypeRange] = List(`application/json`)

  private val jsonStringUnmarshaller =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(unmarshallerContentTypes: _*)
      .mapWithCharset {
        case (ByteString.empty, _) => throw Unmarshaller.NoContentException
        case (data, charset)       => data.decodeString(charset.nioCharset.name)
      }

  private def typeReference[T: TypeTag] = {
    val t      = typeTag[T]
    val mirror = t.mirror
    def mapType(t: Type): JType =
      if (t.typeArgs.isEmpty) {
        mirror.runtimeClass(t)
      } else {
        new ParameterizedType {
          def getRawType = mirror.runtimeClass(t)

          def getActualTypeArguments = t.typeArgs.map(mapType).toArray

          def getOwnerType = null
        }
      }

    new TypeReference[T] {
      override def getType = mapType(t.tpe)
    }
  }

  /**
    * HTTP entity => `A`
    */
  implicit def unmarshaller[A](implicit ct: TypeTag[A], om: ObjectMapper = akkaHttpMapper): FromEntityUnmarshaller[A] = {
    jsonStringUnmarshaller.map(data => om.readValue(data, typeReference[A]).asInstanceOf[A])
  }


  /**
    * `A` => HTTP entity
    */
  implicit def marshaller[Object](implicit om: ObjectMapper = akkaHttpMapper): ToEntityMarshaller[Object] =
    Jackson.marshaller[Object](om)
}