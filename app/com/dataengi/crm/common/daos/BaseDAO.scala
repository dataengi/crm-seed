package com.dataengi.crm.common.daos

import com.dataengi.crm.common.context.types._

trait BaseDAO[Key, Value] extends ContaineredBaseDAO[Key, Value, Or]
