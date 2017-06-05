package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class CreateTrigger implements Stmt {
	public final boolean temporary;
	public final boolean ifNotExists;
	public final QualifiedName triggerName;
	public final TriggerTime time;
	public final TriggerEvent event;
	public final List<String> colNames; // only for event = UpdateOf
	public final QualifiedName tblName;
	public final boolean forEachRow;
	public final Expr whenClause;
	public final List<TriggerCmd> commands;

	public CreateTrigger(boolean temporary,
			boolean ifNotExists,
			QualifiedName triggerName,
			TriggerTime time,
			TriggerEvent event,
			List<String> colNames,
			QualifiedName tblName,
			boolean forEachRow,
			Expr whenClause,
			List<TriggerCmd> commands) {
		this.temporary = temporary;
		this.ifNotExists = ifNotExists;
		this.triggerName = requireNonNull(triggerName);
		this.time = time;
		this.event = requireNonNull(event);
		this.colNames = colNames;
		this.tblName = requireNonNull(tblName);
		this.forEachRow = forEachRow;
		this.whenClause = whenClause;
		this.commands = requireNotEmpty(commands);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("CREATE ");
		if (temporary) {
			a.append("TEMP ");
		}
		a.append("TRIGGER ");
		if (ifNotExists) {
			a.append("IF NOT EXISTS ");
		}
		triggerName.toSql(a);
		if (time != null) {
			a.append(' ');
			time.toSql(a);
		}
		a.append(' ');
		event.toSql(a);
		if (TriggerEvent.UpdateOf == event) {
			for (int i = 0; i < colNames.size(); i++) {
				if (i == 0) {
					a.append(' ');
				} else {
					a.append(", ");
				}
				doubleQuote(a, colNames.get(i));
			}
		}
		a.append(" ON ");
		tblName.toSql(a);
		if (forEachRow) {
			a.append(" FOR EACH ROW");
		}
		if (whenClause != null) {
			a.append(" WHEN ");
			whenClause.toSql(a);
		}
		a.append(" BEGIN\n");
		for (int i = 0; i < commands.size(); i++) {
			if (i != 0) {
				a.append("\n");
			}
			commands.get(i).toSql(a);
			a.append(';');
		}
		a.append("END");
	}
}