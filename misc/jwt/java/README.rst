---------------------------------------
Livefyre Authentication Token Generator
---------------------------------------

This library can be used in Java to generate authentication 
tokens as described in:

https://github.com/Livefyre/livefyre-docs/wiki/Livefyre-authentication-token

=========================
Build and run using Maven
=========================

A `pom.xml` is provided for simple compilation. If you have Maven installed, 
simply run:

    $ mvn package

A simple command line tool is provided for testing. After packaging with Maven, you can execute it as:


    $ java -jar target/jwt-token-1.0-jar-with-dependencies.jar com.livefyre.jwt.AuthTokenTool

The program will output usage information.

============
Dependencies
============

This library is a simple wrapper around `jsontoken <https://code.google.com/p/jsontoken/>`_. It has no other dependencies.

