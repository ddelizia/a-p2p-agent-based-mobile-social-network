# Run the Application #

## Run the Manager (from the msn folder) ##
```
java -cp jade.jar;msnJ2se.jar jade.Boot -gui -nomtp manager:msn.manager.ManagerAgent
```

## Run the Client (on the emulator) ##
```
emulator_path\emulator.exe -Xdescriptor:msn_folder_path\msn.jad
```

## Run the Client (on the device) ##
If you are not running the mobile application in the same computer you need to replace the line
```
LEAP-host: localhost
```
with
```
LEAP-host: your_ip
```
in the file
```
msn.manifest
```

## Start the application JADE-Msn insert the name and start to use it!! ##