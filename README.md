Package Drone
=======

A package manager repository for OSGi / Java (any maybe more).

This project was started to scratch the itch that Maven Tycho can compile OSGi bundles with Eclipse, but not deploy it "somewhere". Everything has to somehow copied around with B3, P2Director or other tools.

The idea is to have a workflow of Tycho Compile -> publish to a repo using plain maven -> Tycho Compile (using deployed artifacts via P2). And some repository tools like cleanup, freezing, validation.

Also if you start a thing like this, it should somehow be extensible, since the next thing I would like to see it creating APT and YUM repositories.

Warning: This project is as its early stages. Try it out, find it useful or wait until it is stable enough to work for you.

Also see the wiki for more information: https://github.com/ctron/package-drone/wiki

Intended use cases
----------------

* Act as local OSGi bundle repository
 * Let Maven deploy its artifacts (Tycho or Bndtools) to the repository
 * Extract OSGi relevant metadata
 * Provide access as P2 and OSGi R5 repository system
* Provide support for local development workflow
 * Different integration and release channels
 * Cleanup, verify and freeze

What it currently can do
----------------

* Maven Tycho can deploy bundles and features (NOT repositories)
* OSGi bundles can also be uploaded by plain maven or through the Web UI
* View bundle and feature information through the Web UI
* Eclipse P2 can fetch meatdata, bundles and features
* Store artifacts and metadata in a MariaDB (possibly MySQL) database
* The required OSGi metadata can either be used from the Maven Tycho build, or can be generated by Package Drone itself. This can be selected on a per channel basis.
* Automatically create P2 features and categories from the list bundles in a channel
* Automatically create Eclipse Source Bundles from Maven source attachments - so you can upload an OSGi bundle from Maven Central and attach its source attachment and Package Drone creates an Eclipse Source bundle which is also accessible by Eclipse P2.
* Provide access to the channels using the OSGi R5 XML index format
* Authorize users and optionally allow self-registration
* Provide a very simple role based access model
* Use "deploy keys" to allow deploying to a repository channel

Known bugs and limitation
----------------

* The concept of P2 binaries is missing
* Automatic cleanup is missing
* There is deadlock in Equinox/EclipseLink when updating the JPA database settings. This is a bug in Equinox and will be fixed in Mars M6.
* PostgreSQL is not supported, since PostgreSQL does not support Blobs in a sane way

What it currently cannot do
----------------

A lot of things. If there is time it will be implemented. Or you can help by contributing ;-)

Hopefully some time Package Drone can:

* Provide access to bundles by an OBR layout (it can do R5)
* Act as a fully functional Maven M2 repository

What it is not designed to do
-----------------

This is a list of things Package Drone is not designed to be do, as of now! Maybe this changes in the future, although this is highly unlikely at the time of writing:

* Act as "P2 central" - a replication of Maven Central for P2 or OSGi R5 repositories

