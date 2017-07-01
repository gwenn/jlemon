./jlemon -DSQLITE_ENABLE_UPDATE_DELETE_LIMIT src/main/java/org/sqlite/parser/parse.y
cpp -P src/main/java/org/sqlite/parser/parse.j > src/main/java/org/sqlite/parser/yyParser.java
