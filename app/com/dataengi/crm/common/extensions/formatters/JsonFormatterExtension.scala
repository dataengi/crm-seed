package com.dataengi.crm.common.extensions.formatters

import play.api.libs.json._


object JsonFormatterExtension {

  implicit class FormatterExtension[T](val data: T) extends AnyVal {

    def toJson(implicit format: Writes[T]): JsValue = Json.toJson(data)(format)

  }

  implicit class TraversableFormatterExtension[T](val data: Traversable[T]) extends AnyVal {

    def toJson(implicit format: Writes[T]): JsValue = Json.toJson(data.map(_.toJson(format)))

  }


}