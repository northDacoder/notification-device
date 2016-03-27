/**
* Namespace for our application
*/
var APP = {

  /**
  * Called when the document is ready. Requests the initial data
  * to render, as well as adds event listeners
  */
  init: function() {
    console.log("initializing...");
    ST.request("getInitialData").success(function(data) {
      console.log("data from getInitialData: " + data);
      APP.render(data.currentState, data.dId);
      APP.addBindings();
    }).GET();
  },

  /**
  * Adds appropriate CSS classes to the switch button to show
  * the state as on or off.
  */
  render: function(state, dId) {
    console.log("in render, state: " + state + ", deviceId: " + dId);
    if (state == "on") {
      $("#button").addClass("on");
    } else {
      $("#button").removeClass("on");
    }
    $("#current-state").text(state);

    // stuff the deviceId into the element. This will then be
    // used later to get the ID and load the device details view
    $("#device-details-container").data("device-id", dId);
  },

  /**
  * Binds event listeners for the elements and actions we
  * want to monitor or take action on.
  */
  addBindings: function(dId) {
    console.log("In addBindings");
    $("#button").on("click touchend", function() {
      // when the button is clicked/pressed, make a POST
      // request to the /toggleSwitch endpoint in the SmartApp
      ST.request("toggleSwitch").POST();
    });

    $("#device-details-container").on("click touchend", function() {
      // when the device details is clicked, we want to take
      // the user to the device details view.
      // We get the device-id out of the element, and use it
      // with ST.loadDevice().
      var dId = $(this).data("device-id");
      console.log("will try and go to device details with id: " + dId);
      ST.loadDevice(dId).error(function(error) {
        console.log("error loading device: " + error);
      }).EXECUTE();
    });

    $("#send-notification-container").on("touchend", function() {
      // can just use normal JavaScript confirm/alerts for popup notifications/prompts
      // could also write your own HTML/JS to do this if a more customized UI/flow
      // is desired.
      console.log("send notification button pressed, will send notification");
      var r = confirm("Send a push notification 60 seconds from now?");
      if (r == true) {
        alert("Will send a notification approx. 60 seconds after dismissing this");

        // alerts block JS execution, so this line will be reached after dismissing
        // the above alert.
        console.log("will make request to send notification");

        // Make POST request to the SmartApp that will send the notification
        ST.request("sendNotification").POST();
      } else {
        console.log("not sending notification");
      }
    });
  },

  /**
  * This method is called on all events (we delegate to it from the JavaScript
  * within the SmartApp
  */
  eventReceiver: function(evt) {
    console.log("received event with value " + evt.value);
    // We're only interested in the buttonState event for this app.
    // When we get this event, it means the state of the switch has changed.
    // We then call render with the updated value so that the UX is updated.
    switch(evt.name) {
      case "buttonState":
      APP.render(evt.value, $("#device-details").data("device-id"));
    }
  }
}

/**
* When the DOM is ready, we initialize the app
*/
$('document').ready(function() {
  console.log("in on ready");
  APP.init();
  // register event handler
  ST.addEventHandler(APP.eventReceiver);
});
