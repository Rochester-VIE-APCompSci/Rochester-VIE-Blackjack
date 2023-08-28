
# Prereqs

Building this projects requires Apache Maven (https://maven.apache.org). Maven can be downloaded separately and run from the command line, or inside of an IDE like Eclipse or IntelliJ.

LibreOffice is also required for editing the .odt (and doc) files in this project, and for generating PDF versions for students at build time.

# Building

In Eclipse, a launch configuration is provided in the `eclipse-dev` folder. It shoudl appear as `Rochester-VIE-Blackjack (clean,package)` in the Run drop down as a favorite, or you can run it from the 'Run Configurations' dialog.

From the command line, to compile the code and builds the student zip. You can invoke maven via its native mvn command or via make with the Makefile provided by this project.

```bash
cd {VIE-workspace-dir}/Rochester-VIE-Blackjack
mvn clean package
#    or
make
```

Once complete the `target` directory will contain a file named `student-{version}.zip`. 

If `soffice` is not found, you either don't have LibreOffice installed, or it is not in your path. 

For Eclipse, copy the launch configuration (make it a local configuration, dont' share it) and add the `exe.soffice` property name with a value of the fully qualified path of the soffice executable.

For command line run

```bash
mvn clean package -Dexe.soffice={path}
```

# Publishing internally

If you build a zip you want to distribute within IBM, run the maven build. Ensure the zip file has `SNAPSHOT` in the name, which is Maven's way of saying this is a development build. Update the version in pom.xml if it doesn't. 

Make sure the default player implementation runs in Dr. Java.

Post your build in the appropriate competition year subfolder in box under [Rochester VIE AP Comp Sci](https://ibm.ent.box.com/folder/7663865070) (contact Kyle, Paul or Jeff for access).



# Publishing for students

Update the version in pom.xml to remove `-SNAPSHOT`. Run the Maven build. Make sure the generated zip file name matches what is in the `doc/BlackjackProjectGuide.odt` document. Update pom.xml or the document if necessary and build again.

Make sure the default player implementation runs in Dr. Java.

Place the zip file in box at https://ibm.ent.box.com/file/450845639621.
The underlying file will need to be renamed to remove `-SNAPSHOT`. This link was shared with the teachers as a test as we were wrapping up development.

Student tables in the UI are controlled by `com.ibm.vie.blackjack.casino.config/defaultConfig.json`

# Grading/scoring scripts

Judging should use `com.ibm.vie.blackjack.judging.config/allCompetitionTables.json`. This file is not shipped to students and has the secret tables we use for scoring in it. The student tables also need to be in sync with the contents of `com.ibm.vie.blackjack.casino.config/defaultConfig.json`.

Run the scoring framework with (modify the paths as appropriate):

```bash
java -jar blackjack.jar -c ../../allCompetitionTables.json -r ../../results.csv -d ../../solutions/
```

There is also an Eclipse run configuration `Judging (Mayo sample).launch` provided that assumes you create a `judging` directory in the same parent directory as `Rochester-VIE-Blackjack`. Create the following directory structure under `judging`:

- `solutions`: Submissions from students/teachers
	- Subdirectories should be named for each school/group (Mayo, Century, JM, Teachers, IBM)
- `results`: Where the framework will put a `results.csv` for this scoring run.
	- Subdirectories should be named for each school/group (Mayo, Century, JM, Teachers, IBM) 

The launch configuration also assumes you've run the Maven build to create `target/blackjack.jar`.

The allCompetitionTables.json in this example defines both the rules of the game and the table configurations. We must be careful that these match what we tell the students we will evaluate against. (e.g. that the rules match the rules used by the UI, and the given tables are in the config) The file is in git, but it still needs to be specified to allow for changes outside of java-code and/or UI.


The path for each Jar is included as a column in the output csv.  A sample path might look like this:
```/home/ntl/blackjack/blackjack_2019/lib/../../solutions/JarUploads/Mayo/Mayo-Hamza-Mohamed.jar```

The R-script (Nick Lawrence, Rebecca Dahlman) and extracts the school name from the path. When we create the directory tree for jars this year, we should include the class name/id as a directory so that we can differentiate that when we build the R-dataframe.

TODO get the R scripts in source control

TODO `scripts` directory.