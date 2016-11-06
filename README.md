# QProber
Contributors of this project in no particular order
- Gaurav Mishra ([gm2715@columbia.edu](mailto:gm2715@columbia.edu))
- Plaban Mohanty ([pm2878@columbia.edu](mailto:pm2878@columbia.edu))

### Objective
To implement the query-probing classification algorithm as described in the QProber paper. 
In the first part, we intend to classify a web database to find out which category from among a fixed set of categories does it fall in. 
In the next part, we implement the process of creating a content summary of the database for distributed search over web databases.

Input: Bing API key, Target Specificity, Target Precision, DB

Mechanism:

Part 1 -Web Database Classification:
This part implements the web database classification by taking as input the threshold coverage and specificity values as well 
as the database to be classified and the Bing API key.
Initially, we build a tree structure with the given classes, 
child classes and the query words given to establish the hierarchy. We have a Category class containing category name, 
its children and the queries associated with it. We populate the queries from txt files from the files given in project description.

A recursive function classify is then called for nodes starting with root. 
For each node processed using this function, we check if the node has any children, if it doesn’t, 
then we add this to the final list of classes. If it does, then we get the list of all its 
child nodes which are valid i.e. which satisfy the threshold coverage and specificity criteria. 
For each of these children, we recursively call this function. If there are no valid children, 
then we add the node itself to the final list of classes.
As can be seen from this algorithm, a node is processed by the function only if it satisfies the 
threshold conditions or if it is a root. In both the cases, we add it to the final list and check if its children are valid or not.
Process to check validity of children nodes:
To check validity of nodes, we get the query set of the parent node. We then check if the query results exist in cache. 
If it does, then get the count from cache, else use BING API to get the no. of matches and then add them in the cache. 
We add the count in total count for that category and add in the total count overall. 
We then compute the coverage and specificity of the database for that category:
Coverage(D, Ci) = number of D documents in category Ci. 
Specificity(D, Ci) = Coverage(D, Ci) /|D| ,
We check if the specificity and coverage lie within the threshold and if they do, we add the child node to the list. 
On completion of processing of all the child nodes, we now have a list of all valid nodes that we return to the above function.
For each classification path for the database that we classify correctly, we call the second part for sample documentation.


The class path received from part 1 will be of the form – “Root/Health/Fitness”. So here there are three categories in hierarchical order. Root -> Health -> Fitness
This process is carried out in several modules, each part handling a separate task. The modules used are:
- Generate Content Summary :
For each category that is not the leaf category, extract the queries for that category. For each query, extract the top four URLs from Bing API. For each of those top four URLs, use the run lynx function to extract the words from the documents. We check to ensure that the duplicate entries are not used. For the extracted words, the function uses a map to update the count of words occurring in a document. Hence at the end of the loop, we have the count of no of docs containing that word in the map value set, with key set being the words occurring in the documents. We then publish this to a file for the category and database.
After each node has been processed, its parent node is called (If it exists).Now since we process the sub categories first and do not reset the map, the parent nodes or the parent classes also contain the words and their counts from the documents processed by the children classes. Hence this satisfies the requirement of the sample of the parent class containing all the children classes document sample as well.
For multiple classification of a single database, while creating content summary for parent class, we append the entries for the new words and update the count for words already in the document. For example, if a database is classified into Root/Health/Fitness as well as Root/Sports, then while processing the second-class Root/Sports,we do not overwrite the root file but rather update the root file by adding new words and their counts and updating the count of old words.
- Create Output File :
ThemapcreatedbygenerateContentSummary functionispassedalongwiththedatabaseandclassnameand an output text file is generated. If the file already exists, then we append the new words of the map in the file and update the count of existing words in the file .This gives a aggregated content summary of the database. The generated file is in the format : Category-Databasename.txt
Additional Details:
1. Cache system was implemented to make queries more efficient only for classification.

### Class Diagram

![alt tag](https://cloud.githubusercontent.com/assets/5005160/20040762/3f10ef22-a42b-11e6-8c7d-8d54d058bb66.jpg "Class Diagram")
