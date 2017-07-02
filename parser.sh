DIR=${1:-$(dirname "$0")}
echo $DIR
$DIR/jlemon -DSQLITE_ENABLE_UPDATE_DELETE_LIMIT $DIR/src/main/java/org/sqlite/parser/parse.y
cpp -P $DIR/src/main/java/org/sqlite/parser/parse.j > $DIR/src/main/java/org/sqlite/parser/yyParser.java
