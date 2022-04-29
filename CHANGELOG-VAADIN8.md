# Vaadin 8 extended maintenance version changelog

## Vaadin 8.15.1

* Fixed an issue where Grid was moving focus away from external input controls when the datasource contents were updated
* Fixed an issue where manual field binding configurations might get overwritten by automatic binding logic
* Field level verification in Binder is no longer run twice

## Vaadin 8.15.0

* Change license from Apache 2.0 to CVDLv4
* Add more intuitive resynchronization error message
* fix: Allow scrolling away from a Grid using touch
* fix: Throw exception when attempting to merge BeanPropertySets with identical keys but different value types
