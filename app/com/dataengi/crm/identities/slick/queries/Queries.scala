package com.dataengi.crm.identities.slick.queries

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

trait Queries extends HasDatabaseConfigProvider[slick.jdbc.JdbcProfile]
