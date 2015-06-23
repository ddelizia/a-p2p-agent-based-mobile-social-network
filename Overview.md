# Overview #

## Main functionalities: ##
  * Discovery new users on the network
  * Manage friends
  * Manage personal wall
  * Manage personal profile
  * View friend's wall
  * View user's profile
  * Search content on friends nodes

## Application Architecture ##
  * The logical comunication is between devices
  * Fisically the agents run in the JADE container
  * Manager listen at the container events and informs the devices (Joins/Lefts)
  * All the others functionalities (search, friend management, friend discovery...) are implemented on the device side

![http://a-p2p-agent-based-mobile-social-network.googlecode.com/files/apabmsn_en_mvc_architecture.png](http://a-p2p-agent-based-mobile-social-network.googlecode.com/files/apabmsn_en_mvc_architecture.png)

## LogIn and LogOut ##
  * Subscription to the server manager
  * Server manager informs the client if a user join/leave the network
  * Device keeps track of the users on the network

## Friends Management ##
  * To be a friend of mine i have to accept you
  * I can delete a user from my friend list
  * The friends list has to be up to date on both devices (case that one is disconnected)
  * A user can watch the friend's profile and the friend's wall
  * I can download content from a friend (on request)
  * I can do a search between friends content

## User Profile ##
  * Basic user profile with an image, name and surname
  * Profile is public so every user can see it
  * Every agent mantain his profile

## Wall Requests ##
  * User request a wall from a friend
  * Attached file is not sent on wall request
  * User can download a single file
  * File is divided in parts (Due to ACLMessage content length limits) and sent to the requester
  * Requester keeps track of the parts and merge the parts

## Search Content ##
  * Search content based on taxonomies (tags)
  * Broadcast search to friends
  * Search is implemented in the destination request node
  * Asynchronous reception of notifications
  * Download attached content file

## Notification Area ##
  * When you receive a message/content you got a notification
  * Clicking on the notification you can see the content
  * Once you see the message/content the data are deleted (due to save memory)
  * If you want to see the content again you have to download it again