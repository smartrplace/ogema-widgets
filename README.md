# ogema-widgets

The OGEMA widgets framework provides a small web framework for building [OGEMA](https://github.com/ogema/ogema) user pages. 

## Getting started

Follow the steps below to run OGEMA with the widgets framework.
Prerequisite: Java 8

1) Download the [demokit](https://ogema-source.net/demokit_widgets.zip)
2) Go to the demokit rundirectory and execute the start.sh (for bash compatible shells) script or the start.cmd (Windows shell)
3) Open the site https://localhost:8443/ogema/index.html in your Browser and accept the warning about the untrusted certificate
4) Login with default credentials master/master

## Developing apps

Documentation: [https://ogema-source.net/apidocsextended/](https://ogema-source.net/apidocsextended/)
Tutorials and resources about OGEMA are available on the OGEMA Wiki and the community Wiki
* [https://ogema-source.net/wiki](https://ogema-source.net/wiki)
* [https://community.ogema-source.net](https://community.ogema-source.net)

For widget-specific information see [https://community.ogema-source.net/xwiki/bin/view/Main/The%20OGEMA%20Widgets%20framework/](https://community.ogema-source.net/xwiki/bin/view/Main/The%20OGEMA%20Widgets%20framework/)

## Examples

Example apps can be found in the subfolder src/widget-apps and in the [tutorial repository](https://github.com/ogema/tutorial).

## Build yourself

Prerequisite: Maven 3

Go to the src folder and execute `mvn clean install -DskipTests`

## Components
### API
* ogema-gui-api
### Implementation
* ogema-js-bundle (+ extended API)
* widget-collection (The base widgets)
* widget-extended (Widgets adapted to OGEMA Resources and ResourcePatterns)
* widget-experimental (Widgets in even more experimental stadium)
* icon-service-impl (Retrieve icons associated to OGEMA Resource types)
* name-service-impl (Defines naming rules for OGEMA Resources)
* messaging (Tools for messaging, and implementation of basic messaging services)
	
### Tools
* widget-tools: A set of useful OGEMA tool apps that provide a GUI, mostly based on widgets
* simulations: Provides an API for simulation providers, and a GUI that shows all available simulations in the system (based on widgets)
* widget-exam-base: Test base and tests for the widgets framework
	
### Sample apps 
* widget-apps
