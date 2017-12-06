package com.dataengi.crm.common.repositories

import java.util.concurrent.atomic.AtomicInteger

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.errors.ValueNotFound

abstract class BaseInMemoryRepository[T] extends AutoIncRepository[T] with KeyExtractor[T, Long] {

  protected val repository = TrieMap[Key, T]()

  private val atomicInteger = new AtomicInteger()

  override protected def getKey(value: T): Key = atomicInteger.getAndIncrement().toLong

  protected def beforeSave(key: Key, value: T): T = value

  override def getAll(): Or[List[T]] =
    Future {
      repository.values.toList
    }.toOr

  override def remove(id: Key): Or[Unit] =
    Future {
      repository.remove(id)
    }.toEmptyOr

  override def add(value: T): Or[Key] =
    Future {
      val key = getKey(value)
      repository.put(key, beforeSave(key, value))
      key
    }.toOr

  override def add(values: List[T]): Or[List[Key]] =
    values.traverseC(add)

  override def get(id: Key): Or[T] =
    repository.get(id) match {
      case Some(value) => value.toOr
      case None        => ValueNotFound(id).toErrorOr
    }

  override def update(id: Key, value: T): Or[Unit] =
    Future {
      repository.update(id, value)
    }.toOr

  override def getOption(id: Key): Or[Option[T]] =
    Future {
      repository.get(id)
    }.toOr

}
