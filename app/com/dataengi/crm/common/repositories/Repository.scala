package com.dataengi.crm.common.repositories

import com.dataengi.crm.common.context.types._

import scala.concurrent.ExecutionContext

trait Repository[T] {

  implicit val executionContext: ExecutionContext

  type Key

  def getAll(): Or[List[T]]

  def remove(id: Key): Or[Unit]

  def add(value: T): Or[Key]

  def add(values: List[T]): Or[List[Key]]

  def get(id: Key): Or[T]

  def update(id: Key, value: T): EmptyOr

  def getOption(id: Key): Or[Option[T]]

}
