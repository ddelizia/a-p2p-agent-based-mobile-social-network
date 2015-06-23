# JAMPS Architecture #
JAMPS has been implemented for Java 2 Micro Edition, and using the Jade-LEAP Framework it was possible to achieve the agent architecture.

The system enable users, and consequently agents, to move trough different areas. The Jade containers are used to make easy the transition between areas. Figure III.1 shows the network topology. Each zone should have the following components:

  * One or more routers to define the local area
  * A Jade Container that hosts the agents  in the area
  * An agent manager that manages the connections and disconnections in the area
  * A set of MSNAgents installed on each device that communicate with both the Manager, (for accessing  the network), and the others MSNAgents (installed on other devices).
  * A DNS service where the Jade container IP address is mapped into a domain name (it must be the same domain name in all areas)

During connection and disconnection, the MSNAgent and the Manager must be able to communicate with each other to exchange information about connected users in the area. The task of the Manager is to warn MSNAgents with JOINED (logging into the social network) and LEFT (logging out from the social network) messages about the users connected in the area. During the disconnection, when an agent leave the container, the AMS agent generates a "DEADAGENT" event. The Manager catch this event and it sends to the other agents the LEFT message with the name of the user that has left the network.


All the other communications take place between the MSNAgents. A MessageTicket is used to interchange information. Every message is splitted by the sender in packages of 100KB due to ACL content limitations, the receiver is responsable for joining the packages to create the final message. A message ticket identify a package. A MessageTicket consists of 5 frames:

  1. RequestOrigin: contains the name of the agent who makes the request.
  1. Type: contains the conversation type.
  1. TextContent: contains the text content.
  1. SequenceNumber: this frame indicates the sequence number of the package.
  1. TotalPackets: contains the total number of packets that compose the complete message.
  1. DataContent: contains the file to send, it is encoded in base64 and inserted into this frame.

JAMPS consists of two parts:
  * Server: This is the application that contains the Agent Manager.
  * Client: is the application that runs on the device, each instance of the client contains a MSNAgent. Each application instance is responsible to manage their own contents and make them available to other agents. The client application, as shown in Figure III.2, is divided into 3 layers:

## Model Layer ##
The Model Layer provides methods for accessing application database. Basically it transforms the RecordStore inro Java beans.

## View  Layer ##
The View Layer provides the user interface. The component of this level are:
  * Form Main. It is the application home page, it provides links to all the various application features, and also it contains a search field to search content on the network.
  * Friend Form. It contains the list of friends, it provides commands to request: the user profile, the wall, and the deletion of friendship.
  * Discovery Form. This interface contains the list of all users connected into the area. By selecting the desired user it is possible to send a friend request or a profile request.
  * Profile Form. It provides the interface for viewing and editing the user profile.
  * Insert Wall Form. It provides user interface for publishing new content into the social network. Each content is composed by: a text message, a list of tags separated by "," and the link to the shared file.
  * Wall Form. It is the interface for displaying the posts on the wall.
  * File System List. Provides a graphical interface for accessing the file system to find the content to share in the social network, and to find the image for the profile picture.
  * Notification List. Contains a the notifications list.

## Controller  Layer ##
The Controller layer represents the application controller. It controls the Model and the View Layers. It contains also the MSNAgent that represents the application core, since then  the agent initiates and maintains the status of the various components of this layer. Other important components in the architecture are the Data Managers, the only components that deal with the Model Layer.


To receive instructions from the user, the MSNAgent uses the GUI Manager to listen to the commands executed by the user and then it informs the agent about what to do. The GUI Manager have also the function to notify the user the notifications provided by the Notification component. The GUI Manager is the only interface between the View Layer and the  Controller level.

The tasks assigned to agents are defined as Behavior. In  JAMPS architecture each MSNAgent has three main Behaviors:
  * Behavior Access manages the information received from the Manager, maintaining updated the users list. This is an action that needs to be done cyclically, this class extends CyclicBehavior.
  * Send Behavior is responsible for splitting the messages in packages and send them to the other users. Being a punctual operation, performed only on request, this class extends OneShotBehavior.
  * Receive Behavior handles messages received from other users in the network and depending on the type of message delegates the management to a sub-Behavior. It is the responsible for joining the packages. The process of receiving messages must always be active this is the reason why it extends the class CyclicBehavior, while the sub-Behaviors are punctual operations which extend the class OneShotBehavior. The sub-behaviors are:
    * Friends Behavior manages friendship requests and responses.
    * Behavior Profile takes care of sending the user profile when a request is received, it also sends a notification to the Notification component.
    * Search Behavior manages the requests received from friends initializing the search procedure and if there is a result it is sent to the requester.
    * Wall Behavior handles wall requests to display the contents of the wall, it monitors if the requests are sent only by friends and in this case it sends the wall to the requester.
    * File Behavior handles download requests and responses.
    * Error Behavior manages all the others requests not covered by the others behaviors. An error message is sent to the requester.

This behavior-based structure allows the application to create easily new functionalities by simply creating new sub-behaviors without modifying existing ones.