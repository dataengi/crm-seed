package com.dataengi.crm.common.repositories

trait KeyExtractor[Value, Key] {

  protected def getKey(value: Value): Key

}
