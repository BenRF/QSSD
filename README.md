
# Querying Semi-Structured Data
This project focuses on automating the process of joining tables from various semi-structured file formats, the created solution can:
* Detect tables inside user imported files.
* Find potential problem detection within each table.
* Generate expression to summarize data inside each column.
* Recognize table relations based on overlapping content or similar column headers.
* Perform an outer join on selected tables.
* Allow the user to override these automated decisions.

![alt text][overview]

[overview]: https://github.com/BenRF/QSSD/blob/master/Screenshots/overview.png "Both tabs of the program"

## Getting Started

### Prerequisites
- Java development kit 8 or higher must be installed on the machine that the program is going to be run on
- Microsoft Excel is not required to run the program but will be needed to edit and view the files this program works with.
- The built version of the program is available as a .jar executable

### Valid file formats
All imported files can contain any number of one-dimensional tables of any size provided all tables have a minimum of one empty row/column between it and any adjacent content.
* .xlsx: Microsoft Excels current main format, content can be any style, inside any sheet and does not have to be specified inside Excel as a table (however doing so will guarantee its detection)
* .csv: Comma-separated values Can read and understand tables that are placed anywhere inside each file.

## Importing a file
<img src="https://github.com/BenRF/QSSD/blob/master/Screenshots/importing.png" width="300"/>

When running the program you will be presented with the importing panel, from here you can import files, select which tables you would like to merge and check each table for problems.

* To import tables from a file select the "Add file" button and navigate to the file that contains the desired tables.
* Once added all the tables found in that file will be presented in summary form, to view the table and it is content double click on the summary table.
* Each table is assigned a temporary name used to identify it in the merging tab.
* Summary information is shown about each column, the first row shows if the program detected the column to be unique or if every value in that column is a single datatype.
* The second row of each column displays a generated Expression of the data, this summarises any patterns of how the data is made up of integers, letters, spaces and symbols.
* If a problem was detected within that table a short description of what the problem was and which columns it occurred in will be shown underneath the table. The cells which were identified as a problem will be highlighted in the full table window.

## Merging tables
<img src="https://github.com/BenRF/QSSD/blob/master/Screenshots/merging.png" width="300"/>

Once a minimum of two tables has been imported the merging panel becomes available to select, by navigating to the merging panel the program will automatically order the tables, detect relations between tables and generate the resulting table. 

* Each step of the merge process is shown to the user and is accessible through its corresponding button, which will display the temporary name assigned to the table being added in that step.
* At each step you can see the table before the merge (top) and the table it is being merged with (middle) and the result of that merge (bottom).
* The links used to join together the tables are displayed as visible lines drawn between the corresponding columns.
* Links can be deleted by clicking on the line representing it.
* New links can be assigned by clicking near a column and then dragging and dropping the cursor near the column it should be linked to.
* When creating or removing links all further steps of the program will be re-evaluated before any further changes can be made.
* The result table of the currently viewed step can be saved by filling out the form at the bottom of the panel. To save the full result the panel has to be displaying the final step of the merge.

## License

* This project was created with the use of Apache POI which are available under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
