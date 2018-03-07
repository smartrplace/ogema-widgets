# ogema-widgets
Simple web framework for OGEMA

The OGEMA widgets framework provides a small web framework for building OGEMA user pages.

API
	* ogema-gui-api
Implementation
	* ogema-js-bundle (+ extended API)
	* widget-collection (The base widgets)
Extended
	* widget-extended (Widgets adapted to OGEMA Resources and ResourcePatterns)
	* widget-experimental (Widgets in experimental stadium)
	* icon-service-impl (Retrieve icons associated to OGEMA Resource types)
	* name-service-impl (Defines naming rules for OGEMA Resources)
	* messaging (Tools for messaging, and implementation of basic messaging services)
	
Separate tools
	* widget-tools: A set of useful OGEMA tool apps that provide a GUI, mostly based on widgets
	* simulations: Provides an API for simulation providers, and a GUI that shows all available simulations in the system (based on widgets)
	* widget-exam-base: Test base and tests for the widgets framework
	
Sample apps can be found in the folder widget-apps.	