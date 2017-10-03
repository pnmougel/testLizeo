package org.lizeo.backend.core.rejections

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{Allow, `Content-Range`, `WWW-Authenticate`}
import akka.http.scaladsl.model.{ContentRange, StatusCodes}
import akka.http.scaladsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import akka.http.javadsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import akka.http.scaladsl.server._
import com.fasterxml.jackson.databind.JsonMappingException
import org.lizeo.backend.core.json.JacksonSupport

/**
  * Created by nico on 10/12/16.
  */



object JsonRejectionHandlers extends Directives with JacksonSupport {
  implicit def jsonRejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case ApiRejection(error, status) => {
          complete((status, ErrorMessage(error)))
        }
        case MalformedRequestContentRejection(message, cause: JsonMappingException) => {
          // Build the error message from json parsing error message
          val elems = message.split("\n").head.split("requirement failed: ")
          complete((BadRequest, ErrorMessage(elems.applyOrElse(1, { _: Int => message }))))
        }
        case AuthorizationFailedRejection ⇒
          complete((Forbidden, ErrorMessage("The supplied authentication is not authorized to access this resource")))
        case MalformedFormFieldRejection(name, msg, _) ⇒
          complete((BadRequest, ErrorMessage("The form field '" + name + "' was malformed:\n" + msg)))
        case MalformedHeaderRejection(headerName, msg, _) ⇒
          complete((BadRequest, ErrorMessage(s"The value of HTTP header '$headerName' was malformed:\n" + msg)))
        case MalformedQueryParamRejection(name, msg, _) ⇒
          complete((BadRequest, ErrorMessage("The query parameter '" + name + "' was malformed:\n" + msg)))
        case MalformedRequestContentRejection(msg, _) ⇒
          complete((BadRequest, ErrorMessage("The request content was malformed:\n" + msg)))
        case MissingCookieRejection(cookieName) ⇒
          complete((BadRequest, ErrorMessage("Request is missing required cookie '" + cookieName + '\'')))
        case MissingFormFieldRejection(fieldName) ⇒
          complete((BadRequest, ErrorMessage("Request is missing required form field '" + fieldName + '\'')))
        case MissingHeaderRejection(headerName) ⇒
          complete((BadRequest, ErrorMessage("Request is missing required HTTP header '" + headerName + '\'')))
        case InvalidOriginRejection(allowedOrigins) ⇒
          complete((Forbidden, ErrorMessage(s"Allowed `Origin` header values: ${allowedOrigins.mkString(", ")}")))
        case MissingQueryParamRejection(paramName) ⇒
          complete((NotFound, ErrorMessage("Request is missing required query parameter '" + paramName + '\'')))
        case RequestEntityExpectedRejection ⇒
          complete((BadRequest, ErrorMessage("Request entity expected but not supplied")))
        case TooManyRangesRejection(_) ⇒
          complete((RequestedRangeNotSatisfiable, ErrorMessage("Request contains too many ranges")))
        case CircuitBreakerOpenRejection(_) ⇒
          complete(ServiceUnavailable)
        case UnsatisfiableRangeRejection(unsatisfiableRanges, actualEntityLength) ⇒
          complete((RequestedRangeNotSatisfiable, List(`Content-Range`(ContentRange.Unsatisfiable(actualEntityLength))),
            unsatisfiableRanges.mkString("None of the following requested Ranges were satisfiable:\n", "\n", "")))
        case ExpectedWebSocketRequestRejection ⇒
          complete((BadRequest, "Expected WebSocket Upgrade request"))
        case ValidationRejection(msg, _) =>
          complete((BadRequest, ErrorMessage(msg)))
        case AuthenticationFailedRejection(cause: CredentialsRejected, challenge) =>
          complete((StatusCodes.Unauthorized, ErrorMessage("invalid credentials")))
        case AuthenticationFailedRejection(cause: CredentialsMissing, challenge) =>
          complete((StatusCodes.Unauthorized, ErrorMessage("missing credentials")))
        case x => {
          complete((InternalServerError, ErrorMessage("Unhandled rejection: " + x)))
        }
      }
      .handleAll[SchemeRejection] { rejections ⇒
        val schemes = rejections.map(_.supported).mkString(", ")
        complete((BadRequest, ErrorMessage("Uri scheme not allowed, supported schemes: " + schemes)))
      }
      .handleAll[MethodRejection] { rejections ⇒
        val (methods, names) = rejections.map(r ⇒ r.supported → r.supported.name).unzip
        complete((MethodNotAllowed, List(Allow(methods)), ErrorMessage("HTTP method not allowed, supported methods: " + names.mkString(", "))))
      }
      .handleAll[AuthenticationFailedRejection] { rejections ⇒
        val rejectionMessage = rejections.head.cause match {
          case CredentialsMissing ⇒ "The resource requires authentication, which was not supplied with the request"
          case CredentialsRejected ⇒ "The supplied authentication is invalid"
        }
      // Multiple challenges per WWW-Authenticate header are allowed per spec,
      // however, it seems many browsers will ignore all challenges but the first.
      // Therefore, multiple WWW-Authenticate headers are rendered, instead.
      //
      // See https://code.google.com/p/chromium/issues/detail?id=103220
      // and https://bugzilla.mozilla.org/show_bug.cgi?id=669675
        val authenticateHeaders = rejections.map(r ⇒ `WWW-Authenticate`(r.challenge))
        complete((Unauthorized, authenticateHeaders, ErrorMessage(rejectionMessage)))
      }
      .handleAll[UnacceptedResponseContentTypeRejection] { rejections ⇒
        val supported = rejections.flatMap(_.supported)
        val msg = supported.map(_.format).mkString("Resource representation is only available with these types:\n", "\n", "")
        complete((NotAcceptable, ErrorMessage(msg)))
      }
      .handleAll[UnacceptedResponseEncodingRejection] { rejections ⇒
        val supported = rejections.flatMap(_.supported)
        complete((NotAcceptable, ErrorMessage("Resource representation is only available with these Content-Encodings:\n" +
          supported.map(_.value).mkString("\n"))))
      }
      .handleAll[UnsupportedRequestContentTypeRejection] { rejections ⇒
        val supported = rejections.flatMap(_.supported).mkString(" or ")
        complete((UnsupportedMediaType, ErrorMessage("The request's Content-Type is not supported. Expected:\n" + supported)))
      }
      .handleAll[UnsupportedRequestEncodingRejection] { rejections ⇒
        val supported = rejections.map(_.supported.value).mkString(" or ")
        complete((BadRequest, ErrorMessage("The request's Content-Encoding is not supported. Expected:\n" + supported)))
      }
      .handleNotFound {
        complete((NotFound, ErrorMessage("The requested resource could not be found.")))
      }
      .result()
}

