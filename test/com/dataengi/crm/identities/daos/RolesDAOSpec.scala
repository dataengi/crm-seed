package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.configurations.RolesConfiguration
import com.dataengi.crm.identities.context.AuthenticationContext
import org.scalacheck.Gen
import play.api.test.PlaySpecification
import com.dataengi.crm.identities.arbitraries.IdentitiesArbitrary
import com.dataengi.crm.identities.models.{Actions, Permission, PermissionStates, Role}
import com.dataengi.crm.common.context.types._
import cats.syntax.traverse._

class RolesDAOSpec extends PlaySpecification with AuthenticationContext with IdentitiesArbitrary {

  sequential

  lazy val rolesDAO = application.injector.instanceOf[RolesDAO]

  lazy val TestPermissions = Seq(
    Permission(Actions.Invoicing, PermissionStates.Allow),
    Permission(Actions.UsersManagement, PermissionStates.Allow),
    Permission(Actions.CreateManagerUser, PermissionStates.Allow),
    Permission(Actions.OrdersManagement, PermissionStates.Allow)
  )

  lazy val TestRole = Role("TestRole", TestPermissions, None)

  "RoleDAO" should {

    s"add role and get by id ${TestRole}" in {
      val addRoleResult = rolesDAO.add(TestRole).await()
      addRoleResult.isRight === true
      val id          = addRoleResult.value
      val getByIdRole = rolesDAO.get(id).await()

      val getAllResult = rolesDAO.all.await()
      getAllResult.isRight === true

      getByIdRole.isRight === true
      getByIdRole.value.name === TestRole.copy(id = Some(id)).name
    }

    "find exist role by name" in {
      val getByNameResult = rolesDAO.find(TestRole.name).await()
      getByNameResult.isRight === true
    }

    "add and remove" in {
      val role          = TestRole.copy(name = "TestRole2")
      val addRoleResult = rolesDAO.add(role).await()
      addRoleResult.isRight === true
      val id          = addRoleResult.value
      val getByIdRole = rolesDAO.get(id).await()
      getByIdRole.isRight === true
      getByIdRole.value.name === role.copy(id = Some(id)).name
      val removeRoleResult = rolesDAO.delete(id).await()
      removeRoleResult.isRight === true
    }

    "update role" in {
      val role             = TestRole.copy(name = "TestRoleForUpdate")
      val roleId           = rolesDAO.add(role).await().value
      val updateForRole    = TestRole.copy(name = "TestRoleForUpdate2").copy(id = Some(roleId))
      val updateRoleResult = rolesDAO.update(updateForRole).await()
      updateRoleResult.isRight === true
      val getByIdRole = rolesDAO.get(roleId).await()
      getByIdRole.isRight === true
      getByIdRole.value.name === updateForRole.name
    }

    "add many roles" in {
      val RolesCount: Int = 32
      val roles           = Gen.listOfN(RolesCount, roleArbitrary.arbitrary).sample.get
      val addRolesResult  = rolesDAO.add(roles).await()
      addRolesResult.isRight === true
      val indexedRoles = roles.zip(addRolesResult.value).map {
        case (role, id) => role.copy(id = Some(id))
      }
      val getRolesResult = addRolesResult.value.traverseC(rolesDAO.get).await()
      getRolesResult.isRight === true
      ignoreIds(getRolesResult.value) must containAllOf(ignoreIds(indexedRoles))
    }

    "add role with empty com.dataengi.crm.identities.actions" in {
      val roleWithEmptyPermissions: Role = roleArbitrary.arbitrary.sample.get.copy(permissions = Seq.empty)
      val addRoleResult                  = rolesDAO.add(roleWithEmptyPermissions).await()
      addRoleResult.isRight === true
      val getRoleResult = rolesDAO.get(addRoleResult.value).await()
      getRoleResult.isRight === true
      ignoreId(roleWithEmptyPermissions) === ignoreId(getRoleResult.value)
    }

    "find CompanyManager roles" in {
      val companyManagerRoleResult = rolesDAO.find(RolesConfiguration.CompanyManager.name).await()
      companyManagerRoleResult.isRight === true
      companyManagerRoleResult.value.isDefined === true
    }

    "find Manager roles" in {
      val managerRoleResult = rolesDAO.find(RolesConfiguration.Manager.name).await()
      managerRoleResult.isRight === true
      managerRoleResult.value.isDefined === true
    }

    "find Root roles" in {
      val rootRoleResult = rolesDAO.find(RolesConfiguration.Root).await()
      rootRoleResult.isRight === true
      rootRoleResult.value.isDefined === true
    }

    "find SalesRep roles" in {
      val salesRepRoleResult = rolesDAO.find(RolesConfiguration.SalesRepresentative.name).await()
      salesRepRoleResult.isRight === true
      salesRepRoleResult.value.isDefined === true
    }

    "get all roles" in {
      val getAllRolesResult = rolesDAO.all.await()
      getAllRolesResult.isRight === true
    }

  }

  def ignoreId(role: Role) = role.copy(id = None, permissions = role.permissions.map(_.copy(id = None)))

  def ignoreIds(roles: List[Role]) = roles.map(ignoreId)

}
