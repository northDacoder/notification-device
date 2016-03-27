/**
 *  HTML Solution Module Example
 *
 *  Copyright 2016 smartthings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "HTML Solution Module Example",
    namespace: "exampledocs",
    author: "smartthings",
    description: "An example showing several capabilities of HTML solution modules.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Beta/Cat-Beta.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Beta/Cat-Beta@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Beta/Cat-Beta@2x.png")


preferences {
    section("Select a switch") {
        input "button", "capability.switch", required: true, multiple: false
    }
    section ("Users & Notifications") {
        input("recipients", "contact", title: "Select Users & Notifications",
              required: false, multiple: true) {
            input "pushNotification", "bool", title: "Send push notifications",
                   required: false, defaultValue: true
            input "phone1", "phone", title: "Phone number?", required: false
        }
    }
}

cards {
    card(name: "Button", type: "html", action: "home") {}
    card(name: "Notify", type: "html", action: "notify") {}
}

// Every HTML card needs a mapping, along with every endpoint we call from our
// client JavaScript code.
mappings {
	// CARDS MAPPING START:
    path("/home") {
        action: [GET: "home"]
    }
    path("/notify") {
        action: [GET: "getNotifyCard"]
    }
    // CARDS MAPPING END

    // endpoint that will schedule a notification to be sent
    path("/sendNotification") {
        action: [POST: "sendNotification"]
    }

    // endpoint to get initial data needed for rendering
    path("/getInitialData") {
        action: [GET: "getInitialData"]
    }

    // endpoint to toggle the switch
    path("/toggleSwitch") {
        action: [POST: "toggleSwitch"]
    }
}

// called by the ST framework when this app is installed
def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

// called by the ST framework when an instance of this app
// is updated, but going into the configuration of the app
// (to change some settings for the installed instance, for
// example).
def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

// Utility method we call from both installed() and updated()
// to handle some common tasks like creating subscriptions and
// updating the solution summary
def initialize() {
    subscribe(button, "switch", buttonEventHandler)
    setSummary()
}

// Updates the summary of this solution module, for display
// on the solution module dashboard
def setSummary() {
    def summaryData = []
    if (button.currentSwitch == "on") {
    	summaryData << [icon:"indicator-dot-green",iconColor:"#79b821",value:"switch is on"]
    } else {
    	summaryData << [icon:"indicator-dot-red",iconColor:"#e86d13",value:"switch is off"]
    }

    // generate a SOLUTION_SUMMARY event that can include status icon indicators
    sendEvent(linkText:'link text', descriptionText: 'description text',
          eventType:"SOLUTION_SUMMARY",
          name: "summary",
          value: "switch is ${button.currentSwitch}",
          data: summaryData,
          displayed: false)
}

// Card definition for the button tab - displays the button
// and a clickable button to go to the device details page.
def home() {
    log.debug "will render home html"
    html('views/home.html', [
        page: [
            title: 'HTML Example',
            description: 'An example HTML SmartApp'
        ]
    ])
}

// Card definition for the notify card.
def getNotifyCard() {
    log.debug "in notify"
    html('views/notify.html', [
        title: 'Notify card',
        description: 'A card to send notifications'
    ])
}

// Gets the initial state of the configured switch
// The return type is a map, which will be serialized into JSON for conumption
// by the client.
def getInitialData() {
    def curr = button.currentValue("switch")
    def deviceId = button.id

    log.debug "in getInitialData, current switch: $curr"
    log.debug "in getInitialData, device id is: $deviceId"
    [name: "initialData", currentState: curr, dId: deviceId]
}

// Method to send a notification to configured recipients.
// This will be called by the client JS when the send notification "button" is
// pressed, by making an ST.request call to the endpoing mapped to this method.
def sendNotification() {
    log.debug "Will schedule notification to send in 60 seconds"

    runIn(60, sendIt)
}



// Sends a notification to the configured users.
// If a push notification is sent, it will link to this solution module and card,
// so that when the notification is opened, the SmartThings mobile app will open on
// this solution module and card.
def sendIt() {
    def message = "This is a notification message"
    if (recipients) {
        // if the user used the contact book feature
        log.debug "will send notification to contacts using sendNotificationToContacts"
        log.debug "notification will link to solution module"
        sendNotificationToContacts(message, recipients, [view: [name: "SOLUTION",
            data: [moduleName: "Smart_Home_Monitor",
            moduleId: app.moduleId, card: 1]]])
     } else {
        // if the user does not have contact book enabled, still send the notification,
        // passing the view as options as well
        log.debug "will send notification to contacts using sendNotification"
        log.debug "notification will link to solution module"
        def options = [
            method: (pushNotification != false && phone1) ? "both" :
                    (pushNotification != false ? "push" : "sms"),
            phone: phone1,
            view: [name: "SOLUTION", data: [moduleName: "Smart_Home_Monitor",
                   moduleId: app.moduleId, card: 2]]
        ]
        sendNotification(message, options)
    }
}

// Called when the configured switch changes states (becomes on or off).
// Sends an event that can be consumed by the client so that it can update
// the UI to show the correct switch state.
def buttonEventHandler(evt) {
    // Create and send an event that will be consumed by our JavaScript.
    // The JS will then handle updating the view to reflect the state of the switch.
    sendEvent(name: "buttonState", value: button.currentValue("switch"))
}

// Toggle the configured switch. A switch that is on will get turned off,
// and a switch that is off will be turned on.
// This method is called by the client JS through the configured mapping
// when the user presses the button.
def toggleSwitch() {
    if (button.currentValue("switch") == "on") {
        log.debug "in toggleSwitch, will turn switch off"
        button.off()
    } else {
        log.debug "in toggleSwitch, will turn switch on"
        button.on()
    }
}
