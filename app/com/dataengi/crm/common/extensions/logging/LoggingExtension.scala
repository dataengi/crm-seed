package com.dataengi.crm.common.extensions.logging
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.logging.LoggingExtension.XorTypeLoggingExtension

trait LoggingExtension {

  implicit def xorTypeLoggingExtension[T](xorType: XorType[T]): XorTypeLoggingExtension[T] =
    new XorTypeLoggingExtension[T](xorType)

}

object LoggingExtension {

  final class XorTypeLoggingExtension[T](val xorType: XorType[T]) extends AnyVal {

    def logResult: String =
      if (xorType.isRight) s"success=[" + xorType.value.toString + "]"
      else s"failed=[" + xorType.leftValue.toString + "]"

  }

}
