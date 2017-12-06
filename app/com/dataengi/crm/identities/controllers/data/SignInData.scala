package com.dataengi.crm.identities.controllers.data

case class SignInData(email: String, password: String, rememberMe: Boolean = true)
