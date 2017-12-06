package com.dataengi.crm.contacts.models

case class Address(street: Option[String],
                   state: Option[String],
                   country: Option[String],
                   city: Option[String],
                   zipCode: Option[String],
                   id: Option[Long] = None)
{
  def isEmpty = street.isEmpty && state.isEmpty && country.isEmpty && city.isEmpty && zipCode.isEmpty

  def nonEmpty = !isEmpty
}
