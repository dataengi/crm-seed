package com.dataengi.crm.common.extensions.formatters

import play.api.libs.json._

object JsonFormatter {
  def enumFormat[T <: Enumeration](enum: T): Format[T#Value] = new EnumFormatter[T](enum)

  class EnumFormatter[T <: Enumeration](enum: T) extends Format[T#Value] {
    override def writes(o: T#Value): JsValue = JsString(o.toString)

    override def reads(json: JsValue): JsResult[T#Value] = json match {
      case JsString(value) => {
        try {
          JsSuccess(enum.withName(value))
        } catch {
          case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$value'")
        }

      }
      case _ => JsError(s"Invalid JSON:$json. Error in '${enum.getClass}' field. Possible value:${enum.values}")
    }
  }

}
