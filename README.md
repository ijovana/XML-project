- Java 1.8
- Tomee 1.7.4 plus

Settings:

1.	Configure build.properties
	-	Set path to the Tomee dir
2.	Eclipse
	-	Right click on project
	-	Build path / Configure Build path
	-	Add library
	-	User library, Next, User libraries, New (name for example "Tomee")
	-	Select previously created user library, Add External Jars (add libraries from Tomee lib folder)
	-	Confirm and add created user library to the project
	
3.	Ant - for compiling..
	-	Window
	-	Preferences
	-	Ant
	-	Runtime
	-	Select Global entries
	-	Add External jars (add libraries from Tomee lib folder)
	- 	Apply
