package com.dataengi.crm.common.repositories

trait AutoIncRepository[T] extends Repository[T] {

  override type Key = Long

}
