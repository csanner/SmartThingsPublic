/**
 *  OSRAM Lightify Dimming Switch Binder
 *
 *  Copyright 2016 Michael Hudson
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
    name: "OSRAM Lightify Dimming Switch Binder",
    namespace: "csanner",
    author: "Chris Sanner",
    description: "Use to bind dimmable lights/switches in ST to the buttons on a OSRAM Lightify Dimming Switch - based heavily on the work of Michael Hudson",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")



preferences {
  page(name: "selectActions")
}

def selectActions() {
    dynamicPage(name: "selectActions", title: "Select Routines to Execute", install: true, uninstall: true) {
        section("Which OSRAM Lightify Dimming Switch..."){
          input(name: "switch1", type: "capability.button", title: "Which switch?", required: true)
        }

        // get the available actions
            def actions = location.helloHome?.getPhrases()*.label
            if (actions) {
            // sort them alphabetically
            actions.sort()
                    section("Hello Home Actions") {
                            log.trace actions
                // use the actions as the options for an enum input
                input "up_press_action", "enum", title: "Select an action to execute", options: actions, required: true
                input "down_press_action", "enum", title: "Select an action to execute", options: actions, required: true
                input "up_hold_action", "enum", title: "Select an action to execute", options: actions
                input "down_hold_action", "enum", title: "Select an action to execute", options: actions
                    }
            }
    }
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
  subscribe(switch1, "button.pushed", buttonPushedHandler)
  subscribe(switch1, "button.held", buttonHeldHandler)
  //subscribe(switch1, "button.released", buttonReleasedHandler)
}

def buttonPushedHandler(evt) {
  def buttonNumber = parseJson(evt.data)?.buttonNumber
  if (buttonNumber==1) {
    log.debug "Button 1 pushed (executing ${settings.up_press_action})"
    location.helloHome?.execute(settings.up_press_action)
  } else {
    log.debug "Button 2 pushed (executing ${settings.down_press_action})"
    location.helloHome?.execute(settings.down_press_action)
  }
}

def buttonHeldHandler(evt) {
  log.debug "buttonHeldHandler invoked with ${evt.data}"
  //def ButtonNumber = evt.jsonData.buttonNumber
  def buttonNumber = parseJson(evt.data)?.buttonNumber
  //def levelDirection = parseJson(evt.data)?.levelData[0]
  //def levelStep = parseJson(evt.data)?.levelData[1]
  if (buttonNumber==1) {
    log.debug "Button 1 held (executing ${settings.up_hold_action})"
    location.helloHome?.execute(settings.up_hold_action)
  } else {
    log.debug "Button 2 held (executing ${settings.down_hold_action})"
    location.helloHome?.execute(settings.down_hold_action)
  }
}

//def buttonReleasedHandler(evt) {
//  log.debug "buttonReleasedHandler invoked with ${evt.data}"
//}
