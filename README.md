NGG2SVM
=======

**ngg2svm** is a program that combines the power of the n-gram graphs (NGGs) with the power of 
support vector machines (SVMs).
The program takes as input a set of labeled text files and creates a precomputed kernel matrix file,
usable by LibSVM.

ngg2svm relies on the JInsect framework to perform the following:
- extract 3-gram graphs, with a neighbourhood distance of 3, per input text.
- compare all pairs of input (training) texts with each other using Value Similarity (VS)
- extract a precomputed kernel file, usable by LibSVM as training, allowing
to perform cross-validation.


Syntax
------
>java -jar ngg2svm.jar inputDir/ [outputFile.txt]

###Parameters
- **inputDir/**
This parameter points to a directory where the text files are located.
The text files are supposed to be contained in subdirectories, indicating the label.
E.g. if we have "spam" and "ham" as classes, the tool will expect a "spam" subdirectory
and a "ham" subdirectory. Each directory will contain a set of files, e.g. ham1.txt, ham2.txt
in "ham", and spam1.txt, spam2.txt in "spam".
- **outputFile.txt (optional)**
This paramter allows the user to define the name of
the precomputed kernel matrix file.

Next steps
----------
- Allow train/test splitting
- Support different n-gram sizes and D parameters

References
==========
Check the [JInsect](http://sourceforge.net/projects/jinsect) project for more information
on n-gram graphs.
Check the [LibSVM](http://www.csie.ntu.edu.tw/~cjlin/libsvm/) page for more information on LibSVM and on how to use the 
precomputed kernel matrix file.
