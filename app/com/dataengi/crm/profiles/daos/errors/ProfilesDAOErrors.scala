package com.dataengi.crm.profiles.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

object ProfilesDAOErrors {

  val ProfileNotFound = ProfilesDAOError(s"Profiles with not found in the com.dataengi.crm.profiles table")

  def profilesWithIdNotFound(id: Long) = ProfilesDAOError(s"Profiles with id=$id not found in the com.dataengi.crm.profiles table")

}

case class ProfilesDAOError(description: String) extends AppErrorResult
