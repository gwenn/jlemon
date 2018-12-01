cl /O2 /Fejlemon.exe lemon.c
jlemon.exe -q -DSQLITE_ENABLE_UPDATE_DELETE_LIMIT src\main\java\org\sqlite\parser\parse.y
cl /EP /C src\main\java\org\sqlite\parser\parse.j > src\main\java\org\sqlite\parser\yyParser.java
del src\main\java\org\sqlite\parser\parse.j
