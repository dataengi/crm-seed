package com.dataengi.crm.common.context

import scalty.types.{AllTypesAlias, AllTypesExtensions, or}

package object types extends AllTypesAlias with AllTypesExtensions with PlayResult with PlayResultExtensions {

  val EmptyOrValue = or.EMPTY_OR

}
