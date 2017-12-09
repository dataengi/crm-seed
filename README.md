# CRM Seed

[![Join the chat at https://gitter.im/dataengi/crm-seed](https://badges.gitter.im/dataengi/crm-seed.svg)](https://gitter.im/dataengi/crm-seed?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

![travis_ci](https://travis-ci.org/dataengi/crm-seed.svg?branch=master)

Scala seed project for starting new customer relationship management system.


## Brief description
Almost every customer relationship management (CRM) software in different domains usually require to develop functions for managing customer contacts as part of it. These functions include create, edit, share customer information, grouping contacts with categories and filtering with some criteria, user access management to contacts inside software, etc. Proposed project could be used as seed project for building fully functional CRM software.

### Architecture

Project has a multilayer architecture with loosely coupled layers, which allows easy to change the behaviors of each layer without significant changes in others. In such design, there is no big deal to change UI presentation, or to add custom behaviour to data model dictated by business-logic, or to modify access to data storage.
It was designed with the thought of being ready to customisations.

Project consists of two major parts: back end written with Scala and Play Framework as RESTful service, and web-UI developed as one page web app on Angular.

Application:
* [Server](https://github.com/dataengi/crm-seed)
* [UI](https://github.com/dataengi/crm-seed-ui)


### Use cases

Originally CRM refers to practices, strategies and technologies that companies use to manage and analyse customer interactions and data throughout the production lifecycle, with the goal of improving business relationships with customers, assisting in customer retention and driving sales growth. CRM software consolidates customer information and documents into a single CRM database so business users can more easily access and manage it. An integral part of every CRM is management of customer information. 

Project contains all typical use cases for user management and customer management. 

User management panel contains:

* user authorisation
* users list
* user invitation
* user activation/deactivation

Contacts panel contains:

* create/edit/delete contact
* filter/search contacts
* group/ungroup contacts


## Running in presentation mode

For make an opinion and to play around with application you can simply use prepared docker images of all application parts. To do so, run `docker-compose` from application directory
```shell
$ docker-compose up
```  
Application will be listening on localhost port 80.  
[Go to the running app](http://localhost:80)

You can login with:
```yaml
email: admin
password: admin
```

## Running in dev mode

### Requirements
To compile application server-side the Scala SDK required.
Database and UI part of application are dockerized. Look at `docker-compose-dev.yaml` for information.    

### Build 

Building server application (from project directory)

```sbtshell
$ sbt compile
```

### Running tests

To run tests
```sbtshell
$ sbt test
```

### Running 

Run `docker-compose` with `docker-compose-dev.yml` compose file:  

```shell
$ docker-compose --file docker-compose-dev.yml run
```

And don't forget to specify in docker-compose file a ip address of your host in variable `BACKEND_URL` in format `http://10.10.10.56:9000`. In opposite case crm_ui will don't know where to find a server.  

Run application server with: 
```sbtshell
$ sbt run
```

Application will be started at [localhost](http://localhost:80)

Default admin account:
```yaml
email: admin
password: admin
```

## Integration
### Endpoints
Web UI use HTTP request to the server for data interaction with json. 

Customer contacts managements:
```yaml
GET     /api/v1/contacts/all
GET     /api/v1/contacts/owner
GET     /api/v1/contacts/:id
POST    /api/v1/contacts/create
PUT     /api/v1/contacts/update/:id
PUT     /api/v1/contacts/update/:id/advertiser/:advId
POST    /api/v1/contacts/delete
DELETE  /api/v1/contacts/delete/:id
DELETE  /api/v1/contacts/delete/:id/advertiser
GET     /api/v1/contacts/contactsbook/owner
GET     /api/v1/contacts/contactsbook/owners
GET     /api/v1/contacts/contactsbook/owner/:id
```

Customer groups managements:
```yaml
GET     /api/v1/contacts/group/get/:id
POST    /api/v1/contacts/group/create
POST    /api/v1/contacts/group/add
POST    /api/v1/contacts/group/delete
POST    /api/v1/contacts/group/update
DELETE  /api/v1/contacts/group/delete/:id
GET     /api/v1/contacts/group/contactsbook/:id
```
Full list can be found in `conf/routes` and `conf/auth.routes` files.

### Authorization

In order to restrict access to information, the authorization module is integrated. There is a few predefined user roles, with invitation system for new users. 
Authorization are build with Silhouette library, supports different authentication methods. Configuration parameters are stored in `conf/aplication/silhouette.conf` file. 
Default administration account are stored in `conf/aplication/root.conf` file.
Note: for production use is strictly recommended to change all parameters with `[changeme]` values in silhouette configuration file.

### Database

Application use Slick library for interaction with database. Such approach allow easy substitution of RDBM from one to another. All db specific parameters are stored in `conf/application/databases.conf` file.


### Invitation 

Adding a new user to the system occurs by sending an invitation to him by email. For sending email gmail smtp are used. To using this feature you have to provided any real google account credentials, by editing file `conf\application.mailer.conf`
```yaml
play.mailer.user = "your-email"
play.mailer.password = "your-email-password"
play.mailer.mock = false
```   
or provide environment variable `PLAY_MAILER_USER`,  `PLAY_MAILER_PASSWORD` and `PLAY_MAILER_MOCK` with appropriate values.


## Built With

* [Play](https://www.playframework.com/documentation/2.5.x/Home) - Lightweight framework to build application with Scala
* [Slick](http://slick.lightbend.com/docs/) - Modern database query and access library for Scala
* [Silhouette](https://github.com/mohiva/play-silhouette) - Authentication library for Play Framework applications that supports different authentication methods
* [Scalty](https://github.com/awesome-it-ternopil/scalty) - Library with useful wrappers for monads transformer EitherT[Future, AppError, T] for Scala


## Contributing

We are welcomes questions via our issues tracker. We also greatly appreciate fixes, feature requests, and updates; before submitting a pull request, please visit contributor [guidelines](CONTRIBUTING.md).

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details

