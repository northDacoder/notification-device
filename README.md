WHAT THE APP DOES
=================

This HTML Solution Module SmartApp demonstrates the following:

* Using HTML to display the status of a switch that stays in sync with the actual switch, as well as allows toggling of the switch by pressing it.
* Sending a SOLUTION_SUMMARY event to update the dashboard data for this app.
* Linking to a device details view from the SmartApp.
* Sending JavaScript logging messages to SmartThings to be logged in the SmartThings logs.
* Sending notifications that will link to this HTML Solution SmartApp and a specific card within it.


SETUP
=====

To use this example app, create a new SmartApp using the following steps:

1.  Create new SmartApp "From Form"
2.  Name the app "HTML Solution Module Example". Fill out any other required fields, but they will be overridden later.
3.  In the "Solution Modules" section of the form:
	+ In "Platform" input, select "Default" dashboard
	+ Check the "Show module in dashboard even when app has no children" checkbox
4.  Enable OAuth
5.  Click "Create"
6.  Paste html-notifications-device-details.groovy code into main editor.
7.  Using file explorer (on the left side of the editor, it may be collapsed by default) upload file action:
	+ Upload app.js and choose "JAVASCRIPT" as the file type.
	+ Upload app.cs and choose "CSS" as the file type.
    + Upload head.html, home.html, and notify.html and choose "VIEW" as the file type.
8.  Save and Publish the SmartApp
9.  Install the app via the mobile client.
10.  The app should now be in the main list on your Dashboard.

NOTES
=====

* The source code for this SmartApp is heavily commented to help you understand the core concepts.
* This app should not serve as a reference or guide for your specific HTML or UX design. The purpose of this app is to show some of the capabilities as outline above; not to demonstrate a professional-grade looking UX.
