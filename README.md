# MQTT Hub
MQTT client app for android

## Key features
### Customizable dashboard
* Different tile types
* Tile appearance customization
<p>
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/90d1190f-926f-4ce3-95a3-175262a9b080" width="200" />
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/87b65146-f1c0-49e4-aa67-cb595d556a0d" width="200" />
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/c12a191c-1595-4384-9e2e-534396cdc1f3" width="200" />
</p>

### Dashboard edit mode
Tiles can be moved, duplicated and deleted in edit mode
<p>
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/d0ecffd7-3a03-4280-a125-9cde6e6fd7bc" width="200" />
</p>

### Dashboard management
* Multiple dashboards support
* Dashboard export and import (using json files)
<p>
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/0f728913-a12b-4b0e-8f0f-65d764d6fd77" width="200" />
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/f69e7c43-0341-47dc-9ab4-11008ff62ab5" width="200" />
</p>

### MQTT connection
* App manages MQTT connection in a foreground service
* Multiple MQTT brokers can be defined
* Received MQTT messages are stored in application cache
* Notifications on payload updates can be set to text tiles

### Dark mode and material dynamic colors
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/417e1096-6c01-4424-bb48-4394c8597960" width="200" />
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/4a9ce836-16bb-4c78-bf25-c9766fa99de6" width="200" />
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/9cd6a77f-778c-49c0-ad78-279807aeb25d" width="200" />
<img src="https://github.com/burachevsky/MQTTHub/assets/60154717/060dbe00-f7eb-4583-becd-c44488f2935b" width="200" />

## Development
App written in Kotlin

### App architecture
* MVVM
* Clean architecture
* Multiple modules (arch layers, features, utils)

### Libraries 
* [**Dagger 2**](https://github.com/google/dagger) - DI
* [**Coroutines & Flow**](https://github.com/Kotlin/kotlinx.coroutines) - Asynchronous programming
* [**Room**](https://developer.android.com/jetpack/androidx/releases/room) - Data persistance
* [**Gson**](https://github.com/google/gson) - Json parsing
* [**Eclipse Paho Java Client**](https://github.com/eclipse/paho.mqtt.java) - MQTT client implementation
* [**Navigation component**](https://developer.android.com/jetpack/androidx/releases/navigation) - App navigation
* [**androidx**](https://github.com/androidx/androidx) - architecture and UI components
* [**Material**](https://github.com/material-components/material-components-android) - Design + UI components
* [**MPAndroidChart**](https://github.com/PhilJay/MPAndroidChart), [**RotateLayout**](https://github.com/rongi/rotate-layout) - For chart tile
* [**Timber**](https://github.com/JakeWharton/timber) - logging

## Contacts
Telegram - https://t.me/Burachevsky (feel free to dm me if you have any questions or suggestions)

E-Mail - burachevsky616@gmail.com

