package com.dataengi.crm.common.daos
import scala.language.higherKinds

trait ContaineredBaseDAO[Key, Value, Container[_]] {
  def get(key: Key): Container[Value]

  def getOption(key: Key): Container[Option[Value]]

  def add(obj: Value): Container[Key]

  def add(values: List[Value]): Container[List[Key]]

  def update(obj: Value): Container[Unit]

  def delete(key: Key): Container[Unit]

  def all: Container[List[Value]]

}
